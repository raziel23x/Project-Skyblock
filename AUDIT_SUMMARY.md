# Project Skyblock — Audit Pass 1

## Scope

Compared:

- Active branch: `MC-1.21.1-NeoForge`
- Legacy reference branch: `MC-V1.16.X`

This package does not copy legacy Java into the active NeoForge source tree. Legacy material remains reference-only until a feature is deliberately rebuilt, reviewed, and tested.

## Immediate conclusions

1. The NeoForge rewrite already contains the retained core equipment, Repair Gem, Mixing Bowl, reagents, Cobblestone/Water/Lava generators, Material Crusher, Thermal Generator Mk I, and FE cable systems.
2. The remaining retained legacy generator concepts should be staged and rebuilt one at a time using the shared `ResourceGeneratorBlock` / `ResourceGeneratorBlockEntity` architecture.
3. Legacy generated recipes, advancements, tags, loot tables, models, and localization must not be restored wholesale. Each asset must be reviewed against modern registry IDs and the current progression design.
4. Dirt and Grass Block generators are not currently listed in the active renewable-generator roadmap and therefore remain undecided/reference-only.
5. The legacy Cobblestone Crusher should not be restored as a separate machine unless it provides behavior not covered by the modern Material Crusher.

## First implementation target

Recommended first migration batch:

- Clay Generator
- Gravel Generator
- Sand Generator
- Red Sand Generator

These are already listed as early-progression generators in the current TODO and fit the shared generator architecture.
