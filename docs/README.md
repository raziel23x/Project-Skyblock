# Project Skyblock Documentation

This is the live documentation tree for Project Skyblock. It distinguishes current implementation,
accepted architecture, planned systems, historical milestones, integration boundaries, and reference
material. It is maintained in place with the repository; it is not a replacement package.

## Start Here

1. [Project Constitution](01-vision/PROJECT_CONSTITUTION.md)
2. [Mod Vision](01-vision/MOD_VISION.md)
3. [Gameplay Pillars](02-game-design/GAMEPLAY_PILLARS.md)
4. [Progression Atlas](02-game-design/progression/PROGRESSION_ATLAS.md)
5. [Engineering Principles](03-engineering/ENGINEERING_PRINCIPLES.md)
6. [Engineering Documentation](03-engineering/README.md)
7. [Engine Manual](03-engineering/ENGINE_DOCUMENTATION.md)
8. [Engine Roadmap](03-engineering/ENGINE_ROADMAP.md)
9. [Integration Contract](04-integration/INTEGRATION_CONTRACT.md)
10. [Reference Index](05-reference/REFERENCE_INDEX.md)
11. [Document Status Index](DOCUMENT_STATUS_INDEX.md)

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

Each subject has one authoritative location. Other documents should link to that source rather than
restating it. Planned features must be labeled **Planned**; implemented or accepted milestone status
must be supported by repository and validation evidence. Historical files must identify themselves as
historical rather than competing with current authority.

## Maintenance

`MANIFEST.md` inventories the tracked Markdown documentation tree. `DOCUMENT_STATUS_INDEX.md` records
purpose and authority/disposition boundaries established by DOC-SPRING-001. Documentation changes
must keep navigation, status, and relative links consistent with the repository state.
