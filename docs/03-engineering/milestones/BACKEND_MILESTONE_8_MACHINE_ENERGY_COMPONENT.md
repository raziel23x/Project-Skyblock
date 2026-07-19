# Backend Milestone 8 - Machine Energy Component

## Purpose

Add the first reusable machine-owned resource component without creating a production machine or coupling the simulation to Minecraft.

## Implemented

- machine energy access rules: none, input, output, and bidirectional,
- reuse of the authoritative `SimulationEnergyState`,
- external receive and extract operations,
- internal produce and consume operations,
- capacity and per-tick throughput enforcement,
- persistence, client-sync, and scheduler dirty signaling,
- owner wake signaling after meaningful changes,
- immutable diagnostics snapshots.

## Architectural boundary

The component contains no Forge Energy, capability, BlockEntity, menu, networking, recipe, or gameplay code. A future Minecraft adapter may expose this component, but the adapter will not own the energy state.

## Deferred

- machine composition container,
- direct energy-network endpoint registration,
- thermal and inventory components,
- processing and recipes,
- persistence codecs,
- NeoForge adapters.
