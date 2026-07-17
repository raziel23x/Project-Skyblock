# Vanilla Content Expansion Audit

## Purpose

This audit reviews vanilla blocks and material families added after Minecraft 1.16.x and through Minecraft 1.21.1.

The goal is not to give every block its own generator. Each material is evaluated for the most appropriate Project Skyblock integration:

- Dedicated resource generator
- Material Crusher recipe
- Mixing Bowl or reagent recipe
- Progression or machine recipe
- Vanilla-renewable route
- Decorative derivative from a base material
- Intentionally unsupported

This document is the starting point for implementation planning. Final recipes, costs, processing times, and progression requirements must be tested in-game before release.

---

## Standing Rule

> New vanilla blocks must be evaluated by material family, vanilla renewability, balance, and progression value before receiving a Project Skyblock generator.

Decorative variants should normally be produced from a renewable base material through vanilla crafting or stonecutting.

Project Skyblock should not create a separate machine for every slab, stair, wall, polished block, brick block, oxidation state, or waxed state.

---

## Classification Key

### Generator Candidate

A base material that fits passive or powered renewable production and does not already have a satisfying Skyblock route.

### Crusher Candidate

A material best obtained by processing another renewable material.

### Mixing or Reagent Candidate

A material suited to a shaped, shapeless, reusable-bowl, or reagent-based recipe.

### Progression Candidate

A valuable or unusual resource that should require power, multiple steps, special inputs, or later-game infrastructure.

### Vanilla Route

Vanilla already provides an intentional renewable method that Project Skyblock should preserve or assist rather than replace.

### Decorative Derivative

The base material may need support, but its decorative family should use crafting, stonecutting, oxidation, waxing, or other vanilla mechanics.

### Restricted or Unsupported

The material is tied to exploration, unique structures, archaeology, boss progression, or special loot and should not be mass-produced without a deliberate design decision.

---

# Minecraft 1.17 and 1.18 Material Families

## Tuff

**Classification:** Generator Candidate  
**Priority:** High

### Recommended Project Skyblock Route

Add a dedicated Tuff Generator after the initial Sand, Gravel, Clay, and Red Sand reconstruction batch.

### Rationale

Tuff is a base stone material with a large decorative family in Minecraft 1.21. It is useful in building, stonecutting, and Trial Chamber-inspired construction.

### Output Scope

The generator should produce only:

```text
minecraft:tuff
```

Do not create separate generators for:

- Tuff Slab
- Tuff Stairs
- Tuff Wall
- Chiseled Tuff
- Polished Tuff
- Polished Tuff Slab
- Polished Tuff Stairs
- Polished Tuff Wall
- Tuff Bricks
- Tuff Brick Slab
- Tuff Brick Stairs
- Tuff Brick Wall
- Chiseled Tuff Bricks

Those blocks are Decorative Derivatives and should remain available through vanilla crafting and stonecutting.

### Initial Progression Idea

Possible inputs:

- Cobblestone
- Gravel
- Lava or FE
- Gray, green, or black reagent component

The exact route remains undecided.

---

## Calcite

**Classification:** Generator Candidate or Crusher Candidate  
**Priority:** High

### Recommended Project Skyblock Route

Prototype both of these approaches:

1. Dedicated Calcite Generator
2. Material Crusher recipe using renewable stone and a white mineral input

Select the route that gives better progression separation from Tuff.

### Balance Concern

Calcite is primarily decorative. Its production should be easier than advanced resources but later than basic Cobblestone.

### Candidate Recipe Concepts

- Diorite plus white reagent
- Dripstone Block plus white reagent
- Bone-derived mineral processing
- Amethyst-adjacent progression without consuming nonrenewable budding amethyst

---

## Dripstone Block

**Classification:** Vanilla Route with optional acceleration  
**Priority:** Medium

### Vanilla Behavior

Pointed Dripstone can grow when configured with Dripstone Blocks and water. Dripstone also participates in lava collection and mud-to-clay conversion.

### Recommended Project Skyblock Route

