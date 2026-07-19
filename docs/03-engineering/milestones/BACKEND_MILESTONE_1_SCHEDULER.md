# Backend Milestone 1 — Explicit Simulation Scheduler

## Purpose

The first executable backend layer is the scheduler. Machines, generators,
cables, thermal bodies, and future networks must not independently poll every
server tick when nothing has changed.

## Added contracts

- `SimulationLifecycle`
- `SimulationContextFactory`
- `SimulationScheduler`
- `SchedulerTickReport`
- `SimulationResult.Invalid`

## Runtime rules

1. Registration begins in `SLEEPING` state.
2. A participant runs only after `wake(...)` or when a scheduled time becomes due.
3. `BLOCKED` participants remain idle until an external change wakes them.
4. `INVALID` participants remain disabled until removed or repaired by platform code.
5. Each server tick has a fixed maximum execution count.
6. Each execution receives a fixed work-unit budget.
7. Stale schedule tickets are ignored through generation numbers.
8. Dirty-state ownership is per participant and remains separate from integration work.

## Why this comes first

Thermal, energy, cable, generator, and machine controllers all require the same
wake/sleep/schedule behavior. Implementing that behavior once prevents every
subsystem from inventing its own tick loop.

## Deliberately deferred

- Minecraft level storage for schedulers
- Block-entity adapters
- chunk load and unload hooks
- persistence encoding
- thermal participant controllers
- cable topology scheduling
- legacy machine replacement

Those layers now have a stable execution contract to build against.

## Local verification

Run:

```bat
gradlew.bat compileJava
gradlew.bat runClient
```

The existing gameplay should remain unchanged because no legacy block entity is
wired into the scheduler during this milestone.
