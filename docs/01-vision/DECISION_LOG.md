# DECISION_LOG.md

Use this document to record major project decisions.

## Template

### Decision ####

Date:

Topic:

Decision:

Reasoning:

Alternatives Considered:

Impacted Documents:

Implementation Notes:

Status:
- Proposed
- Accepted
- Deprecated
- Superseded

## Decision 0001

Date: 2026-07-21

Topic: Engine baseline, validation, reuse, and future extraction

Decision: Capture a non-freezing Engine Era baseline; classify later changes; retain valid reusable capabilities even when Project Skyblock has not consumed them; evaluate standalone library extraction only after real multi-project evidence exists.

Reasoning: The simulation is intended to support Project Skyblock and potentially future mods. Project-specific usage is not a reliable deletion criterion, while premature publication would create unsupported compatibility obligations.

Alternatives Considered: Freeze the engine after Engine Era; remove unused systems after Game Era; publish a library immediately.

Impacted Documents: ENGINE_BASELINE_AND_VALIDATION.md, ENGINE_ROADMAP.md, ADR-0010-REUSABLE-ENGINE-EVOLUTION.md

Implementation Notes: Record post-baseline changes by classification and produce a final validation report before deciding on extraction.

Status: Superseded in part by Decision 0005. The non-freezing baseline, reusable-boundary, and evidence-driven API principles remain accepted. The old requirement for multiple external projects before physical library separation no longer controls the repository topology.

## Decision 0002

Date: 2026-07-21

Topic: Modular multiblock architecture

Decision: Use controller-owned simulation composition with physical module blocks, stable external automation interfaces, transactional formation, bounded event-driven validation, and explicit state-preserving teardown and reformation.

Reasoning: Multiblocks should provide meaningful physical progression without allowing Minecraft block state or member blocks to duplicate authoritative machine resources.

Alternatives Considered: Fixed decorative structures; member-owned inventories and tanks; per-tick full structure scans.

Impacted Documents: MACHINES.md, MULTIBLOCK_ARCHITECTURE.md

Implementation Notes: Implementation remains deferred until a real production machine proves the required module families.

Status: Accepted

## Decision 0003

Date: 2026-07-21

Topic: Localization and dependency-update policy

Decision: Localize all player-facing text from first implementation. Update NeoForge or other dependencies only for a documented concrete project benefit, not merely because a newer version exists.

Reasoning: Presentation must remain separate from simulation authority, and dependency churn must justify its migration and validation cost.

Alternatives Considered: Add localization late; track every latest dependency release automatically.

Impacted Documents: LOCALIZATION_STANDARD.md, ARCHITECTURE_REVIEW_CHECKLIST.md

Implementation Notes: Current platform baseline is Minecraft 1.21.1 with NeoForge 21.1.235.

Status: Accepted

## Decision 0004

Date: 2026-07-23

Topic: Greenfield engine origin, scale model, and prototype lifecycle

Decision: Treat the Project Skyblock simulation engine as a new scale-driven engine being created
from first principles. Existing machines, blocks, tools, armor, menus, and utility items are
prototype stress fixtures. They may be converted to expose engine requirements, then archived or
removed after their unique evidence is captured.

Reasoning: The engine emerged after ordinary Minecraft and NeoForge implementation raised questions
about hundreds, thousands, and extremely large conceptual quantities. Runtime work should scale
with meaningful activity and state change rather than raw represented object count wherever
practical. Calling the effort a rewrite implies a previous engine that did not exist and risks
confusing prototype conversion with final content design.

Alternatives Considered: Continue ordinary per-object ticking; design every engine abstraction
before testing; treat migrated prototypes as permanent Game Era content.

Impacted Documents: ADR-0011-GREENFIELD-SCALE-DRIVEN-ENGINE.md, ENGINE_ROADMAP.md,
ENGINE_GUIDING_PRINCIPLES.md, TODO.md

Implementation Notes: Each prototype migration identifies the contracts it stresses, captures
regression tests, and records whether the prototype still provides unique validation value.

Status: Accepted

## Decision 0005

Date: 2026-08-08

Topic: Physical repository topology after Engine Era graduation

Decision: Keep the current `Project-Skyblock` repository as the 1.21.1 NeoForge engine laboratory
through the Engine Era. At graduation, this repository becomes the single Minecraft/NeoForge-independent
shared Project Skyblock engine/library. Project Skyblock gameplay moves to one separate gameplay
repository with `MC-1.21.1-NeoForge` and `MC-26.1.x-NeoForge` branches, both consuming the same shared
library. Engine semantics must not be duplicated between gameplay branches.

Reasoning: The semantic architecture already requires one version-neutral engine and replaceable
platform adapters. Making the physical repository topology explicit prevents duplicated engine
implementations while allowing both Minecraft generations to expose platform migration cost.

Alternatives Considered: Keep physical topology indefinitely open; duplicate the engine per Minecraft
branch; require unrelated external projects before separating the shared engine.

Impacted Documents: ENGINE_ROADMAP.md, ENGINE_BASELINE_AND_VALIDATION.md,
ADR-0010-REUSABLE-ENGINE-EVOLUTION.md

Implementation Notes: The final public/library artifact name remains undecided. Graduation does not
freeze a stable public API automatically; API publication remains an intentional versioned decision.

Status: Accepted
