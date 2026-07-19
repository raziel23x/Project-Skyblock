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

## Next

Milestone 10 will establish the automated backend test foundation and prove machine composition using the energy and inventory components together.
