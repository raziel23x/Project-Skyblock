# Backend Milestone 7 — Machine Framework Core

## Purpose

Provide reusable machine lifecycle, state, behavior, scheduler participation, identity, and diagnostics.

## Implemented Contracts

- `MachineId`
- `MachineActivity`
- `MachineState`
- `MachineLogic`
- `MachineParticipant`
- `MachineDiagnostics`

## Architectural Guarantees

1. Machine state and machine behavior are separate.
2. MachineParticipant is the scheduler-facing adapter.
3. The framework is Minecraft-independent.
4. Recipes, GUIs, inventories, and capabilities remain outside the core.

## Ownership Boundary

All contracts introduced by this milestone live in the backend simulation layer. Minecraft, NeoForge, block entities, capabilities, menus, screens, packets, and production gameplay are not authoritative owners.

## Validation

The milestone was compiled and runtime-tested before commit. Existing Research Era gameplay remained operational because production integration was deliberately deferred.

## Deliberately Deferred

- machine components
- persistence adapters
- production machines

## Completion Status

Completed and committed. Later milestones may extend these contracts but must preserve the ownership and dependency boundaries documented here.
