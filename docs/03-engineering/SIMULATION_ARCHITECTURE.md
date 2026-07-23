# Simulation Architecture

> Status: **Implementation foundation**

Project Skyblock is building a greenfield simulation engine for a technology and civilization mod.
Machines, generators, cables, thermal bodies, maintenance systems, and transport networks may
participate in one event-driven backend when their gameplay requirements justify those contracts.

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

The engine currently includes:

- bounded deterministic scheduling with sleeping, wake coalescing, and failure isolation;
- typed machine state and shared dirty-state ownership;
- backend-owned energy, inventory, thermal, and processing components;
- energy topology, route, transfer, and sleeping-network foundations;
- transactional runtime snapshots and bounded schema-2 opaque item identity using typed/component
  codecs rather than NBT runtime blobs;
- level-scoped NeoForge scheduling and persistence adapters;
- one engine-owned prototype machine and energy capability adapter;
- immutable last-known-good material and processing-route publication.

Research Era objects are migrated only to stress these boundaries. They remain disposable fixtures
and do not define final gameplay content.