Do not begin with a passive Dripstone Block Generator.

Instead, provide an initial renewable entry point through one of these:

- Mixing Bowl recipe for Pointed Dripstone
- Crusher recipe for Pointed Dripstone
- Trader-independent progression recipe
- Machine recipe that creates the first Dripstone Block

After the player obtains starter dripstone, vanilla growth should remain meaningful.

### Possible Optional Support

A later machine upgrade could accelerate dripstone growth without replacing vanilla mechanics.

---

## Pointed Dripstone

**Classification:** Vanilla Route starter recipe  
**Priority:** Medium

### Recommended Project Skyblock Route

Provide a balanced recipe for obtaining the first Pointed Dripstone in a world where wandering-trader access is unreliable.

Avoid unlimited direct output if vanilla growth is functional and practical.

---

## Deepslate and Cobbled Deepslate

**Classification:** Generator Candidate  
**Priority:** High

### Recommended Project Skyblock Route

Add a Cobbled Deepslate Generator.

The generator should output:

```text
minecraft:cobbled_deepslate
```

### Decorative and Processing Derivatives

Use vanilla crafting, smelting, and stonecutting for:

- Deepslate
- Polished Deepslate
- Deepslate Bricks
- Cracked Deepslate Bricks
- Deepslate Tiles
- Cracked Deepslate Tiles
- Chiseled Deepslate
- Slabs
- Stairs
- Walls

### Naming Decision

Prefer:

```text
Cobbled Deepslate Generator
```

This matches the actual output and avoids implying that Silk Touch Deepslate is generated directly.

---

## Smooth Basalt

**Classification:** Crusher Candidate or machine conversion  
**Priority:** Medium

### Recommended Project Skyblock Route

Do not add a dedicated Smooth Basalt Generator initially.

Candidate routes:

- Basalt processed in the Material Crusher
- Basalt plus Calcite in the Mixing Bowl
- Thermal processing of Basalt
- Standard vanilla smelting support when appropriate

### Related Base Material

If Basalt itself is difficult to obtain in the target Skyblock progression, review whether Project Skyblock should provide a Basalt Generator or a controlled basalt-creation recipe.

---

## Amethyst Block

**Classification:** Progression Candidate  
**Priority:** Medium

### Recommended Project Skyblock Route

Do not generate Budding Amethyst.

Provide renewable Amethyst Shards through a later-game powered or multi-stage process.

Possible design:

1. Produce Calcite.
2. Process Quartz, Calcite, and reagent material.
3. Create Amethyst Shards through the Material Crusher or a future crystal-growing machine.
4. Craft Amethyst Blocks through vanilla recipes.

### Restricted Block

```text
minecraft:budding_amethyst
```

Budding Amethyst should remain unavailable through ordinary crafting unless a separate high-cost progression design is approved.

---

## Moss Block

**Classification:** Vanilla Route starter recipe  
**Priority:** Medium

### Recommended Project Skyblock Route

Provide a controlled way to obtain the first Moss Block.

Once obtained, vanilla bone-meal spreading should remain the main renewable method.

Possible starter routes:

- Mixing Bowl using dirt, vines, seeds, and green reagent
- Composting progression
- Crusher byproduct from organic material

Do not add a permanent Moss Generator unless testing shows vanilla spreading is unsuitable for the modpack.

---

## Rooted Dirt

**Classification:** Vanilla Route or Mixing Candidate  
**Priority:** Low to Medium

### Recommended Project Skyblock Route

Provide a recipe using:

- Dirt
- Hanging Roots or Azalea-related material
- Green reagent
- Reusable Mixing Bowl

Avoid a dedicated generator.

---

## Azalea and Flowering Azalea

**Classification:** Vanilla Route starter content  
**Priority:** Low

### Recommended Project Skyblock Route

Support access through Moss progression and bone meal.

Do not create generators.

---

## Glow Lichen

**Classification:** Vanilla Route starter content  
**Priority:** Low

### Recommended Project Skyblock Route

Provide one starter acquisition recipe or loot path if required.

