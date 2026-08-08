# Backend Milestone 20B1 — Transactional Energy Endpoints

## Status

**Implementation candidate — audit complete; Project Skyblock validation and clean Gradle test/build
passed in Termux/Debian; standalone/integration runtime acceptance is pending.**

This milestone is not part of the immutable Milestone 17 baseline tag. It extends the accepted M20A
transport foundation without changing M20A topology/planning ownership.

## Purpose

Give machine-owned energy endpoints an optimistic transaction/version contract that later M20B
energy dispatch can validate and commit without guessing whether endpoint state changed after
planning.

## Implemented Contract

- `MachineEnergyTransaction` stages one isolated directional endpoint operation.
- Each transaction captures exactly one `MachineEnergyComponent` and one base `stateVersion`.
- Staging is non-mutating: it does not change authoritative energy, dirty state, or scheduler state.
- A changed current transaction publishes once, advances state version once, dirties once, and wakes
  once.
- A stale transaction fails closed with no authoritative energy mutation or wake.
- A no-op commit does not advance state version or wake.
- External receive/extract obey `MachineEnergyAccess`; internal produce/consume bypass external access
  but use the same publication path.
- One transaction represents one atomic endpoint operation. Repeated staging in one direction shares
  that operation's receive/extract throughput budget; receive and extract directions cannot be mixed.
- Access-mode changes and changed durable restores invalidate open candidates by advancing endpoint
  state version.
- Durable restore remains a load boundary: it requests client synchronization but does not create
  persistence/scheduler dirtiness or a scheduler wake.

## NeoForge Boundary

`EngineEnergyStorageAdapter` remains an adapter over simulation-owned state. Simulated FE calls stage
a disposable candidate and close it without commit. Real FE calls open a fresh candidate and commit
only when it changed. A prior FE simulation is explicitly **not** a reservation of future endpoint
capacity.

## Persistence Boundary

`MachineRuntimePersistence` validates and restores machine energy through `MachineEnergyComponent`
rather than reaching through to mutable `SimulationEnergyState`. This preserves endpoint version
invalidation while keeping the simulation state authoritative.

## Regression Coverage

M20B1 adds or extends:

- `MachineEnergyTransactionTest`;
- `EngineEnergyStorageAdapterTest`;
- `MachineRuntimePersistenceTest`;
- `EnergyNetworkEngineTest`;
- `EnergyNetworkParticipantTest`.

The Milestone 5/6 tests are intentionally retained so later transport migration cannot silently erase
useful route-loss, acceptance-bounding, active-recheck, sleeping, or topology-change behavior before
its replacement contract is proven.

## Validation State

Completed before this candidate status is recorded:

- `python3 tools/validate_project.py`;
- `./gradlew clean test --no-daemon --stacktrace`;
- `./gradlew clean build --no-daemon --stacktrace`;
- exact changed-path and `git diff --check` validation.

Still required before acceptance:

- standalone client/runtime regression;
- integration client/runtime regression;
- live Creative Energy Cell / Material Crusher energy-adapter regression;
- save/reload and clean-log check.

## Deliberately Deferred

M20B1 does not implement or claim:

- complete typed-network energy dispatch;
- route-loss policy over M20A transport plans;
- atomic/two-phase commit across independent endpoints;
- transactional guarantees for arbitrary third-party FE implementations;
- Basic Energy Cable migration;
- gameplay, progression, recipe, balance, asset, or configuration changes.

Later M20B must preserve the energy-conservation invariant:

`source decrease = receiver increase + declared loss`

The Basic Energy Cable remains unchanged until M20C after that broader policy is proven.
