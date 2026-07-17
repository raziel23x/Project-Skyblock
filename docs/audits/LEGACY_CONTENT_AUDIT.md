# Legacy Content Audit

## Already rebuilt

- Cobblestone Generator
- Water Generator
- Lava Generator

## Approved for reconstruction

- Clay Generator
- Gravel Generator
- Sand Generator
- Red Sand Generator
- Netherrack Generator
- Soul Sand Generator
- End Stone Generator
- Obsidian Generator
- Quartz Generator

## Decision required

- Dirt Generator
- Grass Block Generator

## Important architecture rule

Do not recreate the Forge 1.16 pattern of separate classes for every generator. Use the modern shared resource-generator implementation with per-generator registration/configuration.

## Crusher rule

Treat the legacy Cobblestone Crusher as reference-only. Restore missing behavior through the modern Material Crusher rather than restoring the old class structure.
