<p align="center">
  <img src="docs/images/project-skyblock-logo.png" alt="Project Skyblock — Progress. Automate. Survive." width="700">
</p>

<h1 align="center">Project Skyblock</h1>

<p align="center">
  A standalone Skyblock progression and automation mod for Minecraft 1.21.1 on NeoForge.
</p>

<p align="center">
  <a href="https://www.curseforge.com/minecraft/mc-mods/project-skyblock"><img alt="CurseForge" src="https://img.shields.io/badge/Download-CurseForge-F16436?logo=curseforge&logoColor=white"></a>
  <a href="https://github.com/raziel23x/Project-Skyblock"><img alt="GitHub" src="https://img.shields.io/badge/Source-GitHub-181717?logo=github"></a>
  <a href="https://github.com/raziel23x/Project-Skyblock/issues"><img alt="Issues" src="https://img.shields.io/github/issues/raziel23x/Project-Skyblock"></a>
  <img alt="Minecraft 1.21.1" src="https://img.shields.io/badge/Minecraft-1.21.1-62B47A">
  <img alt="NeoForge" src="https://img.shields.io/badge/Loader-NeoForge-EA6B2D">
  <img alt="Java 21" src="https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white">
  <img alt="GPL-3.0" src="https://img.shields.io/badge/License-GPL--3.0-blue">
</p>

## Progress. Automate. Survive.

Project Skyblock expands the gameplay **after** a Skyblock world has been created. It is not a world generator; dedicated mods already handle that job well.

The mod focuses on resource generation, progression, automation, equipment, reusable crafting tools, configurable mechanics, and optional integrations. Project Skyblock works as a standalone mod and enables extra compatibility only when supported mods are installed.

## Downloads

- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/project-skyblock)
- [GitHub releases and source](https://github.com/raziel23x/Project-Skyblock)
- [Issue tracker](https://github.com/raziel23x/Project-Skyblock/issues)

## Current Features

| System | Status | Details |
|---|:---:|---|
| Repair Gem | ✅ | Passively repairs carried equipment |
| Curios integration | ✅ | Optional Repair Gem slot support |
| Resource Generators | ✅ | Cobblestone, water, and lava |
| Generator automation | ✅ | Item/fluid capabilities and upward item output |
| Flint equipment | ✅ | Tools, shears, and armor |
| Wooden equipment | ✅ | Armor and shears |
| Reagent items | ✅ | Red, green, and blue |
| Reagent blocks | ✅ | Storage blocks with matching particles |
| Mixing Bowl | 🟡 | Reusable crafting foundation |
| Cobblestone Crusher | 📋 | Planned |
| Recipe progression | 🚧 | Being redesigned |

## Repair Gem

The Repair Gem gradually repairs damaged equipment while carried.

- Works from the normal inventory, armor slots, and offhand
- Supports a Curios slot when Curios is installed
- Never scans the Ender Chest
- Requires no repair bench or machine
- Emits a subtle magical aura when dropped
- Repair interval and amount are configurable

## Resource Generators

### Cobblestone Generator

- Stores up to 64 cobblestone by default
- Supports manual collection
- Automatically inserts into a compatible inventory above
- Exposes an extraction-only item capability
- Works with compatible pipes, storage blocks, drawers, and backpacks

### Water Generator

- Stores 8,000 mB by default
- Supports bucket extraction
- Exposes an extraction-only fluid capability
- Works with compatible pipes and tanks

### Lava Generator

- Stores 8,000 mB by default
- Supports bucket extraction
- Exposes an extraction-only fluid capability
- Works with compatible pipes and tanks

All generators include placed-block and dropped-item particle effects. Capacity, speed, extraction, automation, and particles are configurable.

## Reagents and Mixing Bowl

Project Skyblock currently includes:

- Red Reagent and Red Reagent Block
- Green Reagent and Green Reagent Block
- Blue Reagent and Blue Reagent Block
- Reusable Mixing Bowl

Each reagent item and block emits subtle color-matched particles. Recipes are being redesigned rather than copied blindly from the legacy version.

## Equipment

### Flint

- Sword
- Pickaxe
- Axe
- Shovel
- Hoe
- Shears
- Helmet
- Chestplate
- Leggings
- Boots

### Wooden

- Shears
- Helmet
- Chestplate
- Leggings
- Boots

## Design Philosophy

Project Skyblock follows four rules:

1. **Standalone first** — no content mod is required.
2. **Optional integrations** — compatibility activates only when matching mods are installed.
3. **Configuration over hardcoding** — pack authors can tune behavior without rebuilding the mod.
4. **Automation friendly** — capabilities use standard NeoForge systems instead of hardcoded mod checks.

When another mod already solves a problem well, Project Skyblock integrates with it instead of duplicating it.

## Configuration

The mod generates the following configuration files after first launch:

```text
config/projectskyblock-common.toml
config/projectskyblock-generators.toml
config/projectskyblock-integrations.toml
```

These control Repair Gem behavior, generator storage and speed, extraction rules, particle effects, optional integrations, and debugging.

## Optional Integrations

Currently supported:

- Curios

Potential future integrations:

- Jade
- The One Probe
- EMI / JEI
- Additional storage and automation systems

## Roadmap

- [x] NeoForge 1.21.1 foundation
- [x] Repair Gem
- [x] Flint and wooden equipment
- [x] Resource generators
- [x] Item and fluid automation
- [x] Configuration system
- [x] Reagent items and blocks
- [x] Mixing Bowl foundation
- [ ] Cobblestone Crusher
- [ ] Expanded processing machines
- [ ] Reworked recipes and progression
- [ ] Additional optional integrations
- [ ] More visual and audio polish

## Development

Project Skyblock requires **Java 21**.

### Windows

```cmd
gradlew.bat runClient
gradlew.bat build
```

### Linux and macOS

```bash
./gradlew runClient
./gradlew build
```

Clone the repository:

```bash
git clone https://github.com/raziel23x/Project-Skyblock.git
cd Project-Skyblock
git switch MC-1.21.1-NeoForge
```

The legacy implementation remains available on the `MC-V1.16.X` branch.

## Bug Reports

Please include:

- Minecraft version
- NeoForge version
- Project Skyblock version
- Installed optional integrations
- Reproduction steps
- Relevant client or server log
- Crash report, when applicable

Report problems through the [GitHub issue tracker](https://github.com/raziel23x/Project-Skyblock/issues).

## Modpack Use

Project Skyblock may be included in public and private modpacks. Credit and a link to the CurseForge or GitHub project page are appreciated.

## Credits

Created and maintained by **Raziel23x**.

Thanks to community testers, contributors, and players reporting bugs during the 1.21.1 rebuild.

## License

Project Skyblock is licensed under the [GNU General Public License v3.0](LICENSE).