Vanilla bone-meal spreading should handle renewability afterward.

---

## Powder Snow

**Classification:** Vanilla Route or utility recipe  
**Priority:** Low

### Recommended Project Skyblock Route

Preserve cauldron-based vanilla collection where practical.

A direct recipe should only be considered if the target Skyblock environment makes snowfall impossible.

---

# Minecraft 1.19 Material Families

## Mud

**Classification:** Vanilla Route  
**Priority:** High documentation priority, low implementation priority

### Vanilla Route

Mud is created by applying a water bottle to Dirt.

### Recommended Project Skyblock Route

Do not create a Mud Generator.

Project Skyblock may add:

- Recipe Viewer documentation
- Advancement guidance
- Optional dispenser-friendly automation examples
- Machine integration that consumes Mud

---

## Packed Mud

**Classification:** Decorative and crafting derivative  
**Priority:** Low

Use vanilla crafting from Mud.

No generator required.

---

## Mud Bricks and Variants

**Classification:** Decorative Derivative  
**Priority:** Low

Use vanilla crafting and stonecutting.

No generator required.

---

## Mangrove Wood Family

**Classification:** Vanilla Route starter content  
**Priority:** Medium

### Recommended Project Skyblock Route

Provide access to the first Mangrove Propagule through progression, trading replacement, or a controlled organic recipe.

Once obtained, tree farming should remain the renewable system.

Do not create separate generators for logs, planks, roots, doors, slabs, or other wood variants.

---

## Mangrove Roots

**Classification:** Vanilla Route  
**Priority:** Low

Renewability should follow Mangrove tree farming.

A separate generator is unnecessary.

---

## Muddy Mangrove Roots

**Classification:** Crafting derivative  
**Priority:** Low

Use vanilla crafting or conversion from Mangrove Roots and Mud.

---

## Sculk

**Classification:** Advanced Progression or Vanilla Route  
**Priority:** Medium

### Recommended Project Skyblock Route

Avoid a passive Sculk Generator.

Possible approach:

- Provide a difficult route to the first Sculk Catalyst.
- Preserve experience-driven vanilla Sculk spreading.
- Add Crusher recipes that consume Sculk for thematic materials or experience-related byproducts.

### Balance Concern

Sculk has experience storage and farming implications. Any automated production must be tested carefully.

---

## Sculk Vein

**Classification:** Vanilla Route  
**Priority:** Low

Obtain through Sculk spreading and Silk Touch behavior.

No generator required.

---

## Sculk Catalyst

**Classification:** Progression Candidate  
**Priority:** Medium

### Recommended Project Skyblock Route

Possible later-game recipe requiring:

- Sculk
- Echo-related substitute or dark reagent
- Significant FE
- Soul Sand or soul-derived material
- Rare machine component

Do not make it an early passive output.

---

## Sculk Shrieker

**Classification:** Restricted or Unsupported  
**Priority:** Low

Natural and player-placed shriekers have important behavioral differences.

Do not add a normal crafting recipe until those mechanics are fully reviewed.

---

## Reinforced Deepslate

**Classification:** Restricted or Unsupported  
**Priority:** None

Do not make this block renewable in the initial design.

It is structure-bound, extremely durable, and not intended as a general building resource.

---

## Froglights

**Classification:** Vanilla Route with optional progression assistance  
**Priority:** Medium

### Recommended Project Skyblock Route

Preserve frog-and-magma-cube production as the primary route.

Potential assistance:

- A way to obtain frog spawn or tadpoles
- A way to access biome-temperature conversion
- Magma Cube spawning support
- Late-game machine recipe only if mob progression is intentionally disabled

Do not add three simple passive Froglight Generators.

---

# Minecraft 1.20 Material Families

## Cherry Wood Family

**Classification:** Vanilla Route starter content  
**Priority:** Medium

### Recommended Project Skyblock Route

Provide access to the first Cherry Sapling.

Tree growth should produce the full renewable wood route.

Do not add generators for individual Cherry wood blocks.

---

## Bamboo Wood Family

