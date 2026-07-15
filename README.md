# Project Skyblock

Project Skyblock is a standalone skyblock-focused utility and progression mod by **Raziel23x**.

The mod is currently being rebuilt for **Minecraft 1.21.1** on **NeoForge** as a clean rewrite of the original 1.16.5 version. The legacy implementation remains available on the `MC-V1.16.X` branch.

Project Skyblock does not require Gardens of Glass, Curios, or any other content mod. Optional integrations are enabled only when their matching mods are installed.

## Current Development Status

The 1.21.1 rewrite is under active development. Core functionality is being restored first, with recipes and progression being reviewed afterward so they can be improved instead of copied blindly from the old version.

### Implemented

- Repair Gem
- Optional Curios support for the Repair Gem
- Flint tools
- Flint armor
- Wooden armor
- Flint shears
- Wooden shears
- Cobblestone Generator
- Water Generator
- Lava Generator
- Mixing Bowl
- Red, Green, and Blue Reagents
- Red, Green, and Blue Reagent Blocks
- Configurable gameplay and integration settings
- Item and fluid capability support
- Ambient and dropped-item particle effects

## Repair Gem

The Repair Gem gradually repairs damaged equipment while it is carried.

- Repairs one damaged carried item at a configurable interval
- Works from the normal player inventory, armor slots, and offhand
- Works from a Curios slot when Curios is installed and the integration is enabled
- Never scans the Ender Chest
- Emits a subtle magical particle effect when dropped
- Does not require a repair bench or other machine

## Resource Generators

The three current resource generators share a reusable generator system.

### Cobblestone Generator

- Stores up to 64 cobblestone by default
- Generates while internal storage has room
- Can be collected manually
- Automatically inserts into an inventory directly above
- Exposes an extraction-only item capability
- Works with compatible item pipes, chests, barrels, drawers, and modded storage blocks

### Water Generator

- Stores 8,000 mB by default
- Supports bucket extraction
- Exposes an extraction-only fluid capability
- Works with compatible fluid pipes and tanks

### Lava Generator

- Stores 8,000 mB by default
- Supports bucket extraction
- Exposes an extraction-only fluid capability
- Works with compatible fluid pipes and tanks

All capacities, generation rates, extraction behavior, particle effects, and related settings are configurable.

## Reagents and Mixing Bowl

Project Skyblock includes three reagent types:

- Red Reagent
- Green Reagent
- Blue Reagent

Each reagent also has a matching storage block.

The Mixing Bowl is a reusable crafting tool intended for reagent and special-material recipes. Recipes are being redesigned during the rewrite and may differ from the original 1.16.5 version.

Reagent items and blocks emit subtle particles matching their color.

## Equipment

### Wooden Equipment

- Wooden Helmet
- Wooden Chestplate
- Wooden Leggings
- Wooden Boots
- Wooden Shears

### Flint Tools

- Flint Sword
- Flint Pickaxe
- Flint Axe
- Flint Shovel
- Flint Hoe
- Flint Shears

### Flint Armor

- Flint Helmet
- Flint Chestplate
- Flint Leggings
- Flint Boots

## Legacy 1.16.5 Features

The original version also included additional vanilla-style crafting options intended to improve skyblock progression, including recipes involving:

- Bamboo and paper
- Clay blocks and clay balls
- Crying obsidian
- Flint from gravel
- Slimeballs
- Glowstone dust
- Quartz blocks
- Nether wart blocks
- Wool and string
- Saddles and horse armor
- Beetroot and wheat seeds
- Campfire torches
- Rotten flesh and leather

These recipes are not automatically considered part of the 1.21.1 rewrite. They will be reviewed, modernized, or replaced as progression is rebuilt.

## Configuration

After the first launch, Project Skyblock creates:

```text
config/projectskyblock-common.toml
config/projectskyblock-generators.toml
config/projectskyblock-integrations.toml
```

These files control features such as:

- Repair Gem behavior
- Generator capacity and speed
- Manual and automated extraction
- Pipe capability access
- Ambient particle effects
- Optional Curios integration
- Debug logging

## Optional Integrations

Project Skyblock remains fully functional as a standalone mod.

Optional compatibility is isolated and enabled only when the relevant mod is installed. Curios support is currently implemented for the Repair Gem. Additional integrations may be added later.

## Planned Features

- Additional resource-processing machines
- Cobblestone Crusher
- Reworked recipes and progression
- More data-driven generator definitions
- Void world generation
- Optional information overlays such as Jade or The One Probe
- Additional compatibility modules

## Development

Project Skyblock requires **Java 21**.

### Windows

```cmd
gradlew.bat build
gradlew.bat runClient
```

### Linux and macOS

```bash
./gradlew build
./gradlew runClient
```

To clone the repository:

```bash
git clone https://github.com/raziel23x/Project-Skyblock.git
cd Project-Skyblock
```

Use the `MC-1.21.1-NeoForge` branch for the current rewrite and `MC-V1.16.X` for the legacy version.

## Bug Reports

When reporting a bug, include:

- Minecraft version
- NeoForge version
- Project Skyblock version
- Installed optional integrations
- Relevant client or server log
- Crash report, when applicable
- Clear reproduction steps

Issues can be reported through the GitHub repository.

## Modpack Use

Project Skyblock may be included in modpacks. Credit and a link back to the project are appreciated.

## License

Project Skyblock is licensed under the **GNU General Public License v3.0**.
