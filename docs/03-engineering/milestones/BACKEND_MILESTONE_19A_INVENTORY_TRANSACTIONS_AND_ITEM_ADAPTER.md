# Backend Milestone 19A — Inventory Transactions and Item Capability Adapter

## Purpose

Prove the inventory mutation and NeoForge item-capability contracts required before migrating a
processing machine. Authoritative slot state remains in the Minecraft-independent simulation;
Minecraft `ItemStack` objects, data-component serialization, menus, and `IItemHandler` remain
boundary concerns.

This milestone is deliberately separated from the Material Crusher migration. The adapter and
transaction semantics must be testable before a processing prototype depends on them.

## Audit Findings

The Milestone 9 inventory component already owned immutable item identity, access rules, insertion
rules, restoration, dirty flags, and wake signaling. The next processing workload exposed four
missing contracts:

1. A recipe completion may consume inputs and insert multiple outputs, so per-slot operations are
   insufficient without an atomic multi-slot commit.
2. NeoForge capability simulation must never dirty state or wake a sleeping machine.
3. Automation and player menus require distinct extraction contracts. Automation must respect
   sided output access, while a player must still be able to remove an item from an input slot.
4. `SimulationItemState` had a safe opaque format but no production Minecraft data-component
   encoder/decoder.

## Implemented

### Atomic inventory transactions

- Added isolated transaction candidates over immutable slot snapshots.
- Added optimistic state versions and stale-candidate rejection.
- Added one atomic commit for multi-slot consumption and insertion.
- A successful multi-slot commit produces one dirty transition and one scheduler wake.
- Rollback and simulated operations produce no mutation, dirty flags, or wake signal.
- Added commit and conflict diagnostics without making diagnostics authoritative state.

### Explicit inventory views

- Added mapped inventory views for `AUTOMATION` and `MENU` boundaries.
- Views contain no slot state and cannot be used with a transaction from another component.
- Automation insertion and extraction respect slot access declarations.
- Menu insertion still respects slot acceptance, while menu extraction may return player-owned
  contents from an input slot.
- Duplicate or invalid mapped slots fail during view construction.

### Typed Minecraft item boundary

- Added a registry-aware `MinecraftItemStackCodec`.
- Item identity uses the registered item identifier plus a canonical persistent
  `DataComponentPatch.CODEC` payload encoded as sorted compact JSON.
- Component entries and JSON object keys are ordered before encoding, so equivalent patches do not
  receive different simulation identities because of mutation order.
- Registry references are serialized by namespaced keys rather than numeric network registry IDs.
- The codec verifies an exact encode/decode round trip and rejects transient or lossy components.
- The payload remains immutable and bounded by the existing 64 KiB engine limit.
- NBT and SNBT are not used for runtime identity or transport.
- Unknown items, unsupported state codecs, malformed or non-canonical payloads, unavailable
  registries, invalid stack limits, and oversized payloads fail closed.

### NeoForge item capability adapter

- Added `EngineItemHandlerAdapter` over a restricted inventory view.
- `simulate=true` stages and converts the result without committing.
- Extraction converts the staged output back into a Minecraft stack before the transaction commits,
  preventing undecodable state from being deleted.
- Failed encoding, failed decoding, and stale commits preserve authoritative state.
- Adapter diagnostics count boundary failures and transaction conflicts without log spam.

## Automated Coverage Added

- Atomic multi-slot commit and single-wake behavior.
- Rollback and simulation non-mutation.
- Stale transaction failure isolation.
- Combined input consumption and output insertion.
- Automation/menu permission separation.
- Slot mapping and transaction-owner validation.
- Stateless and component-bearing Minecraft stack round trips.
- Canonical component-order independence.
- Unsupported, malformed, non-canonical, transient, or lossy opaque-state rejection.
- Capability insertion, extraction, simulation, component identity, and undecodable-state safety.

## Architectural Boundaries

- No Minecraft, NeoForge, Mojang, NBT, or SNBT type enters the simulation package.
- The capability adapter owns no inventory state.
- The component codec is a platform adapter, not a simulation codec or public scripting API.
- No capability registration is changed in this milestone.
- The Material Crusher remains on its legacy inventory until Milestone 19B proves the complete
  processing migration.
- No general public API is frozen from this implementation candidate.

## Validation Available in the Delivery Sandbox

- All Minecraft-independent simulation sources compile cleanly with Java 21 and strict compiler
  warnings.
- A lightweight standalone assertion runner executed all 61 Minecraft-independent simulation tests
  successfully, including the new transaction and inventory-view suites.
- A separate Java harness passed atomic commit, rollback, stale-candidate, view-boundary, and
  opaque-state identity checks.
- New platform adapter and test sources compile against the preserved NeoForge 21.1.235 development
  artifacts used by the project, with minimal compile-only symbols for external libraries that are
  normally supplied by Gradle. This is a source-compatibility check, not a runtime test.
- Repository validation passes after the milestone documentation and source contracts are added.

The sandbox cannot resolve the Gradle distribution or external dependency graph. The complete
Windows `clean test build` remains the authoritative local gate.

## Gameplay, Save, API, and Dependency Impact

- **Gameplay:** no recipes, processing speed, progression, balance, interface layout, or visual
  behavior changes.
- **Save:** no machine snapshot schema change and no existing block entity is migrated.
- **API:** no public integration API is declared stable.
- **Dependencies:** no new runtime or development dependency.

## Next Candidate

Milestone 19B should migrate the Material Crusher only after this package passes the local Gradle
suite. That workload will prove transactional recipe completion, sided automation, hybrid FE/fuel
operation, processing persistence, menu synchronization, and scheduler sleep/wake behavior.
