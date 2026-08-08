# ADR-0010 — Reusable Engine Evolution

## Status

Accepted, with the original physical-extraction trigger superseded by Decision 0005.

## Context

Project Skyblock is the first major consumer of a Minecraft-independent simulation backend. Future
mods may need the same scheduler, resources, machine composition, networks, and processing contracts.
The engine is therefore developed as reusable infrastructure rather than gameplay-specific code.

A Project Skyblock feature not using a capability does not prove that the capability has no value.
Removing unused-but-valid engine systems would erase future reuse and distort validation of the
architecture.

The original ADR deferred physical standalone-library extraction until multiple independent projects
needed the engine. Later Project Skyblock authority resolved the repository topology directly: the
current repository remains the 1.21.1 NeoForge engine laboratory through the Engine Era and then
graduates into the single Minecraft/NeoForge-independent shared Project Skyblock engine/library.
Gameplay lives in a separate repository with 1.21.1 and 26.1.x NeoForge branches consuming that one
library. This supersedes only the old multi-project trigger; it does not weaken the evidence-driven
API and architecture rules below.

## Decision

The simulation backend is developed as reusable infrastructure with strict separation from Project
Skyblock-specific gameplay and Minecraft platform adapters.

Capabilities are not removed solely because Project Skyblock has not used them. Removal requires a
positive architectural justification such as invalid design, duplicate ownership, replacement,
security or correctness risk, or maintenance cost that outweighs demonstrated future value.

At the end of the Engine Era, the project captures an engine baseline. Game Era changes are classified
and later compared against that baseline.

The physical graduation target is one shared, version-neutral engine/library repository and one
separate gameplay repository carrying supported Minecraft/NeoForge branches. Engine semantics are
never forked between gameplay branches.

Public API stabilization remains evidence-driven. Physical repository separation does not make every
internal contract public or stable; explicit documentation, versioning, tests, and maintenance
commitment are still required before a contract is published as supported API.

## Consequences

- Core packages must remain free of Project Skyblock-specific progression and presentation logic.
- Minecraft and NeoForge integration remains in adapters.
- General capabilities may be retained before the first gameplay consumer uses them when they already
  have sound ownership, tests, and a documented purpose.
- Premature abstractions are still prohibited; future reuse is not permission to implement imaginary
  systems without a real design need.
- Engine evolution and removals require documentation and migration consideration.
- The baseline and validation report remain evidence for evaluating the engine's predictions and
  public API readiness.
- Graduation produces one shared engine/library and version-specific gameplay branches rather than
  duplicated engine implementations.
