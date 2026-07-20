# Engine Milestone 15 — Level-Scoped Scheduler Driver

- Added one bounded scheduler owner per logical server level.
- Added post-execution observation without changing existing scheduler callers.
- Avoided per-tick scans of sleeping machines by queuing integration work only after actual execution.
- Connected engine machine lifecycle to NeoForge level tick and unload events.

# Engine Architectural Changelog

This is an architectural history, not a release changelog.

## Foundation

The project adopted backend-owned state, dirty tracking, explicit execution results, bounded budgets, and Minecraft-independent participants.

## Milestone 1 — Explicit Simulation Scheduler

Introduced sleeping, waking, scheduled execution, blocked state, invalid state, deterministic queue ordering, generation-based stale-ticket invalidation, and per-tick execution bounds.

## Milestone 2 — Thermal Core

Introduced authoritative thermal state, properties, environments, transfer logic, conditions, constants, and diagnostics.

## Milestone 3 — Energy Simulation

Moved energy ownership into simulation state and introduced limits, requests, deterministic transfers, flow results, engines, and diagnostics.

## Milestone 4 — Energy Network Topology

Separated graph ownership from resource movement through node identifiers, connections, routes, and topology.

## Milestone 5 — Energy Network Transfer

Added deterministic route execution with hop-level and whole-transfer results.

## Milestone 6 — Network Sleeping

Connected energy-network runtime state to the scheduler so idle networks stop recurring work and wake after meaningful change.

## Milestone 7 — Machine Framework Core

Separated machine state, machine logic, scheduler participation, identity, activity, and diagnostics.

## Milestone 8 — Machine Energy Component

Added the first reusable machine component with backend energy ownership, access control, throughput enforcement, dirty signaling, wake signaling, and diagnostics.

## Milestone 9 — Machine Inventory Component

Added stable item identity, immutable item quantities, configurable machine slots, per-slot insertion rules, external and internal operation separation, validated restoration, immutable snapshots, diagnostics, dirty signaling, wake signaling, and automated component tests.

## Milestone 10 — Machine Thermal Component

Added reusable machine-owned thermal state integration with external heat access control, internal generation and cooling operations, operating-condition queries, bounded ambient exchange, restoration, immutable snapshots, diagnostics, dirty signaling, wake signaling, and automated component tests.

## Milestone 11 — Scheduler Wake Coalescing

Hardened active-execution wake behavior by coalescing repeated wake requests, deferring in-flight wakes to one next-tick reevaluation, preventing duplicate same-tick execution, and adding scheduler lifecycle tests.

## Milestone 12 — Machine Composition Proof

Added `MachineRuntime` as the Minecraft-independent composition root for energy, inventory, thermal, typed machine state, scheduler participation, shared dirty-state ownership, coalesced wake signaling, aggregate diagnostics, and lifecycle removal. Added a backward-compatible scheduler registration overload for externally owned dirty trackers after composition exposed the need for one authoritative dirty-state boundary.

## Milestone 13 — Machine Processing Component

Added recipe-independent processing lifecycle state with explicit idle, running, blocked, and ready-to-complete phases. Integrated processing into the composed machine runtime, added immutable diagnostics and validated restoration, and avoided redundant scheduler wake requests during ordinary progress while retaining wake behavior for lifecycle transitions.

## Milestone 14 — Machine Runtime Persistence Bridge

Added a versioned Minecraft-independent machine snapshot, minimal capture and restoration services, exact thermal restoration, a NeoForge NBT codec isolated from the simulation, and a reusable `EngineMachineBlockEntity` lifecycle bridge. The adapter performs deferred persistence and client-update work from dirty flags while deliberately leaving scheduler execution to a future level-scoped driver.

## Next

The next milestone will establish level-scoped scheduler ownership and use the bridge for the first engine-owned vertical slice.
