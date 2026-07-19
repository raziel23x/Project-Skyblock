# Backend Milestone 3 — Energy Simulation

## Purpose

Establish simulation-owned energy storage, limits, requests, transfer, and diagnostics.

## Implemented Contracts

- `SimulationEnergyState`
- `EnergyBuffer`
- `EnergyLimits`
- `EnergyRequest`
- `EnergyTransfer`
- `EnergyFlowResult`
- `EnergyEngine`
- `EnergyDiagnostics`

## Architectural Guarantees

1. The backend owns stored energy.
2. Capacity and throughput are enforced by simulation operations.
3. Transfers produce explicit deterministic results.
4. Forge Energy is deferred to an adapter.

## Ownership Boundary

All contracts introduced by this milestone live in the backend simulation layer. Minecraft, NeoForge, block entities, capabilities, menus, screens, packets, and production gameplay are not authoritative owners.

## Validation

The milestone was compiled and runtime-tested before commit. Existing Research Era gameplay remained operational because production integration was deliberately deferred.

## Deliberately Deferred

- NeoForge Energy adapter
- machine energy composition
- network topology

## Completion Status

Completed and committed. Later milestones may extend these contracts but must preserve the ownership and dependency boundaries documented here.