**Classification:** Vanilla Route  
**Priority:** Low

Bamboo is already renewable once obtained.

Project Skyblock only needs to ensure the player can obtain starter Bamboo.

Bamboo Planks, Mosaic, slabs, stairs, doors, trapdoors, rafts, and other derivatives should use vanilla recipes.

---

## Chiseled Bookshelf

**Classification:** Crafting Derivative  
**Priority:** None

No Project Skyblock generator or custom production route required.

---

## Hanging Signs

**Classification:** Crafting Derivative  
**Priority:** None

No custom production route required.

---

## Suspicious Sand

**Classification:** Restricted or Unsupported  
**Priority:** Low

### Recommendation

Do not create a normal recipe or generator.

Suspicious blocks contain archaeology loot data and are designed around exploration and brushing.

If archaeology progression must exist in Skyblock, design a dedicated archaeology system rather than crafting plain Suspicious Sand.

---

## Suspicious Gravel

**Classification:** Restricted or Unsupported  
**Priority:** Low

Use the same policy as Suspicious Sand.

---

## Decorated Pot

**Classification:** Crafting Derivative with restricted decorations  
**Priority:** Low

Plain brick-sided pots can remain craftable through vanilla behavior.

Pottery Sherds should remain tied to archaeology or a separately approved progression system.

---

## Calibrated Sculk Sensor

**Classification:** Advanced Crafting Derivative  
**Priority:** Low

Provide Quartz and Sculk access; then preserve the vanilla recipe.

No dedicated generator required.

---

## Pink Petals

**Classification:** Vanilla Route starter content  
**Priority:** Low

Provide starter access only if Cherry Grove content is otherwise unavailable.

Vanilla bone-meal behavior should handle renewability.

---

## Torchflower and Pitcher Plant

**Classification:** Exploration or mob-progression content  
**Priority:** Low

These should remain tied to Sniffer progression unless the modpack explicitly removes that route.

Do not add basic generators.

---

# Minecraft 1.21 Material Families

## Expanded Tuff Family

**Classification:** Decorative Derivative  
**Priority:** High documentation priority

The Tuff Generator should output base Tuff only.

All polished, brick, chiseled, slab, stair, and wall variants should use vanilla crafting and stonecutting.

---

## Expanded Copper Family

**Classification:** Decorative and functional derivatives  
**Priority:** Medium

Includes:

- Chiseled Copper
- Copper Grate
- Copper Bulb
- Copper Door
- Copper Trapdoor
- Exposed variants
- Weathered variants
- Oxidized variants
- Waxed variants

### Recommended Project Skyblock Route

Ensure Copper Ingots or Copper Blocks are renewable through an approved resource path.

Then preserve:

- Vanilla crafting
- Stonecutting
- Oxidation
- Scraping
- Waxing

Do not create generators for individual oxidation states or waxed variants.

---

## Crafter

**Classification:** Vanilla Crafting Derivative  
**Priority:** None

No custom generator required.

Project Skyblock machines should remain compatible with Crafter-based automation where applicable.

---

## Heavy Core

**Classification:** Restricted progression loot  
**Priority:** None

Do not make renewable in the initial implementation.

The Heavy Core is tied to Ominous Vault progression and the Mace.

---

## Trial Spawner and Ominous Trial Spawner

**Classification:** Restricted or Unsupported  
**Priority:** None

Do not provide recipes or generators.

---

## Vault and Ominous Vault

**Classification:** Restricted or Unsupported  
**Priority:** None

Do not provide recipes or generators.

---

# Initial Generator Roadmap

## Reconstruction Batch

These were already planned from legacy Project Skyblock content:

1. Clay Generator
2. Gravel Generator
3. Sand Generator
4. Red Sand Generator

### Required Reconsideration

Clay is renewable in modern vanilla through Mud and Pointed Dripstone.

Before rebuilding the Clay Generator, decide whether it should:

- Remain as a convenience generator
- Become a later upgrade
- Be replaced by a Mud or Dripstone support system
- Produce Clay faster than vanilla at an FE cost
- Be removed from the initial generator lineup

