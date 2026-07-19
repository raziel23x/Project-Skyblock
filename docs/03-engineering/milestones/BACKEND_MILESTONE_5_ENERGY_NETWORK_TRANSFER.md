# Backend Milestone 5 - Energy Network Transfer

## Purpose

Add deterministic single-route energy movement on top of the established topology.
The simulation remains independent from Minecraft, NeoForge capabilities, block
entities, and the legacy cable manager.

## Added contracts

- `EnergyNetworkEngine`
- `EnergyNetworkHopResult`
- `EnergyNetworkTransferResult`

## Runtime rules

1. The topology selects the deterministic minimum-hop route.
2. Source extraction, route bottleneck, and target acceptance all limit movement.
3. Connection losses are applied in route order using integer fixed-point math.
4. Every connection produces explicit entered, delivered, and lost accounting.
5. The source and target mutate only after a complete valid transfer plan exists.
6. A disconnected source and target produce no transfer result.

## Deliberately deferred

- competing transfers in the same tick,
- shared-edge throughput reservations,
- fairness between multiple targets,
- source and receiver registration,
- scheduler sleep and wake integration,
- network-wide diagnostics snapshots,
- Minecraft and NeoForge adapters.
