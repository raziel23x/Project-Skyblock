# Dimensional Science System

## Purpose

Dimensional science explains and supports travel, stabilization, resource access, and logistics between the Overworld, Nether, and End.

It provides skyblock-compatible progression when vanilla access depends on terrain or structures that may not exist.

## Core Design Rules

- Vanilla portals remain valid whenever practical.
- Alternative access should preserve the cost, danger, and significance of dimensional travel.
- Dimensions retain their vanilla identity.
- Alternative gateways must not become cheap teleport blocks.
- The End needs minimal alteration because it is already fundamentally a void dimension.

## Overworld

The Overworld is the primary civilization space.

- Small starting island
- Void beyond generated footholds
- Player-created expansion
- Primary biology, agriculture, forestry, and settlement

## Nether

The Nether requires a deliberately designed bootstrap foothold.

The starting Nether plot should provide only materials required to begin Nether progression. It should not be a miniature buffet containing every Nether resource.

Potential initial materials:

- Netherrack or basalt
- Limited lava access
- Fire source
- Small amounts of quartz-bearing material
- Carefully chosen fungi or spores
- One or more renewable bootstrap paths

The exact contents must be justified by the progression map.

## End

The End should preserve:

- Central dragon island
- Obsidian pillars
- End crystals
- Dragon encounter
- Exit portal
- End gateways where technically practical

Outer terrain may be reduced, limited, or redesigned, but the base encounter should remain recognizably vanilla.

The central island already suits skyblock because it exists within a void. Changes should be minimal and purposeful.

## Alternative Access

Potential access systems include:

- Stabilized dimensional breach
- Resonance gateway
- Engineered portal frame
- Dimensional anchor
- Research-built transport apparatus

Alternative access must require equivalent progression to vanilla access, not simply bypass it.

## Dimensional Materials

The first obtainable specimen may be finite or limited. Later progression should provide renewable cultivation, synthesis, trade, or recovery where appropriate.

## Cross-Dimensional Logistics

Late-game infrastructure may support:

- Item transfer
- Fluid transfer
- Energy transfer
- Signal transfer
- Player and entity transport

These systems should require stable anchors and significant infrastructure.

## Vanilla Integration

- Nether portals remain functional.
- End portals remain functional when obtainable.
- Eyes of Ender, blaze powder, obsidian, and portal-related vanilla materials retain value.
- The dragon fight remains a major milestone.
- End gateways and vanilla post-dragon mechanics should be preserved where compatible with void generation.

## Restrictions

- No arbitrary portal item that teleports freely with trivial cost.
- No alternate dimension path that makes vanilla materials irrelevant.
- Do not over-lore normal Minecraft behavior.
- Do not remove iconic encounters merely because the world is void.
- Do not place every dimension-exclusive resource on the arrival platform.

## Technical Questions

- How does the selected 1.21.1 void generator handle the central End island?
- Can outer End island generation be limited without breaking gateways?
- How are Nether biomes represented when terrain is absent?
- How are fortress- and structure-dependent resources introduced?
- Which world-generation hooks are safest under NeoForge?

---

## Feature Review Checklist

Before implementation, confirm that the proposed feature:

- complements vanilla Minecraft rather than disabling it;
- has an understandable cause and effect;
- introduces knowledge before automation;
- supports the journey from survival to civilization;
- avoids unnecessary grind and arbitrary recipe chains;
- can be expressed through data where practical;
- has a clear place in progression;
- defines failure, recovery, and renewable paths;
- does not accidentally invalidate several other systems.

## Status

Design specification. Exact values, recipes, machine costs, and timings remain subject to playtesting.
