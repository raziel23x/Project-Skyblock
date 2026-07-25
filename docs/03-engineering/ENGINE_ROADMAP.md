# Engine Roadmap

## Era Sequence

```text
Research Era
    ↓
Engine Era
    ↓
Engine Baseline
    ↓
Archive
    ↓
Journal
    ↓
Game Era
```

## Research Era

Existing blocks, machines, items, menus, recipes, and compatibility code are prototypes and evidence. They remain available while the production engine is built.

Prototype conversion is deliberate stress testing. A converted object is not automatically final
Game Era content. It remains only while it exercises a distinct contract or provides useful
regression evidence.

Milestones are planning checkpoints rather than fixed commitments. Audit evidence may split, merge,
expand, reorder, or retire them when that produces a smaller complete contract or avoids premature
abstraction.

## Engine Era

### Completed

- Simulation foundation
- Scheduler
- Thermal core
- Energy simulation
- Energy network topology
- Energy network transfer
- Network sleeping
- Machine framework
- Machine energy component
- Machine inventory component
- Machine thermal component
- Scheduler wake coalescing and active-execution safety
- Machine composition proof and shared dirty-state ownership
- Machine processing component
- Machine runtime persistence contract and NeoForge block-entity bridge
- Level-scoped scheduler driver
- First engine-owned machine and NeoForge energy capability adapter
- Baseline hardening and contract stabilization
- Transaction-safe persistence and machine snapshot schema 2
- Scheduler failure isolation
- Opaque item-state identity
- Atomic last-known-good material publication
- Tagged Milestone 17 Engine Era baseline
- Tiered standalone and integration developer validation environments
- Full integration runtime validation, including optional Curios Repair Gem behavior
- Milestone 19A transactional inventory and item capability contracts
- Milestone 19B1 engine-owned combustion state and snapshot schema 3
- Milestone 19B2 Material Crusher engine migration and both-profile validation

### Active Candidate

- Milestone 20A — typed transport topology, profiles, shared-edge reservations, fairness, and
  stable no-progress sleeping

### Near-Term Planned

- Run the complete Windows Gradle and standalone/integration runtime suite for Milestone 20A
- Implement Milestone 20B energy-specific dispatch, route loss, endpoint validation, and commit policy
- Migrate the Basic Energy Cable in Milestone 20C only after the energy policy is proven
- Keep transport profile names and speeds data-facing; do not freeze Mk 1/Mk 2/Mk 3 progression

### Later Planned When Proven Necessary

- Machine fluid component
- Machine gas component
- fluid and gas network behavior
- chemistry-supporting resource properties

## Engine Baseline

Milestone 17 is the first formal Engine Era baseline, tagged as `engine-era-baseline-m17` after
local Gradle, standalone runtime, persistence, wake-path, and Linux CI validation. Milestone 18 adds
the permanent optional-integration test harness after the baseline; it validates adapters and
ecosystem behavior without redefining core baseline ownership. This is a comparison point, not an
engine freeze. See [Engine Baseline and Validation](ENGINE_BASELINE_AND_VALIDATION.md).

## Archive

Research Era implementations are archived after the backend replacement is proven. They are not allowed to remain as accidental production architecture.

## Journal Era

The Journal becomes the first production feature and the central progression interface.

## Game Era

Production content implements knowledge-driven progression, material decomposition, ecology, processing, logistics, energy, chemistry, and civilization systems through the validated engine.

## Potential Engine Extraction

After the Engine Era baseline is captured and the simulation APIs have proven stable through real gameplay and additional real projects, evaluate extracting the Minecraft-independent backend into a standalone NeoForge library mod shared by Project Skyblock and future mods. This is a future direction evaluated through evidence, not a current publishing milestone.

Extraction should be considered only when:

- multiple mods genuinely need the engine;
- core packages remain free of Project Skyblock-specific dependencies;
- component and simulation APIs have stabilized;
- independent automated tests exist;
- long-term versioned public API maintenance is justified.

Until then, backend packages and platform adapters remain cleanly separated without introducing premature publishing, compatibility, or public-API obligations. Valid engine capabilities are not removed merely because Project Skyblock has not consumed them yet; removal requires an affirmative architectural justification. See ADR-0010 and the engine baseline policy.
