# Backend Milestone 4 — Energy Network Topology

## Purpose

Establish a deterministic, Minecraft-independent topology authority before adding
network transfer scheduling. This keeps graph discovery and routing separate from
Forge Energy capabilities and the legacy cable block entities.

## Added contracts

- `EnergyNetworkNodeId`
- `EnergyNetworkConnection`
- `EnergyRoute`
- `EnergyNetworkTopology`

## Runtime rules

1. Topology changes only in response to explicit add, remove, connect, or disconnect events.
2. Nodes use stable identifiers supplied by the platform adapter.
3. Connections are undirected and canonicalized by node-id order.
4. Every real topology mutation increments a monotonic revision.
5. Connected-component discovery is deterministic.
6. Route discovery uses minimum hop count with node-id order as the tie breaker.
7. A route exposes its bottleneck transfer limit but does not move energy.

## Why this slice is separate

The network solver will need stable components and repeatable routes. Implementing
transfer, fairness, shared-edge throughput, loss accounting, and sleeping behavior
before topology is trustworthy would mix several independent problems and make the
result difficult to validate.

## Deliberately deferred

- source and receiver registration,
- per-tick shared-edge throughput accounting,
- route efficiency and cumulative loss,
- fair distribution between multiple receivers,
- scheduler participant integration,
- sleeping and wake conditions,
- diagnostics snapshots,
- NeoForge and BlockEntity adapters,
- replacement of the legacy cable manager.

Existing gameplay remains unchanged. This milestone introduces backend contracts only.
