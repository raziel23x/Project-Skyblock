# Project Skyblock Development TODO

This file tracks active work and near-term checkpoints. It is not a permanent idea dump and it does not define the project's identity. The Constitution, game-design documents, engineering principles, and engine roadmap remain authoritative.

## Current Direction

Project Skyblock is a technology and civilization mod built on a greenfield,
Minecraft-independent simulation engine. Existing content is a set of prototype stress fixtures:
it remains available while it exposes scheduler, resource, persistence, adapter, networking, and
integration requirements, then may be archived or removed after its evidence is captured.

## Immediate Work

- [x] Backend Milestone 12 — Machine Composition Proof
- [x] Backend Milestone 13 — Machine Processing Component
- [x] Backend Milestone 14 — Machine Runtime Persistence Bridge
- [x] Backend Milestone 15 — Level-Scoped Scheduler Driver
- [x] Run local Gradle tests and build for Milestone 15
- [x] Add the first energy capability adapter without duplicating authoritative state
- [x] Build the first engine-owned vertical slice using the Creative Energy Cell
- [x] Backend Milestone 17 — Baseline Hardening and Contract Stabilization
- [x] Add scheduler failure isolation and failure-injection tests
- [x] Make machine persistence validation transactional
- [x] Preserve opaque item state in engine identity and snapshot schema 2
- [x] Publish material and processing-route data as one last-known-good snapshot
- [x] Correct machine energy limits to explicit per-operation semantics
- [ ] Run local Gradle and in-game validation for Milestone 16
- [ ] Run local Gradle and in-game validation for Milestone 17
- [ ] Add the inventory capability adapter when a processing-prototype migration proves sided item semantics

## Prototype Stress-Test Migration

The existing working content is executable evidence, not a final gameplay or content commitment.
Each conversion must identify the contracts being stressed, fix root causes, add regression tests,
and record when the prototype no longer provides unique validation value.

- [ ] Convert a processing prototype, likely the Material Crusher, to stress energy, inventory,
  processing, persistence, synchronization, and sided-adapter contracts
- [ ] Preserve hybrid operation using either stored power or burnable material where the final design still requires it
- [x] Recreate the creative power source as an engine-owned testing adapter
- [ ] Recreate the Thermal Generator through engine-owned energy and thermal state
- [ ] Recreate Basic Energy Cable behavior through the energy-network backend
- [ ] Validate multiple machines on one network
- [ ] Recreate cobblestone, water, and lava utility generators through approved production systems
- [ ] Validate sided input, output, fuel, power, and interface behavior through adapters
- [ ] Validate prototype blocks, items, tools, weapons, armor, and remote behaviors only where they
  exercise a distinct engine or adapter boundary
- [ ] Move superseded PoC code, assets, and data into `legacy-reference/`
- [ ] Record each retirement in `legacy-reference/MIGRATION_LEDGER.md`
- [ ] Confirm archived material is not compiled or loaded

## Engine Era

### Implemented

- [x] Explicit scheduler with bounded execution
- [x] Thermal simulation core
- [x] Energy simulation
- [x] Energy network topology and transfer
- [x] Sleeping networks and wake coalescing
- [x] Machine framework
- [x] Machine energy component
- [x] Machine inventory component
- [x] Machine thermal component
- [x] Machine composition runtime
- [x] Machine processing component

### Planned When Proven Necessary

- [x] Persistence contracts and codecs
- [x] Minecraft/NeoForge block-entity and level-scheduler adapters
- [x] Energy capability adapter
- [ ] Inventory capability adapter
- [x] Reference vertical slice and first engine-owned machine
- [x] Transaction-safe persistence and schema-2 item-state identity
- [x] Scheduler failure isolation
- [x] Atomic last-known-good material publication
- [ ] Machine fluid component
- [ ] Machine gas component
- [ ] Fluid and gas network behavior
- [ ] Network fairness and contention
- [ ] Chemistry-supporting resource properties

## Game Era

- [ ] Establish the one-tree starting experience
- [ ] Build meaningful manual survival before automation
- [ ] Introduce the first engine-driven processing chain
- [ ] Make automation a progression reward rather than an idle shortcut
- [ ] Develop research and the Journal as the central progression interface
- [ ] Expand logistics, energy, materials, chemistry, ecology, and civilization systems
- [ ] Support multiple valid progression paths without losing clear goals

## Compatibility and Polish

- [ ] Standardize visible machine connection rules
- [ ] Add recipe-viewer integration when production recipes exist
- [ ] Validate optional compatibility without making external mods mandatory
- [ ] Add shared machine sounds and animation helpers only after the vertical slice proves the need
- [ ] Profile real gameplay before performance tuning beyond established bounded-work rules

## Parking Lot

Interesting ideas that are not commitments:

- Replaceable machine parts and maintenance
- Upgrade cards or augments
- Factory planning overlays
- Advanced energy storage
- Additional thermal interactions
- Large-scale engineering projects

## Explicitly Not Planned

- Passive generators that bypass progression
- Infinite idle progression
- Copying an existing technology mod's machine list as the design
- Per-tick polling where event-driven sleeping is possible
- NBT used as live runtime state
- Backend redesigns performed only for novelty or cosmetic preference
- Premature abstractions without a milestone proving the need

## Vision Check

Every task should move Project Skyblock toward becoming a technology and civilization mod powered by a reusable simulation engine. Work that does not support that direction should be reconsidered before implementation.
