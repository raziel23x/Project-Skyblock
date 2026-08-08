# Engine Roadmap

## Era Sequence

```text
Research Era
    ↓
Engine Era
    ↓
Engine Baseline / Validation
    ↓
Engine Graduation
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

### Accepted Through Milestone 20A

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
- Milestone 20A typed transport topology, profiles, shared-edge reservations, fairness, and stable
  no-progress sleeping

### Implemented Candidate — Milestone 20B1

- Added optimistic `MachineEnergyTransaction` candidates over machine-owned energy endpoints.
- Added monotonic endpoint state versions, stale-candidate rejection, commit/conflict diagnostics,
  one-publication dirty/wake behavior, and restore invalidation.
- Updated the NeoForge energy adapter so simulation stages disposable candidates while real calls
  commit a fresh transaction; FE simulation is explicitly not a reservation.
- Routed durable machine-energy validation/restore through `MachineEnergyComponent` ownership.
- Added dedicated endpoint/adapter tests and permanent Milestone 5/6 energy-network regression tests.
- Passed Project Skyblock validation plus clean Gradle test/build in Termux/Debian.

### Current Gate

- Run standalone and integration runtime regression on a launch-capable PC and accept M20B1 only after
  the live prototype energy-capability path remains clean.

### Near-Term Planned

- Accept M20B1 after the runtime regression gate.
- Continue evidence-driven Milestone 20B energy dispatch, declared route-loss accounting, endpoint
  validation, and multi-endpoint commit policy only after M20B1 acceptance.
- Migrate the Basic Energy Cable in Milestone 20C only after the energy policy is proven.
- Keep transport profile names and speeds data-facing; do not freeze Mk 1/Mk 2/Mk 3 progression.

### Later Planned When Proven Necessary

- Machine fluid component
- Machine gas component
- fluid and gas network behavior
- chemistry-supporting resource properties

## Engine Baseline

Milestone 17 is the first formal Engine Era baseline, tagged as `engine-era-baseline-m17` after
local Gradle, standalone runtime, persistence, wake-path, and Linux CI validation. Milestone 18 and
accepted post-baseline milestones extend the engine without redefining the immutable M17 tag. The
baseline is a comparison point, not an engine freeze. See [Engine Baseline and Validation](ENGINE_BASELINE_AND_VALIDATION.md).

## Engine Graduation

The physical repository topology is now decided.

Through the Engine Era, this `Project-Skyblock` repository remains the Minecraft 1.21.1 / NeoForge
engine laboratory. At graduation it becomes the single Minecraft/NeoForge-independent shared Project
Skyblock engine/library repository.

Project Skyblock gameplay then lives in one separate repository with version branches for 1.21.1 and
26.1.x NeoForge. Both branches consume the same shared library; engine semantics are never duplicated
between gameplay versions.

Graduation does not automatically freeze a public API. Public contracts are promoted deliberately
only after their ownership, tests, versioning, and maintenance obligations are justified. The final
public/library artifact name remains undecided.

## Archive

Research Era implementations are archived after the backend replacement is proven. They are not allowed to remain as accidental production architecture.

## Journal Era

The Journal becomes the first production feature and the central progression interface.

## Game Era

Production content implements knowledge-driven progression, material decomposition, ecology, processing, logistics, energy, chemistry, and civilization systems through the validated engine.
