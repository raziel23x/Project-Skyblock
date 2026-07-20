# Backend Milestone 15 — Level-Scoped Scheduler Driver

## Status

Implemented and validated locally with Gradle.

## Goal

Connect engine-owned machine runtimes to one bounded scheduler per logical server level without restoring per-block-entity tick loops or scanning every sleeping machine.

## Delivered

- `SimulationExecutionObserver` provides an allocation-free post-execution notification.
- `SimulationScheduler.tick(...)` gained a backward-compatible observer overload.
- `LevelMachineScheduler` owns one bounded scheduler for one `ServerLevel`.
- `EngineMachineLevelManager` creates, ticks, and retires level schedulers through NeoForge events.
- `EngineMachineBlockEntity` now receives the level-owned scheduler when constructing its runtime.
- Only machines that actually execute and produce dirty integration state are queued for persistence or client synchronization flushing.
- Level unload removes the platform registry and releases integration references.

## TPS Contract

Sleeping machines are absent from recurring work. The platform adapter does not iterate every registered machine each tick. Scheduler execution remains bounded by a maximum participant count and per-execution work budget.

## Deliberate Deferrals

- Energy and inventory capability adapters remain the next integration concern.
- No production machine is migrated in this milestone.
- Scheduler limits remain internal constants until profiling proves configuration is needed.
- Cross-level or global machine networks are not introduced.
