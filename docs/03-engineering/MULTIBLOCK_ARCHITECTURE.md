# Modular Multiblock Architecture

> Status: **Approved design direction; implementation deferred until a real machine requires it**

## Purpose

Multiblocks represent physical scale, structure, and replaceable infrastructure. They are not used
merely because a large structure looks impressive.

A multiblock must provide gameplay that a single block cannot express as clearly, such as capacity,
thermal control, ventilation, pressure containment, modular storage, parallel processing, or
serviceable physical upgrades.

## Core Rules

1. **The controller is the stable boundary.** External automation, capabilities, persistence bridges,
   and player interaction communicate with one controller-owned machine runtime.
2. **The simulation owns machine state.** Minecraft blocks describe structure and expose adapters;
   they do not independently own authoritative inventories, energy, fluids, gases, temperature, or
   processing state.
3. **Structure produces composition.** Member blocks contribute typed modules or properties. The
   controller derives the active machine composition from the validated structure.
4. **Upgrades are physical and meaningful.** Replacing heating, ventilation, containment, storage,
   transfer, or processing modules changes understandable capabilities rather than only increasing
   an arbitrary tier number.
5. **External interfaces remain stable.** Reforming or upgrading a valid structure may change
   capacity and performance without requiring automation systems to discover a different machine.
6. **Formation and teardown are transactional.** A structure either commits a valid composition or
   leaves the previous valid state intact. Partial scans must not destroy state.
7. **Breaking is recoverable.** Invalidating a structure must not void inventories, fluids, gases,
   energy, progress, or ownership records. Reformation restores valid simulation state according to
   explicit rules.
8. **Work is bounded and event-driven.** Structure checks occur on relevant block or chunk events,
   with bounded validation work. Multiblocks do not perform unbounded scans every tick.
9. **Chunk lifecycle is explicit.** Formation, partial loading, unload, reload, controller removal,
   member removal, and corrupted-state recovery require documented behavior and tests.
10. **No duplicate authority.** Member blocks may cache adapter-facing information, but only the
    controller-owned simulation runtime mutates authoritative machine resources.

## Controller Responsibilities

The controller:

- identifies candidate members;
- validates shape and module compatibility;
- computes a deterministic composition;
- owns or locates the authoritative machine runtime;
- exposes stable sided capabilities and automation contracts;
- updates composition when relevant structure changes occur;
- preserves state across valid upgrades, invalidation, unload, and reformation;
- reports clear formation and failure diagnostics to players.

## Member Responsibilities

A member block:

- declares its structural role and typed contribution;
- provides location and orientation evidence;
- participates in bounded validation;
- exposes no independent duplicate machine state;
- notifies the controller when its relevant state changes.

Examples of contributions include heating capacity, heat rejection, tank volume, gas containment,
input or output ports, inventory capacity, pressure tolerance, catalysts, processing chambers, and
parallel-operation slots.

## Formation Model

Formation should follow a staged process:

1. detect a relevant structure change;
2. collect bounded candidate evidence;
3. validate required roles and constraints;
4. build a proposed deterministic composition;
5. verify that existing state can be preserved or safely normalized;
6. atomically commit the new composition;
7. invalidate and refresh affected adapters;
8. wake the scheduler only when the new state permits work.

## Automation Contract

External automation targets the controller's stable ports. Sided rules and transfer limits are
computed from the active composition.

When modules change:

- existing capability references must be invalidated through the platform adapter correctly;
- newly queried capabilities reflect the new composition;
- stored resources remain simulation-owned;
- automation never communicates directly with arbitrary internal member blocks unless a documented
  port block delegates to the controller.

## Required Tests Before Production Use

- form a valid structure;
- reject invalid structures with useful diagnostics;
- remove and replace every module category;
- preserve state during valid upgrades;
- invalidate without resource loss;
- reform after invalidation;
- unload and reload across chunk boundaries;
- break and replace the controller;
- connect automation before and after composition changes;
- load corrupted or obsolete persisted structure data;
- stress many idle and changing structures without recurring unbounded scans.
