# Project Skyblock

Project Skyblock is being rebuilt for Minecraft 1.21.1 on NeoForge.

This branch is a clean modern rewrite of the original 1.16 mod. The legacy implementation remains available on the `MC-V1.16.X` branch.

## Current milestone

- NeoForge 1.21.1 project foundation
- Organized item and creative-tab registries
- Repair Gem registered with its original texture
- Localized item name and tooltip

The Repair Gem's active repair behavior will be implemented after the foundational registry and data structure is proven in-game.

## Development

```bash
./gradlew build
./gradlew runClient
```

Windows:

```cmd
gradlew.bat build
gradlew.bat runClient
```

## License

GNU General Public License v3.0.


## Repair Gem behavior

- Repairs one damaged carried item by one durability point each second.
- Works while carried in the normal inventory, armor, or offhand slots.
- Supports an equipped Curios slot when Curios is installed.
- Never scans or repairs from the Ender Chest.
- Curios remains completely optional.

## Equipment milestone

This build restores the original flint tool set, flint armor, wooden armor, flint shears, and wooden shears. The equipment remains repairable by its original material and is included in the Project Skyblock creative tab.

## Generator foundation

- Cobblestone Generator: right-click with an empty hand for cobblestone.
- Water Generator: use an empty bucket to receive a water bucket.
- Lava Generator: use an empty bucket to receive a lava bucket.

These three blocks share one reusable generator block implementation. Automated item/fluid capabilities are planned as the next generator milestone.

## Generator buffers and automation

- Cobblestone Generator stores up to 64 cobblestone internally.
- Water Generator stores up to 8 buckets (8,000 mB).
- Lava Generator stores up to 8 buckets (8,000 mB).
- All generators produce once per second while space remains.
- The cobblestone generator automatically inserts upward into any block exposing NeoForge's item-handler capability.
- Item pipes can extract cobblestone from every side.
- Fluid pipes can extract water or lava from every side.
- Generator capabilities are extraction-only; external systems cannot insert into the buffers.
- No storage, item-pipe, or fluid-pipe mod is required or hardcoded.
