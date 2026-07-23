# Backend Milestone 17 — Baseline Hardening and Contract Stabilization

## Purpose

Harden the greenfield engine contracts exposed by the first complete machine vertical slice and the
repository baseline audit before additional prototype workloads depend on them.

This milestone is not cosmetic cleanup. It corrects ambiguous or unsafe boundaries while the engine
is still young enough to change without public compatibility obligations.

## Implemented

### Energy semantics

- Renamed machine-buffer receive and extract limits from misleading per-tick terminology to
  explicit **per-operation** limits.
- Applied the same terminology to prototype cable segments, topology connections, routes, and hop
  validation so every current transfer contract has one meaning.
- Preserved actual NeoForge capability-call behavior without introducing premature global tick
  counters.
- Left shared-edge contention and aggregate network budgets as separate future network concerns.

### Transaction-safe persistence

- Added full-snapshot validation before any machine component is mutated.
- Made energy, inventory, thermal, and processing restoration commit only after the complete
  candidate is valid.
- Added exact thermal restoration without replaying ordinary runtime wake behavior.
- Added schema validation and safe failure handling at the block-entity adapter boundary.
- Advanced the machine snapshot schema to version 2.

### Stateful item identity

- Added an opaque, adapter-owned `SimulationItemState` contract.
- Included item state in `SimulationItemKey` equality and ordering so damaged, enchanted, or
  otherwise component-bearing items cannot silently merge as one stateless item.
- Bounded canonical component-state payloads to 64 KiB per item identity and defensively copied
  them at every engine boundary.
- Added quarantined platform-persistence support for opaque component state plus schema-1 stateless migration.
- Explicitly prohibited NBT/SNBT from being smuggled through opaque engine payloads.

### Scheduler failure isolation

- Added failure stages and a failure-observer contract.
- Isolated participant, context, result-application, and platform-observer failures.
- Isolated deferred block-entity integration flushes so one failing Minecraft adapter cannot abort
  unrelated machines waiting in the same level batch.
- Invalidated only the failed participant and continued bounded execution for unrelated work.
- Added level-aware diagnostics without allowing logging failures to damage scheduler state.

### Deterministic network topology

- Made connection ordering consistent with equality.
- Rejected conflicting parallel definitions for the same endpoint pair explicitly.
- Published both adjacency references transactionally.

### Atomic material publication

- Added one immutable `MaterialRegistrySnapshot` containing definitions and source provenance.
- Prepared, cross-validated, and published materials and processing routes through one volatile
  generation change.
- Rejected malformed candidates while retaining the last-known-good snapshot.
- Rejected an unexpectedly empty material-and-route candidate rather than replacing a valid
  generation with silent absence.
- Namespaced the custom data roots under `projectskyblock/` so unrelated mods cannot be parsed as
  Project Skyblock definitions merely because they use generic `materials` folders.
- Preserved deterministic source and identifier ordering.

### Build and validation tooling

- Added project-resource and documentation integrity validation to CI.
- Pinned the Gradle wrapper distribution checksum.
- Restored the executable bit on `gradlew`.
- Added test-report and development-JAR CI artifacts.
- Enabled the ModDevGradle unit-test runtime for tests that legitimately exercise quarantined
  Minecraft/NeoForge adapter boundaries.
- Expanded automated regression coverage for energy, thermal, scheduler failure isolation,
  topology, stateful item identity, transactional persistence, NBT migration, and material
  snapshots.

## Architectural Decisions

- Per-operation machine-buffer limits remain local component policy. Aggregate per-simulation-step
  budgets will be introduced only when a real network or shared-resource workload proves their
  required ownership and fairness model.
- Item components remain opaque to the Minecraft-independent engine. Adapters own a documented
  typed/component encoding and validation; the engine owns identity-safe comparison. NBT/SNBT is
  never accepted as the runtime representation.
- One invalid reload candidate never becomes a partially visible registry generation.
- A failed simulation participant cannot stop unrelated participants or remain falsely active.

## Validation Performed

- Compiled all Minecraft-independent simulation sources with Java 21, all lint warnings enabled,
  and warnings treated as errors.
- Executed 54 simulation tests through an independent local runner; all passed.
- Compiled the added adapter/material tests against the included NeoForge development artifacts.
- Executed six NBT-codec and material-snapshot boundary tests against strict in-memory test doubles;
  all passed.
- Ran targeted failure probes for energy accounting, topology conflict handling, scheduler
  isolation, transactional restore, stateful item identity, and thermal equilibrium.
- Ran `tools/validate_project.py` successfully against JSON, textures, models, documentation links,
  and documentation-manifest coverage.

A fresh Gradle build was not available in the audit sandbox because the Gradle distribution and its
external dependency graph were not cached and network resolution was unavailable. Local Gradle,
GameTest, and in-game validation remain required before this milestone becomes the tagged Engine
Era baseline.

## Gameplay, Save, API, and Dependency Impact

- **Gameplay:** no intended balance or progression changes; affected content remains prototype
  stress-test material.
- **Save:** schema-1 stateless machine snapshots migrate to schema 2; malformed candidates fail
  closed. Existing prototype worlds still require local validation.
- **API:** no stable public API was frozen. Internal names changed before external compatibility
  obligations exist.
- **Dependencies:** no new runtime dependency was added.

## Remaining Validation

- Run `gradlew clean test build` locally.
- Validate the Creative Energy Cell in-game after Milestone 16 and this hardening pass.
- Confirm sleeping-cell receiver wake/refill behavior under actual NeoForge capabilities.
- Run bare-core, optional-integration, scripting-enabled, and large-modpack validation tiers.
- Use the next prototype migration as a stress test, not as a commitment to permanent content.
