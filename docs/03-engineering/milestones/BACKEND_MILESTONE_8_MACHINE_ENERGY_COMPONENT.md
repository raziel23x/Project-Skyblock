# Backend Milestone 8 — Machine Energy Component

## Purpose

Add the first reusable machine-owned resource component without creating a production machine.

## Implemented Contracts

- `MachineEnergyAccess`
- `MachineEnergyComponent`
- `MachineEnergyDiagnostics`

## Architectural Guarantees

1. SimulationEnergyState remains authoritative.
2. External and internal operations are distinct.
3. Access modes and throughput are enforced.
4. Meaningful changes mark persistence, client-sync, and scheduler dirty state and wake the owner.

## Ownership Boundary

All contracts introduced by this milestone live in the backend simulation layer. Minecraft, NeoForge, block entities, capabilities, menus, screens, packets, and production gameplay are not authoritative owners.

## Validation

The milestone was compiled and runtime-tested before commit. Existing Research Era gameplay remained operational because production integration was deliberately deferred.

## Deliberately Deferred

- inventory, fluid, gas, thermal, and processing components
- network endpoint integration
- NeoForge Energy adapter

## Completion Status

Completed and committed. Later milestones may extend these contracts but must preserve the ownership and dependency boundaries documented here.
