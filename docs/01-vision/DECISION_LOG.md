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

## Decision 0001

Date: 2026-07-21

Topic: Engine baseline, validation, reuse, and future extraction

Decision: Capture a non-freezing Engine Era baseline; classify later changes; retain valid reusable capabilities even when Project Skyblock has not consumed them; evaluate standalone library extraction only after real multi-project evidence exists.

Reasoning: The simulation is intended to support Project Skyblock and potentially future mods. Project-specific usage is not a reliable deletion criterion, while premature publication would create unsupported compatibility obligations.

Alternatives Considered: Freeze the engine after Engine Era; remove unused systems after Game Era; publish a library immediately.

Impacted Documents: ENGINE_BASELINE_AND_VALIDATION.md, ENGINE_ROADMAP.md, ADR-0010-REUSABLE-ENGINE-EVOLUTION.md

Implementation Notes: Record post-baseline changes by classification and produce a final validation report before deciding on extraction.

Status: Accepted

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
