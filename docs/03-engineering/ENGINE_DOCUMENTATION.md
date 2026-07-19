# Project Skyblock Engine Manual

## Purpose

The Project Skyblock engine is a Minecraft-independent simulation backend for a progression-focused NeoForge mod. It provides deterministic state, bounded execution, sleeping systems, resource ownership, networks, and reusable machine composition.

## Architectural Layers

```text
Game rules and production content
            |
Machine logic and reusable components
            |
Simulation systems: energy, thermal, networks
            |
Scheduler, state contracts, budgets, dirty tracking
            |
Minecraft / NeoForge adapters and persistence
```

The arrows represent dependency direction. Backend systems do not depend on Minecraft.

## Simulation Core

The core package defines:

- `SimulationState`: marker for authoritative backend state
- `SimulationParticipant`: executable scheduler-facing contract
- `SimulationLifecycle`: sleeping, ready, scheduled, active, blocked, and invalid states
- `SimulationBudget`: bounded work allowance
- `SimulationContext`: deterministic execution context
- `SimulationResult`: explicit outcome of one execution
- `SimulationScheduler`: wake, sleep, schedule, and bounded execution
- `DirtyStateTracker`: records persistence, client-sync, and scheduler work

## Scheduler Model

Participants do not own permanent tick loops.

```text
state change or due time
          |
          v
        wake
          |
          v
ready / scheduled participant
          |
          v
bounded execution
          |
          +--> sleep
          +--> schedule
          +--> block
          +--> invalidate
```

Generation numbers invalidate stale queue entries. Stable participant identifiers provide deterministic ordering where due times match.

## Thermal System

Thermal state is backend-owned and represented independently of Minecraft fluids or blocks. The thermal engine supports explicit state, properties, environment, transfer, conditions, constants, and diagnostics.

Future machines may use thermal state for operating ranges, heat exchange, boiling, condensation, efficiency, shutdown, and reactions.

## Energy System

`SimulationEnergyState` owns stored energy. Limits, requests, transfers, results, and diagnostics are simulation contracts. NeoForge Energy will later expose this state through an adapter.

## Energy Networks

Network work is split deliberately:

- topology owns nodes, connections, and deterministic routes;
- transfer moves energy across a selected route;
- runtime state records activity;
- a participant connects the network to the scheduler;
- idle networks sleep.

Fair multi-transfer distribution and shared-edge contention remain deferred until a milestone requires them.

## Machine Framework

The machine framework separates:

- `MachineState`: authoritative typed machine data and lifecycle bookkeeping
- `MachineLogic`: machine-specific backend behavior
- `MachineParticipant`: scheduler adapter
- `MachineDiagnostics`: immutable observability
- `MachineId` and `MachineActivity`: stable identity and activity vocabulary

The framework contains no block entities, menus, rendering, or capabilities.

## Machine Components

Implemented:

- `MachineEnergyComponent`
- `MachineInventoryComponent`
- `MachineThermalComponent`

Planned:

- `MachineFluidComponent`
- `MachineGasComponent`
- `MachineProcessingComponent`

Components own or delegate authoritative state, enforce local access rules, produce diagnostics, mark dirty categories, and wake their owner after meaningful changes.

### Machine Inventory Model

`SimulationItemKey` represents stable item identity using a validated `namespace:path` value. `SimulationItemStack` is an immutable quantity value with an explicit maximum stack size. `MachineInventoryComponent` owns slot contents and applies slot capacity, access, insertion-rule, merge, restoration, dirty-state, and wake behavior.

External adapters use `insert` and `extract`; machine logic uses `store` and `consume`. Minecraft `ItemStack`, registry lookup, NBT, and `IItemHandler` remain outside the authoritative backend. Immutable slot snapshots provide the handoff point for persistence and diagnostics adapters.


### Machine Thermal Model

`MachineThermalComponent` composes the existing thermal core for machine ownership. `ThermalState` remains the authoritative fixed-point energy store, `ThermalProperties` derives operating and shutdown conditions, and `ThermalEngine` performs bounded environmental exchange.

External adapters use `receiveHeat` and `extractHeat`; machine logic uses `generateHeat` and `consumeHeat`. `step` applies generated process heat and one ambient exchange as a single machine change. Immutable snapshots expose persistence and synchronization values without introducing block entities, biome lookup, fluids, or NeoForge capabilities into the backend.

## Integration Boundary

Minecraft-facing code may:

- construct and hold simulation objects;
- translate world events into backend events;
- expose capabilities;
- save and restore validated state;
- synchronize immutable views;
- render or present diagnostics.

Minecraft-facing code must not:

- duplicate authoritative resource amounts;
- create independent machine lifecycle rules;
- bypass backend limits;
- introduce unbounded polling;
- mutate state from the client.

## Current Completion Point

Milestones 1 through 10 are implemented. The engine now has reusable machine-owned energy, inventory, and thermal components. The next milestone will prove that these components compose cleanly inside one machine state and machine logic implementation.

## Long-Term Expansion

The engine is expected to support fluids and gases as backend-owned resources. Fluids may model amount, capacity, flow, temperature, quality, and controlled mixing. Gases may model amount, volume, pressure, temperature, flow, and containment. These systems will be added only when concrete milestones justify their contracts.
