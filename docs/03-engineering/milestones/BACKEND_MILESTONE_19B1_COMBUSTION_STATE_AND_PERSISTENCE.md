# Backend Milestone 19B1 — Combustion State and Persistence

## Purpose

Add the smallest engine-owned durable state required before the Material Crusher can migrate from
its independent block-entity ticker. The existing crusher persists remaining and total furnace-fuel
burn time. Leaving those values in the block entity after migration would create split authority,
while encoding them as energy, thermal state, inventory contents, or processing metadata would
misrepresent their semantics.

Milestone 19B was therefore split after audit evidence. This prerequisite proves combustion state
and persistence independently before the crusher depends on it.

## Audit Finding

The composed machine runtime already owns energy, inventory, thermal, and processing state. None of
those components can correctly represent a furnace-fuel burn reservoir:

- energy would blend externally received FE with fuel-derived work and lose source preference;
- thermal energy represents physical heat rather than remaining furnace burn units;
- inventory must not contain hidden state tokens;
- processing state describes one operation, not reusable fuel remaining across operations.

A focused combustion component is the smallest complete fix.

## Implemented Candidate

### Machine combustion component

- Stores normalized remaining and total burn work units.
- Rejects non-positive ignition, repeated ignition while already burning, negative values, and
  remaining units greater than the cycle total.
- Consumes requested units without underflow and preserves the last cycle total after the reservoir
  reaches zero, matching the existing crusher gauge contract.
- Owns only durable burn state; Minecraft fuel lookup, item consumption, container remainders, and
  power-source selection remain outside the component.
- Shares the composed machine dirty tracker.
- Marks persistence and client synchronization for runtime changes without issuing scheduler wakes,
  because ignition and consumption occur inside an already-executing machine step.
- Restores validated state with client synchronization only and no replayed work.

### Runtime composition and diagnostics

- Adds combustion to `MachineRuntime` and `MachineComponentState`.
- Includes immutable combustion diagnostics in aggregate runtime diagnostics.
- Preserves one shared dirty-state boundary across every component.

### Snapshot schema 3

- Adds a typed `MachineCombustionSnapshot` to `MachineRuntimeSnapshot`.
- Extends capture, complete prevalidation, and transactional restore.
- Writes combustion state through the NeoForge-only NBT boundary.
- Reads schema 1 and schema 2 snapshots as an empty combustion reservoir, then publishes the
  current schema-3 in-memory contract.
- Rejects malformed schema-3 combustion data before a runtime can be partially restored.

## Automated Coverage

- Ignition, bounded consumption, reservoir exhaustion, and repeated ignition rejection.
- Dirty-flag behavior without redundant scheduler wake requests.
- Restore validation and mutation isolation.
- Runtime shared-dirty-tracker composition.
- Capture and restore of active combustion state.
- Transactional failure leaving all target components unchanged.
- Schema-3 NBT round trip and older-schema empty-combustion migration.

## Architectural Boundaries

- No Minecraft, NeoForge, Mojang, NBT, or SNBT type enters the simulation package.
- The component does not inspect items or recipes.
- No public API is frozen from this candidate.
- No existing block entity uses the component yet.
- The Material Crusher remains unchanged until Milestone 19B2.

## Validation Available in the Delivery Sandbox

- All Minecraft-independent simulation sources compile with Java 21 under `-Xlint:all -Werror`.
- A lightweight assertion runner executes all 65 pure simulation tests successfully, including the
  four new combustion tests and the updated runtime and persistence suites.
- The schema-3 NBT codec and its five focused tests compile and execute against strict minimal
  boundary symbols, proving the codec logic independently of a full NeoForge launch.
- Repository validation passes with the platform quarantine and Milestone 19B1 contract checks.

The sandbox cannot resolve the Gradle distribution or external NeoForge dependency graph. The full
Windows `clean test build` remains the authoritative project gate.

## Gameplay, Save, API, and Dependency Impact

- **Gameplay:** none; no active machine is migrated.
- **Save:** current engine snapshots move to schema 3; schema 1 and 2 remain readable and receive an
  empty combustion state.
- **API:** internal composition and snapshot records change; no stable public integration contract
  has been declared.
- **Dependencies:** none.

## Next Candidate

Milestone 19B2 migrates the Material Crusher onto engine-owned inventory, energy, combustion, and
processing state. It must preserve legacy placed-machine data, hybrid FE/fuel behavior, menu state,
sided automation, recipes, timing, and visuals while replacing independent ticking with explicit
scheduler decisions.
