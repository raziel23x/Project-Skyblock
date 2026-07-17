# Project Skyblock — Audit Pass 1

## Scope

Compared:

- Active branch: `MC-1.21.1-NeoForge`
- Legacy reference branch: `MC-V1.16.X`

This package does not copy legacy Java into the active NeoForge source tree. Legacy material remains reference-only until a feature is deliberately rebuilt, reviewed, and tested.

## Immediate conclusions

1. The NeoForge rewrite already contains the retained core equipment, Repair Gem, Mixing Bowl, reagents, Cobblestone/Water/Lava generators, Material Crusher, Thermal Generator Mk I, and FE cable systems.
2. Remaining retained legacy generator concepts should be rebuilt one at a time using the shared generator architecture.
3. Legacy recipes, advancements, tags, loot tables, models, and localization must not be restored wholesale.
4. Dirt and Grass Block generators remain undecided/reference-only.
5. The legacy Cobblestone Crusher should not return as a separate machine unless it provides behavior missing from the modern Material Crusher.

## Recommended first migration batch

- Clay Generator
- Gravel Generator
- Sand Generator
- Red Sand Generator
