# ADR-0010 — Reusable Engine Evolution

## Status

Accepted

## Context

Project Skyblock is the first major consumer of a Minecraft-independent simulation backend. Future
mods may need the same scheduler, resources, machine composition, networks, and processing contracts.
The engine may eventually be extracted into a standalone NeoForge library used by multiple projects.

A Project Skyblock feature not using a capability does not prove that the capability has no value.
Removing unused-but-valid engine systems would erase future reuse and distort the final validation of
the architecture.

At the same time, publishing a public library before multiple consumers and stable contracts exist
would create premature compatibility obligations.

## Decision

The simulation backend is developed as reusable infrastructure with strict separation from Project
Skyblock-specific gameplay and Minecraft platform adapters.

Capabilities are not removed solely because Project Skyblock has not used them. Removal requires a
positive architectural justification such as invalid design, duplicate ownership, replacement,
security or correctness risk, or maintenance cost that outweighs demonstrated future value.

At the end of the Engine Era, the project captures an engine baseline. Game Era changes are classified
and later compared against that baseline.

Standalone library extraction remains evidence-driven. It occurs only when multiple projects need the
engine, contracts have proven stable, independent tests exist, Project Skyblock-specific dependencies
are absent from core packages, and long-term versioned API maintenance is accepted.

## Consequences

- Core packages must remain free of Project Skyblock-specific progression and presentation logic.
- Minecraft and NeoForge integration remains in adapters.
- General capabilities may be retained before the first project consumes them when they already have
  sound ownership, tests, and a documented purpose.
- Premature abstractions are still prohibited; future reuse is not permission to implement imaginary
  systems without a real design need.
- Engine evolution and removals require documentation and migration consideration.
- The baseline and validation report become evidence for eventual extraction.
