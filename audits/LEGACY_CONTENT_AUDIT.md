# Legacy Content Audit

> **Historical record — not current project authority.**
>
> This file preserves a legacy-content migration snapshot. Use `legacy-reference/` and the current
> engineering roadmap for present decisions; do not treat the statuses below as the active work queue.

## Generators found in legacy resources

Already rebuilt:

- Cobblestone
- Water
- Lava

Approved for staged reconstruction:

- Clay
- Gravel
- Sand
- Red Sand
- Netherrack
- Soul Sand
- End Stone
- Obsidian
- Quartz

Decision required:

- Dirt
- Grass Block

## Legacy resource categories requiring staged review

- Java block and tile-entity implementations
- Block-item wrappers
- Recipes and alternate recipes
- Recipe advancements
- Loot tables
- Forge-era tags
- Blockstates
- Block models and item models
- Textures
- Language files
- Configuration behavior
- Generated-resource conventions

## Duplication warning

The old branch implemented most generators as separate block, tile, and block-item classes. That design must not be recreated. The modern shared resource-generator implementation should receive per-generator definitions through registration/configuration rather than one class hierarchy per material.

## Crusher warning

The old `CobblestoneCrusher` classes are legacy references only. The modern `MaterialCrusher` is broader and data-driven. Restore only missing gameplay behavior, not the old class structure.
