# Backend Milestone 4 — Energy Network Topology

## Purpose

Represent energy-network graph structure and deterministic routing independently of transfer execution.

## Implemented Contracts

- `EnergyNetworkNodeId`
- `EnergyNetworkConnection`
- `EnergyRoute`
- `EnergyNetworkTopology`

## Architectural Guarantees

1. Topology owns graph relationships.
2. Node identifiers provide stable ordering.
3. Routes are explicit values.
4. Topology discovery from the world remains an adapter concern.

## Ownership Boundary

All contracts introduced by this milestone live in the backend simulation layer. Minecraft, NeoForge, block entities, capabilities, menus, screens, packets, and production gameplay are not authoritative owners.

## Validation

The milestone was compiled and runtime-tested before commit. Existing Research Era gameplay remained operational because production integration was deliberately deferred.

## Deliberately Deferred

- resource transfer
- network runtime
- world cable discovery

## Completion Status

Completed and committed. Later milestones may extend these contracts but must preserve the ownership and dependency boundaries documented here.
