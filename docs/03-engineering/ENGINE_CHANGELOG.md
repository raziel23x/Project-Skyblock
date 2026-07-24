# Engine Milestone 19B1 — Combustion State and Persistence

- Added an internal machine combustion reservoir with validated ignition, consumption, restore, and diagnostics.
- Integrated combustion into the shared machine runtime dirty-state boundary.
- Added typed combustion persistence and machine snapshot schema 3.
- Preserved schema 1 and 2 loading with empty combustion state.
- Deliberately deferred Material Crusher behavior and capability migration to Milestone 19B2.

# Engine Milestone 19A — Inventory Transactions and Item Capability Adapter

- Added optimistic atomic machine-inventory transactions with stale-candidate rejection.
- Added explicit automation and player-menu inventory views without duplicating slot state.
- Added canonical registry-aware Minecraft data-component persistence encoding with namespaced keys, exact round-trip verification, and transient-state rejection.
- Added a fail-closed NeoForge `IItemHandler` adapter that converts staged extraction before commit.
- Added transaction, view, codec, capability, simulation, conflict, and component-identity regression tests.
- Deliberately deferred Material Crusher migration, capability registration, gameplay changes, and public API freezing to Milestone 19B or later evidence.

# Engine Milestone 18 — Tiered Developer Validation Environments

- Added isolated standalone and integration client runs with separate working directories and world copies.
- Added a tracked Modrinth intent manifest, recursive dependency resolution, exact local version locking,
  SHA-512 verification, and unmanaged-JAR rejection.
- Added offline validation for the local helper set and release-artifact isolation checks to `check`.
- Kept helper mods out of runtime dependencies, published metadata, source control, and release JARs.
- Deliberately separated environment construction from future Project Skyblock-specific optional adapters.

# Engine Milestone 17 — Baseline Hardening and Contract Stabilization

- Corrected machine, cable, connection, and route energy limits to explicit per-operation
  semantics without pretending shared-edge contention already exists.
- Added full-snapshot validation and transactional machine restore.
- Added machine snapshot schema 2 with bounded, opaque adapter-owned item state that explicitly
  rejects NBT/SNBT runtime contracts.
- Added scheduler failure stages, participant and deferred-platform isolation, and level-aware
  diagnostics.
- Made energy-network connection ordering consistent with equality and rejected conflicting endpoint definitions.
- Added one immutable, last-known-good material and processing-route registry snapshot, including
  empty-candidate rejection and collision-resistant namespaced data roots.
- Expanded regression tests, project validation tooling, CI artifacts, wrapper verification, and developer validation documentation.
- Recorded that the engine is greenfield and that migrated Research Era objects are prototype stress fixtures rather than permanent gameplay commitments.

# Engine Milestone 16 — First Engine-Owned Machine

- Migrated the Creative Energy Cell onto `MachineRuntime` and the level-scoped scheduler.
- Added a NeoForge energy capability adapter over backend-owned machine energy.
- Removed the Creative Energy Cell's dedicated block-entity ticker.
- Added a post-execution platform hook, initial load evaluation, and handled scheduler-dirty cleanup.
- Added backend tests for the creative energy source logic.

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

Run the complete local Gradle and client regression gates for Milestone 19B1. After combustion
persistence is proven, migrate the Material Crusher in Milestone 19B2 to stress transactional
processing, sided automation, hybrid FE/fuel operation, legacy save conversion, and scheduler
sleep/wake behavior. Prototype conversion does not make the crusher permanent Game Era content.
