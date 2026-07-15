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
