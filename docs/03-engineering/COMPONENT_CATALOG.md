# Machine Component Catalog

## Component Contract

A reusable machine component should:

- own or delegate authoritative backend state;
- enforce component-local invariants and access rules;
- remain independent of Minecraft and NeoForge;
- expose immutable diagnostics;
- mark the correct dirty categories after meaningful change;
- wake the owning participant when new work may be possible;
- avoid knowledge of menus, rendering, packets, and recipes.

## MachineEnergyComponent

**Status:** Implemented in Milestone 8.

Owns a `SimulationEnergyState` relationship and controls external receive/extract and internal
produce/consume operations. It enforces capacity, explicit per-operation limits, and access mode.
Aggregate shared-edge or per-simulation-step budgets remain network concerns. Changes mark
persistence, client sync, and scheduler dirty state and signal the owner to wake.

## MachineInventoryComponent

**Status:** Implemented in Milestone 9.

Owns authoritative Minecraft-independent slot contents using `SimulationItemKey`, opaque
`SimulationItemState`, and `SimulationItemStack`. Adapter-owned state participates in identity so
damaged, enchanted, or otherwise component-bearing items cannot silently merge as stateless
items. The canonical typed/component payload is immutable, capped at 64 KiB, and may not use NBT
or SNBT as a runtime encoding. Each slot has explicit capacity, external access, and an insertion rule. External
`insert`/`extract` operations are separated from internal `store`/`consume` operations. The
component validates restored state, exposes immutable slot snapshots and diagnostics, marks
persistence/client-sync/scheduler dirty state, and wakes its owner after meaningful changes.

Milestone 19A adds optimistic atomic transactions, stale-candidate rejection, one-wake multi-slot
commit, and explicit automation/menu views. Minecraft `ItemStack` conversion uses a canonical,
registry-aware data-component persistence payload at the platform boundary. The payload uses sorted
compact JSON and namespaced registry keys rather than unstable numeric network IDs. The NeoForge
item handler owns no slot state and converts staged extraction before committing, so undecodable
state is never deleted.

## MachineFluidComponent

**Status:** Planned.

Expected to support capacity, amount, flow limits, temperature, and optional quality or mixture metadata where gameplay requires it. Minecraft fluid capabilities will be adapters.

## MachineGasComponent

**Status:** Planned.

Expected to support amount, volume, pressure, temperature, flow, and containment. The exact abstraction will be proven by a concrete science or industrial milestone.

## MachineThermalComponent

**Status:** Implemented in Milestone 10.

Wraps authoritative `ThermalState`, shared `ThermalProperties`, and `ThermalEngine` behavior for machine ownership. It separates external heat input/output from internal generation/cooling, exposes operating and shutdown conditions, performs bounded environmental exchange, validates restored thermal energy, produces immutable snapshots and diagnostics, marks persistence/client-sync/scheduler dirty state, and wakes its owner after meaningful changes. Minecraft environments and heat capabilities remain adapter concerns.

## MachineProcessingComponent

**Status:** Implemented in Milestone 13.

Owns the recipe-independent lifecycle of one active operation: idle, running, blocked, and ready to complete. It tracks stable process identity, bounded progress, blocking reasons, completed-operation counts, validated restoration, immutable diagnostics, and shared dirty-state ownership. Ordinary progress avoids redundant scheduler wakes; lifecycle transitions wake through the composed runtime. Recipe matching, resource transactions, energy policy, thermal requirements, and output insertion remain explicit collaborators rather than hidden component behavior.

## Machine Composition Runtime

**Status:** Implemented in Milestone 12.

`MachineRuntime` composes the implemented energy, inventory, thermal, combustion, and processing components with typed machine state and scheduler participation. All components share one externally registered `DirtyStateTracker`; components with externally triggered work use the runtime's coalesced wake signal, while internal combustion progress avoids redundant wake requests. `MachineComponentState`, `MachineComponentDiagnostics`, and `MachineRuntimeDiagnostics` provide ownership and observability without introducing platform dependencies.

## Machine Combustion Component

**Status:** Candidate in Milestone 19B1.

`MachineCombustionComponent` owns normalized remaining and total burn work for machines that consume
discrete fuels. It deliberately does not inspect Minecraft items, recipes, burn-time APIs, or
container remainders. Runtime ignition and consumption mark persistence and client synchronization
without issuing a redundant scheduler wake because those mutations occur inside an active machine
execution. Snapshot schema 3 persists this state, while older schemas restore an empty reservoir.

## Machine Runtime Persistence Bridge

- `MachineRuntimeSnapshot` is the format-neutral durable state contract.
- `MachineRuntimePersistence` validates the complete candidate before committing authoritative
  component state, preventing partial restore.
- `MachineRuntimeNbtCodec` is the NeoForge-only NBT adapter.
- `EngineMachineBlockEntity` owns platform lifecycle and deferred integration effects, never simulation execution.

## Level-Scoped Machine Scheduler

- `EngineMachineLevelManager` owns scheduler lifecycle through NeoForge level events.
- `LevelMachineScheduler` runs one bounded scheduler slice per server-level tick.
- `SimulationExecutionObserver` lets adapters enqueue only dirty machines that actually executed.
- `SimulationFailureObserver` reports staged failures while the scheduler invalidates only the
  failed participant and continues unrelated bounded work.
- Sleeping machines remain outside recurring platform work.

## Material Registry Snapshot

**Status:** Hardened in Milestone 17.

Materials, processing routes, and source provenance are built as one immutable candidate. The
registry publishes the generation through one volatile write only after parsing and cross-reference
validation succeeds. Invalid candidates retain the previous known-good generation rather than
exposing partial reload state.
## NeoForge Energy Capability Adapter

**Status:** Implemented in Milestone 16.

`EngineEnergyStorageAdapter` exposes `MachineEnergyComponent` through NeoForge `IEnergyStorage` without creating a second energy store. Simulation and capability operations share the same capacity, throughput, access rules, dirty state, and wake path.

## First Engine-Owned Machine

**Status:** Implemented in Milestone 16.

The Creative Energy Cell is the first existing PoC block migrated to `EngineMachineBlockEntity`. Its refill behavior is Minecraft-independent machine logic; adjacent FE transfer remains a post-execution platform adapter concern. The block has no dedicated server ticker and sleeps when no transfer work exists.
