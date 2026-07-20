# Project Skyblock Documentation

This folder is the complete, replacement-ready documentation system for **Project Skyblock**.

It is designed to be copied into the repository as one authoritative `docs` folder. The documentation distinguishes current implementation, accepted architecture, planned systems, and historical milestones.

## Start Here

1. [Project Constitution](01-vision/PROJECT_CONSTITUTION.md)
2. [Mod Vision](01-vision/MOD_VISION.md)
3. [Gameplay Pillars](02-game-design/GAMEPLAY_PILLARS.md)
4. [Progression Atlas](02-game-design/progression/PROGRESSION_ATLAS.md)
5. [Engineering Principles](03-engineering/ENGINEERING_PRINCIPLES.md)
6. [Engineering Documentation](03-engineering/README.md)
6. [Engine Manual](03-engineering/ENGINE_DOCUMENTATION.md)
7. [Engine Roadmap](03-engineering/ENGINE_ROADMAP.md)
8. [Integration Contract](04-integration/INTEGRATION_CONTRACT.md)
9. [Reference Index](05-reference/REFERENCE_INDEX.md)

## Directory Map

```text
docs/
├── 01-vision/        Project identity, constraints, language, and long-range direction
├── 02-game-design/   Gameplay pillars, progression, and system design
├── 03-engineering/   Engine architecture, decisions, milestones, standards, and API guidance
├── 04-integration/   Contracts for Minecraft, NeoForge, modpacks, and external integrations
└── 05-reference/     Glossary, resource bible, matrices, and fast lookup material
```

## Canonical-Source Rule

Each subject has one authoritative location. Other documents should link to that source rather than restating it. Planned features must be labeled **Planned**; implemented features must be supported by the repository.

## Replacement Safety

This documentation set is intended to replace the repository's existing `docs` folder in one operation. See [Replacement Instructions](REPLACEMENT_INSTRUCTIONS.md).
