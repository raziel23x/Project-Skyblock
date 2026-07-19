# Backend Milestone 1 — Explicit Simulation Scheduler

## Purpose

Create one bounded execution model shared by future machines, networks, thermal controllers, and other simulation participants.

## Implemented Contracts

- `SimulationLifecycle`
- `SimulationContextFactory`
- `SimulationScheduler`
- `SchedulerTickReport`
- `SimulationResult.Invalid`

## Architectural Guarantees

1. Registration begins sleeping.
2. Participants execute only after wake or a due schedule.
3. Blocked participants wait for external change.
4. Invalid participants remain disabled until repaired or removed.
5. Per-tick executions and per-execution work are bounded.
6. Generation numbers invalidate stale schedule entries.
7. Equal-time ordering is deterministic.

## Ownership Boundary

All contracts introduced by this milestone live in the backend simulation layer. Minecraft, NeoForge, block entities, capabilities, menus, screens, packets, and production gameplay are not authoritative owners.

## Validation

The milestone was compiled and runtime-tested before commit. Existing Research Era gameplay remained operational because production integration was deliberately deferred.

## Deliberately Deferred

- Minecraft scheduler storage
- block-entity adapters
- chunk hooks
- persistence encoding

## Completion Status

Completed and committed. Later milestones may extend these contracts but must preserve the ownership and dependency boundaries documented here.
