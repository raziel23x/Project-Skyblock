# Backend Milestone 6 — Network Sleeping

## Purpose

Integrate energy networks with the scheduler so idle networks perform no recurring work.

## Implemented Contracts

- `EnergyNetworkActivity`
- `EnergyNetworkRuntimeState`
- `EnergyNetworkParticipant`
- `EnergyNetworkRuntimeDiagnostics`

## Architectural Guarantees

1. Runtime activity is explicit.
2. Idle networks sleep.
3. Meaningful state or topology changes wake the participant.
4. Execution remains bounded by scheduler budgets.

## Ownership Boundary

All contracts introduced by this milestone live in the backend simulation layer. Minecraft, NeoForge, block entities, capabilities, menus, screens, packets, and production gameplay are not authoritative owners.

## Validation

The milestone was compiled and runtime-tested before commit. Existing Research Era gameplay remained operational because production integration was deliberately deferred.

## Deliberately Deferred

- Minecraft network ownership
- fair distribution
- persistence codecs

## Completion Status

Completed and committed. Later milestones may extend these contracts but must preserve the ownership and dependency boundaries documented here.
