# Simulation Architecture

> Status: **Implementation foundation**

Project Skyblock is an industrial simulation mod. Machines, generators, cables, thermal bodies,
maintenance systems, and transport networks are participants in one event-driven backend.

```text
Simulation Core
├── Scheduling and bounded work
├── Typed authoritative state
├── Dirty-state tracking
├── Machine participants
├── Energy networks and cables
├── Thermal simulation
├── Resource transport
├── Maintenance and components
├── Persistence adapters
└── NeoForge platform integration
```

## Core rule

A participant executes only because relevant state changed or scheduled work became due. Merely
existing during a server tick is not sufficient reason to run.

## Thermal rule

Temperature is not a cosmetic machine property. Machines, generators, cables, and environmental
interfaces participate in the same deterministic thermal model.

Thermal behavior may influence:

- valid operating ranges,
- conversion efficiency,
- cable losses,
- shutdown conditions,
- cooling requirements,
- maintenance and wear,
- environmental heat exchange.

The backend uses fixed-point units for authoritative thermal calculations so results remain stable
and testable.

## Cable rule

Cables are simulation participants rather than passive FE forwarding blocks. Their backend state
may include:

- capacity,
- transfer limits,
- electrical loss,
- generated resistive heat,
- thermal limits,
- network membership,
- fault or shutdown state.

Topology rebuilding must be event-driven and incremental. A cable network must not rediscover its
entire graph every tick.

## Current implementation boundary

The first code slice establishes:

- `SimulationState`,
- `SimulationParticipant`,
- `SimulationContext`,
- `SimulationBudget`,
- `SimulationResult`,
- `DirtyStateTracker`,
- fixed-point `ThermalState`,
- deterministic `ThermalTransfer`,
- thermally aware `EnergyCableState`,
- resistive-loss `EnergyCableTransfer`.

It intentionally does not yet connect these contracts to legacy block entities. The next slice is
the scheduler and level-scoped participant registry, followed by replacement block-entity hosts.
