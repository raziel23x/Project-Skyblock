# Project Skyblock

**Progress. Automate. Survive.**

Project Skyblock is a standalone Skyblock progression and automation mod for **Minecraft 1.21.1** using **NeoForge**.

The mod expands gameplay after a Skyblock world has already been created. It does not generate Skyblock worlds. Instead, it provides renewable resource generation, automation, equipment, reusable crafting tools, configurable machines, power generation, and optional compatibility with supported mods.

Project Skyblock is designed to work on its own. Optional integrations activate only when their matching mods are installed.

---

## Current Features

### Resource Generators

Project Skyblock currently includes:

- Cobblestone Generator
- Water Generator
- Lava Generator

These generators provide renewable materials without adding ore generation.

The Cobblestone Generator:

- Stores generated cobblestone
- Supports manual collection
- Automatically outputs upward into compatible inventories
- Exposes an extraction-only item capability
- Works with compatible pipes, drawers, storage blocks, and backpacks

The Water and Lava Generators:

- Store generated fluid internally
- Support bucket extraction
- Expose extraction-only fluid capabilities
- Work with compatible pipes and tanks

Generator capacity, speed, extraction behavior, automation, and particle effects are configurable.

---

## Machines and Power

### Material Crusher

The Material Crusher processes datapack-driven crushing recipes.

Features include:

- FE-powered processing
- Furnace fuel as a fallback when FE is unavailable
- Strict sided item automation
- Configurable processing behavior
- Animated GUI
- FE and fuel displays
- Active exterior gears
- Glowing processing chamber
- Dust, smoke, ash, and vent effects
- Optional Ex Deorum dust support when Ex Deorum is installed

### Thermal Generator Mk I

The Thermal Generator Mk I converts fuel or lava into Forge Energy.

Features include:

- Standard furnace fuel support
- Lava input through NeoForge fluid capabilities
- Internal fluid and FE storage
- Automatic FE output to adjacent receivers
- Persistent fuel conversion, tank contents, and stored energy
- Rotating front turbine
- Animated rear cooling fan
- Glowing firebox
- Exhaust smoke and heat effects

### Basic Energy Cable

The Basic Energy Cable provides standalone FE transport.

Features include:

- Automatic six-sided connections
- Shared connected-cable networks
- FE extraction from compatible sources
- FE distribution to compatible receivers
- Configurable network transfer limits
- Compact, seamless conduit visuals

The cable transports energy only. Item and fluid transport remain compatible through vanilla mechanics and standard NeoForge capabilities.

---

## Reagents and Reusable Crafting

Project Skyblock includes:

- Red Reagent
- Green Reagent
- Blue Reagent
- Red Reagent Block
- Green Reagent Block
- Blue Reagent Block
- Reusable Mixing Bowl

Reagent items and blocks include subtle color-matched particle effects.

Recipes are being redesigned for the NeoForge version rather than copied directly from the legacy implementation.

---

## Equipment

### Flint Equipment

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

### Wooden Equipment

- Shears
- Helmet
- Chestplate
- Leggings
- Boots

---

## Repair Gem

The Repair Gem gradually repairs damaged equipment while carried.

Features include:

- Works from the normal inventory
- Works from armor slots
- Works from the offhand
- Supports an optional Curios slot
- Never scans the Ender Chest
- Requires no repair station
- Emits a subtle magical aura when dropped
- Configurable repair interval and repair amount

---

## Configuration

Project Skyblock creates configuration files after its first launch:

```text
config/projectskyblock-common.toml
config/projectskyblock-generators.toml
config/projectskyblock-integrations.toml
config/projectskyblock-machines.toml
```

Configuration options control:

- Repair Gem behavior
- Generator capacity and speed
- Extraction rules
- Automation
- Particle effects
- Machine behavior
- Optional integrations
- Debugging options

---

## Optional Integrations

Project Skyblock has no required content-mod dependencies.

Current optional integrations include:

- Curios support for the Repair Gem
- Ex Deorum dust output support for the Material Crusher

Additional integrations may be added when they improve compatibility without making another mod mandatory.

---

## Design Goals

Project Skyblock follows four core design rules:

1. **Standalone first**  
   The mod must remain useful without requiring another content mod.

2. **Optional integrations**  
   Compatibility activates only when the matching mod is installed.

3. **Configuration over hardcoding**  
   Modpack authors should be able to tune behavior without rebuilding the mod.

4. **Automation friendly**  
   Machines and generators use standard NeoForge capabilities instead of hardcoded mod checks.

When another mod already solves a problem well, Project Skyblock integrates with it rather than duplicating the entire system.

---

## Project Direction

- Resource Generators produce renewable building and utility materials, not ores.
- Cobblestone, Water, and Lava are the basic generators.
- Later generators are intended as progression rewards.
- Project Skyblock provides FE generation, machine power, and energy transport.
- Item and fluid transport use vanilla mechanics and standard capabilities.
- Project Skyblock does not add world generation.

---

## Modpack Permissions

Project Skyblock may be included in public and private modpacks.

Credit and a link to the CurseForge or GitHub project page are appreciated.

---

## Bug Reports

Please include the following when reporting a problem:

- Minecraft version
- NeoForge version
- Project Skyblock version
- Installed optional integrations
- Reproduction steps
- Relevant client or server log
- Crash report, when applicable

Use the GitHub issue tracker for bug reports and development feedback.

---

## Support Development

Project Skyblock is free and open source.

Development support is always optional, but it helps cover testing, hosting, artwork, and development time.

PayPal support link:

```text
https://www.paypal.com/donate/?business=raziel23x%40gmail.com&no_recurring=0&currency_code=USD
```

---

## Credits

Created and maintained by **Raziel23x**.

Thanks to community testers, contributors, and players who report problems during the Minecraft 1.21.1 NeoForge rebuild.

---

## License

Project Skyblock is licensed under the GNU General Public License v3.0.