---

## Modern Stone Batch

1. Tuff Generator
2. Calcite production system
3. Cobbled Deepslate Generator
4. Dripstone starter route
5. Basalt and Smooth Basalt processing route

---

## Nether Batch

1. Netherrack Generator
2. Soul Sand Generator
3. Quartz production system
4. Basalt production review

---

## End and Advanced Batch

1. End Stone Generator
2. Obsidian Generator
3. Amethyst progression
4. Sculk progression
5. Froglight access review

---

# Implementation Order

## Phase 1 — Registry and Data Audit

Before adding blocks:

- Confirm current block, item, block entity, menu, recipe type, and recipe serializer registration patterns.
- Confirm generator configuration structure.
- Confirm loot table and blockstate generation strategy.
- Confirm whether data generation is already in use.
- Confirm naming and package standards.

## Phase 2 — Generator Framework

Build or extract shared systems for:

- Timed generation
- Internal item storage
- Automatic output
- Sided capabilities
- Configuration
- Serialization
- Synchronization
- Menu data
- Particle state
- Comparator support
- Redstone behavior
- Upgrade hooks if planned

## Phase 3 — First Modern Generator

Use the Tuff Generator as the first post-1.16 implementation.

Reasons:

- Simple item output
- No fluid handling
- No unusual block state
- Strong building value
- Large vanilla stonecutting family
- Good test of shared generator architecture

## Phase 4 — Calcite Decision Prototype

Implement recipe prototypes before deciding whether Calcite needs a dedicated generator.

## Phase 5 — Cobbled Deepslate Generator

Use this to verify that multiple generators can share the framework without copy-and-paste classes.

## Phase 6 — Starter-Resource Recipes

Add controlled starter access for:

- Pointed Dripstone
- Moss Block
- Mangrove Propagule
- Cherry Sapling
- Bamboo
- Other biome-bound renewable starters

## Phase 7 — Advanced Progression

Design and test:

- Amethyst
- Sculk
- Froglights
- Archaeology access, only if required

---

# Required Follow-Up Decisions

The following decisions must be made before implementation reaches their systems:

1. Does the modern version retain a Clay Generator despite vanilla clay renewability?
2. Is Calcite generated directly or processed through the Material Crusher?
3. Is Basalt generated, crafted, or produced through a world interaction?
4. How does a Skyblock player obtain the first Pointed Dripstone?
5. How does a player obtain biome-bound starter plants and saplings?
6. Should Amethyst use the Material Crusher or a future crystal-growing machine?
7. Is Sculk progression supported directly or left to modpack configuration?
8. Is archaeology intentionally available in a pure Skyblock world?
9. Which resources require FE, fuel, reagents, or machine tiers?
10. Should generators share a common block with configurable output, or remain distinct registered blocks?

---

# Testing Checklist

For every newly supported material:

- Confirm the item or block exists in Minecraft 1.21.1.
- Confirm the chosen route does not duplicate an adequate vanilla renewable method.
- Confirm recipes do not create progression loops or infinite-value exploits.
- Confirm outputs work with recipe viewers.
- Confirm stonecutting and crafting derivatives remain accessible.
- Confirm tags are used instead of hardcoded single-item checks where appropriate.
- Confirm automation works through standard NeoForge capabilities.
- Confirm configuration values synchronize correctly.
- Confirm server-only operation does not reference client classes.
- Confirm translations, models, blockstates, loot tables, recipes, and textures exist.
- Confirm README, CurseForge description, screenshots, release notes, and changelog are reviewed.

---

# Documentation Impact

When implementation begins, review and update:

```text
README.md
CHANGELOG.md
TODO.md
DECISIONS.md
branding/curseforge-description.md
branding/release-description-template.md
branding/screenshot-plan.md
docs/architecture/RESOURCE_GENERATORS.md
```

---

# Audit Status

**Status:** Initial classification complete  
**Next action:** Audit the current NeoForge registration and generator code, then implement the shared generator framework before adding the Tuff Generator.
