# Project Skyblock Decisions

This document records settled project direction so decisions do not need to be rediscovered later.

## Core Platform

- Minecraft 1.21.1
- NeoForge 21.1.235
- Java 21
- Mod ID: `projectskyblock`
- Package root: `raziel23x.projectskyblock`

## Development Philosophy

- Standalone first
- Optional integrations only
- No hard content-mod dependencies
- Configuration over hardcoding
- Datapack-driven recipes where practical
- Standard NeoForge item, fluid, and energy capabilities
- Shared frameworks before repeated one-off implementations
- Stable features are compiled, tested, committed, and pushed before starting the next feature

## Resource Generators

- Resource Generators produce renewable building and utility materials, never ores.
- Cobblestone, Water, and Lava are the basic generators.
- Additional generators are progression rewards rather than immediate starting blocks.
- All Resource Generators should reuse the same implementation and general model style.
- The center model element represents the generated block, or the source block appearance for fluids.


## Machine Identity and Feedback

- Machine names should describe their full datapack-driven purpose rather than only their first recipe.
- Existing registry IDs remain stable when a user-facing machine name changes, preventing avoidable world and inventory breakage.
- Processing machines should provide visible in-world operating feedback where practical.
- The Material Crusher uses the registry ID `projectskyblock:material_crusher`; legacy remapping is not required during active fresh-world testing.

## Transport

Project Skyblock provides:

- FE generation
- FE storage foundations
- FE cable transport

Project Skyblock does not duplicate:

- Item pipes
- Fluid pipes
- Advanced logistics networks
- Digital storage systems

Vanilla hoppers and buckets remain valid standalone automation, while installed transport mods can interact through standard NeoForge capabilities.

## Scope Exclusions

- No world generation
- No ore generation
- No ore-producing Resource Generators
- No duplicate sieve mechanics
- No duplicate hammer mechanics
- No mandatory external content mods

## Documentation

- `README.md` describes the current working mod.
- `CHANGELOG.md` records completed changes and fixes.
- `TODO.md` tracks planned work and future ideas.
- `DECISIONS.md` records permanent project direction.
- Use one root changelog; do not create feature-specific changelog files.

## Machine and Cable Visual Language

- Machine status indicators use compact LEDs mounted in dark industrial bezels.
- Basic Energy Cables use a slim blue conduit with dark connector housings and minimal texture noise.
- Visual redesigns must not change FE capability or network behavior unless explicitly planned.

- Cable visuals favor continuous straight runs; visible segmentation is reserved for actual turns and junctions.

## Machine System v1.0 Visual Baseline

- Material Crusher GUI layout is frozen unless a future feature requires structural changes.
- Thermal Generator status information lives beneath the Lava and FE gauges.
- Basic Energy Cable straight runs must render as a visually continuous conduit without per-block collars or seams.
- Cable textures remain quieter than machine textures so transport supports rather than dominates factory builds.

## Machine Connections and Activity Effects

- Energy cables use recessed machine-side socket collars while cable-to-cable runs remain seamless.
- Cable activity animation must remain subtle and must not read as a laser beam.
- Generator exhaust uses restrained particle variation rather than identical repeated puffs.

- Structural Energy Frame is a visual/building variant of the Basic Energy Cable and shares the same FE network behavior.
