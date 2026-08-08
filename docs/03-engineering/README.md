# Project Skyblock Engineering Documentation

This section is the authoritative technical documentation for the Project Skyblock engine.

## Current Engine State

The first formal Engine Era baseline remains Milestone 17 at `engine-era-baseline-m17`. Post-baseline
work through Milestone 20A has been implemented and accepted, including tiered validation
environments, transactional inventory/item adaptation, engine-owned combustion state, the Material
Crusher engine migration, and the typed transport network core.

`DOC-SPRING-001` is the current project action and is documentation-only. Production Milestone 20B1
remains blocked until the documentation acceptance gate closes. M20B1 then begins with a guarded
audit of energy state, capability adapters, prior energy-network behavior, and M20A transport
contracts before any production implementation.

Existing blocks and machines remain prototype stress fixtures unless separately promoted as Game Era
content.

## Reading Order

1. [Engine Documentation](ENGINE_DOCUMENTATION.md)
2. [Engineering Principles](ENGINEERING_PRINCIPLES.md)
3. [Engine Guiding Principles](ENGINE_GUIDING_PRINCIPLES.md)
4. [System Architecture](SYSTEM_ARCHITECTURE.md)
5. [Simulation Architecture](SIMULATION_ARCHITECTURE.md)
6. [Architecture Overview](ARCHITECTURE_OVERVIEW.md)
7. [Component Catalog](COMPONENT_CATALOG.md)
8. [Engine Roadmap](ENGINE_ROADMAP.md)
9. [Engine Baseline and Validation](ENGINE_BASELINE_AND_VALIDATION.md)
10. [Engine Changelog](ENGINE_CHANGELOG.md)
11. [Developer Validation Environment](DEVELOPER_VALIDATION_ENVIRONMENT.md)
12. [Architecture Decision Records](architecture-decisions/README.md)
13. [Backend Milestones](milestones/README.md)

## Documentation Categories

- **Architecture:** stable system boundaries and ownership rules
- **ADRs:** durable decisions and their consequences
- **Milestones:** historical and accepted implementation slices
- **Feature specifications:** implementation-aligned specifications or explicitly labeled planned outlines
- **Standards:** required engineering and documentation practices
- **API reference:** public contracts and extension guidance; planned until intentionally frozen

## Definition of Done

A backend milestone is complete only after its required design, implementation, automated validation,
runtime validation where applicable, architecture review, documentation, and source-state evidence are
complete. A milestone may be accepted after the formal M17 baseline without moving that immutable
baseline tag.
