# Backend Milestone 14 — Machine Runtime Persistence Bridge

## Purpose

Establish the first reusable contract between a Minecraft `BlockEntity` and the backend-owned `MachineRuntime` without transferring simulation authority to Minecraft.

## Implemented

- Minecraft-independent `MachineRuntimeSnapshot` schema.
- Minimal processing snapshot contract.
- Capture and validated restore through `MachineRuntimePersistence`.
- Exact thermal-energy restoration without NBT in the backend.
- NeoForge NBT codec isolated under the platform adapter package.
- Reusable `EngineMachineBlockEntity` lifecycle and persistence bridge.
- Explicit adapter lifecycle states.
- Deferred dirty-flag flushing for persistence and client presentation.
- Persistence round-trip tests.

## Architectural Boundaries

The snapshot contains only authoritative durable state:

- stored energy;
- thermal energy;
- inventory contents;
- active processing state and completed-process count.

It deliberately excludes scheduler queues, dirty masks, wake state, diagnostics, caches, world references, capabilities, menus, and rendering data.

`EngineMachineBlockEntity` does not tick the simulation. A future level-scoped scheduler driver must execute one bounded scheduler per level so machine count does not create machine-count tick loops.

## Additional Architectural Work

A schema version was included immediately because save compatibility requires an explicit migration boundary before production worlds exist. This is a small current requirement, not speculative abstraction.

The platform bridge flushes deferred side effects from dirty flags rather than allowing backend components to call Minecraft APIs directly.

## Deliberate Deferrals

- Level-scoped scheduler ownership and server tick integration.
- Forge Energy and item capability adapters.
- Client packet specialization beyond block updates.
- Migration of an existing PoC block entity.
- Snapshot schema migration beyond version 1.

## Validation

- Backend persistence sources compiled independently with `javac`.
- Project resource validation passed.
- Gradle execution must be completed locally because the build environment could not download Gradle.
