# Project Skyblock Engineering Documentation

This section is the authoritative technical documentation for the Project Skyblock engine.

## Current Engine State

The backend has completed implementation work through Milestone 17. The current baseline candidate
includes the first engine-owned prototype machine, level-scoped scheduler integration, failure
isolation, transactional persistence, schema-2 stateful item identity, an energy capability
adapter, deterministic topology hardening, and atomic material-data publication.

The next gate is local Gradle and in-game validation. After that, the next prototype conversion is
selected by which unproven engine contracts it can stress most effectively; it is not selected as
a promise of final gameplay content.

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
- **Milestones:** historical implementation slices
- **Feature specifications:** approved design targets
- **Standards:** required engineering and documentation practices
- **API reference:** public contracts and extension guidance

## Definition of Done

A backend milestone is complete only after design, implementation, compilation, runtime validation, architecture review, documentation, and commit preparation are complete.
