# Legacy Reference Archive

This directory preserves superseded Proof of Concept work after an engine-driven replacement has been implemented and verified.

It is intentionally outside active NeoForge source and resource paths.

## Why It Exists

Project Skyblock began by cleaning and porting working 1.16-era content. The Material Crusher, hybrid fuel-or-power operation, creative power source, Thermal Generator, energy cables, utility generators, equipment, blocks, and items exposed repeated problems involving state ownership, sided access, scheduling, persistence, networking, and TPS.

That work led directly to the reusable simulation engine. It should not be deleted merely because the production architecture replaces it.

## Archive Rules

- Archive only after the engine replacement works in-game.
- Never place archived Java under `src/main/java`.
- Never place archived assets or data under `src/main/resources`.
- Preserve behavior and historical context, not active dependencies.
- Record every migration in `MIGRATION_LEDGER.md`.
- Archived files are read-only references and must never be compiled or loaded.

## Intended Layout

```text
legacy-reference/
├── assets/          Retired models, textures, blockstates, and other assets
├── code-reference/  Retired Java and implementation notes
├── data/            Retired recipes, tags, loot tables, and other data
├── MIGRATION_LEDGER.md
├── POC_MIGRATION_PLAN.md
└── README.md
```

The directories may remain empty until the corresponding active PoC implementation has been replaced and verified.
