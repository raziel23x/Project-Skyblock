# Backend Milestone 12 — Machine Composition Proof

## Purpose

Prove that the machine framework, scheduler, energy component, inventory component, and thermal component can operate as one coherent Minecraft-independent runtime without introducing recurring polling or duplicated integration state.

## Composition Root

`MachineRuntime` is the registration and ownership boundary for one composed machine. It creates and owns:

- one stable `MachineId`;
- one shared `DirtyStateTracker`;
- one `MachineEnergyComponent`;
- one `MachineInventoryComponent`;
- one `MachineThermalComponent`;
- one typed `MachineState<MachineComponentState>`;
- one `MachineParticipant<MachineComponentState>`;
- one scheduler registration.

The runtime does not own recipes, world access, block entities, menus, packets, rendering, persistence codecs, or NeoForge capabilities.

## Shared Dirty-State Contract

The review found that the scheduler previously always created its own dirty tracker during registration. A composed machine would therefore have required either duplicated dirty state or copying between component state and scheduler context.

`SimulationScheduler` now includes a registration overload that accepts an externally owned `DirtyStateTracker`. Existing callers remain source-compatible through the original registration method.

Every component in `MachineComponentState` must reference the same tracker by identity. Construction fails immediately if a mismatched tracker is supplied. This guarantees that:

- component changes mark one authoritative dirty state;
- scheduler execution receives that same tracker through `SimulationContext`;
- persistence and client-sync adapters can observe the same pending work;
- no polling or dirty-mask copying is required.

## Wake and Sleep Behavior

All composed components share one wake signal. A meaningful component change:

1. marks persistence, client-sync, and scheduler dirty flags;
2. requests machine work;
3. wakes the scheduler registration;
4. relies on Milestone 11 wake coalescing if the change occurs during execution.

Sleeping and blocked machines perform no recurring work. They reevaluate only after explicit work requests, meaningful component changes, or a returned future schedule.

## Diagnostics

The milestone adds aggregate immutable diagnostics:

- `MachineComponentDiagnostics` combines energy, inventory, thermal, and dirty-mask state;
- `MachineRuntimeDiagnostics` combines scheduler lifecycle, scheduler reason, machine lifecycle, and component diagnostics.

These snapshots provide a future adapter boundary without exposing mutable backend state.

## Lifecycle

`MachineRuntime` implements `AutoCloseable`. Closing it unregisters the participant and is idempotent. Runtime operations that require registration fail clearly after closure, while repeated closure and rejected work requests remain harmless.

## Automated Validation

`MachineRuntimeTest` covers:

- shared dirty tracker identity across scheduler, context, and all components;
- sleeping machines performing no recurring polling;
- external component changes waking sleeping machines;
- blocked machines reevaluating only after relevant state changes;
- active-execution component changes using deferred scheduler wake behavior;
- bounded work-budget enforcement;
- aggregate diagnostics;
- idempotent runtime removal.

`SimulationSchedulerTest` additionally verifies externally owned dirty tracker registration.

## Additional Architectural Change

The scheduler registration overload was not part of the original narrow composition proposal. It was added because composition exposed a concrete ownership conflict: one machine cannot have both component-owned and scheduler-owned dirty state without duplication, copying, or polling.

The change is deliberately small, backward-compatible, and directly supports the project constitution.

## Deliberate Deferrals

This milestone does not add:

- recipe lookup;
- processing progress;
- persistence codecs;
- Minecraft or NeoForge adapters;
- fluids or gases;
- a production machine migration.

Those remain separate milestones after composition has been validated locally.

## Result

The backend now has a proven composition root for a machine that owns energy, inventory, and thermal resources, shares one authoritative dirty-state boundary, sleeps while idle, wakes after meaningful changes, executes under a bounded budget, and remains independent of Minecraft.
