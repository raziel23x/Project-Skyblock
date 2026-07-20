# Backend Milestone 16 — First Engine-Owned Machine

## Status

Implemented; awaiting authoritative local Gradle and in-game validation.

## Goal

Prove that an existing Minecraft block can be driven by the reusable simulation engine without restoring a per-block-entity simulation tick loop.

## Delivered

- Migrated the Creative Energy Cell from a standalone ticking block entity to `EngineMachineBlockEntity`.
- Added `CreativeEnergyCellLogic` as Minecraft-independent machine logic.
- Added `EngineEnergyStorageAdapter`, a NeoForge `IEnergyStorage` view over `MachineEnergyComponent`.
- Reused the level-scoped scheduler, runtime persistence snapshot, dirty tracking, and lifecycle bridge.
- Added a post-execution platform hook for effects that require world access while keeping simulation logic Minecraft-independent.
- Added one initial runtime evaluation when an engine machine loads.
- Cleared handled scheduler dirty state after platform integration is complete.
- Removed the Creative Energy Cell's dedicated block ticker.
- Added automated backend coverage for the creative energy logic.

## Runtime Flow

```text
level scheduler wakes Creative Energy Cell
                |
                v
CreativeEnergyCellLogic fills backend energy buffer
                |
                v
platform post-execution hook offers FE to neighbors
                |
                v
accepted FE is extracted from backend-owned energy
                |
                v
extraction wakes the runtime for the next bounded refill
```

When no adjacent receiver accepts energy, the cell remains full and sleeps. A neighbor change or external capability extraction wakes it for reevaluation.

## TPS Contract

- The block no longer owns a `BlockEntityTicker`.
- No work occurs while the cell is full and has no receiver.
- Neighbor changes wake the machine instead of forcing continuous topology polling.
- All backend execution remains bounded by the level scheduler.

## Persistence Contract

Stored energy, thermal state, inventory state, and processing state continue to use `MachineRuntimeSnapshot`. Scheduler queues, capabilities, cached neighbors, and derived values are not stored in NBT.

## Additional Architectural Changes

### Initial load evaluation

Every engine machine now requests one bounded evaluation after runtime creation and snapshot restoration. This is required so a newly loaded machine can reconstruct platform-facing behavior without waiting for an unrelated external event.

### Post-execution platform hook

`EngineMachineBlockEntity.afterMachineExecution()` allows world-facing integration after backend execution. This keeps world access out of `MachineLogic` while avoiding a second per-machine tick loop.

### Scheduler dirty cleanup

The bridge clears `DirtyFlag.SCHEDULER` after the wake request has already been applied. This prevents handled scheduler work from remaining permanently dirty.

## Deliberate Deferrals

- The inventory capability adapter remains deferred until item-component fidelity and sided slot mapping are proven by the first processing machine.
- The Material Crusher is not migrated in this milestone.
- The old Creative Energy Cell implementation will be copied into `legacy-reference/` only after the engine replacement passes in-game validation, following the archive rules.
- Cable migration remains separate; the existing cable PoC may still transfer FE through the new capability adapter during validation.
- A GUI is unnecessary for the creative testing source and was not added.

## Validation

Completed in the implementation environment:

- Minecraft-independent simulation sources compile with Java 21.
- New backend test sources compile against local JUnit API stubs.
- `git diff --check` passes.

Required locally:

```bat
gradlew.bat clean
gradlew.bat test
gradlew.bat build
gradlew.bat compileJava
```

Required in-game:

1. Place a Creative Energy Cell next to a machine that accepts FE.
2. Confirm energy transfers without the old block ticker.
3. Place the receiver after the cell has already gone idle and confirm the neighbor change wakes it.
4. Save and reload the world and confirm transfer resumes.
5. Test through the existing cable PoC to confirm capability compatibility.
