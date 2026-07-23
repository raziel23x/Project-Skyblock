# Project Skyblock

> **One Tree. Infinite Possibilities.**
>
> From survival to civilization.

Project Skyblock is a **NeoForge 1.21.1 mod** designed for use inside modpacks. Every world begins with a living tree in the void; from that starting point, every mandatory progression path must remain solvable. It turns limited-resource survival into a long-form civilization-building experience driven by renewable production, research, engineering, biology, automation, and interconnected systems.

## Core Features

- **Civilization progression** instead of a simple linear tech tree
- **Renewable resource systems** designed for long-term worlds
- **Knowledge-based advancement** through discoveries and research
- **Interconnected gameplay systems** including biology, agriculture, chemistry, metallurgy, energy, logistics, and machines
- **Data-driven content** for easier balancing and customization
- **Datapack support**
- **Planned first-class KubeJS integration through the optional public API**
- **Optional compatibility layers** for other mods
- **Modpack-friendly design** with configurable progression and extension points

## Design Philosophy

Project Skyblock is built around a few core rules:

1. Every important resource should have a meaningful acquisition path.
2. Progression should unlock capabilities, not only stronger numbers.
3. Systems should support one another instead of existing in isolation.
4. Automation should feel earned and understandable.
5. Modpack authors should be able to customize progression without rewriting the mod.
6. Documentation should exist before implementation becomes difficult to change.

## Project Status

Project Skyblock is currently in active **Engine Era** development. Its Minecraft-independent
simulation engine is a greenfield system being built from first principles around bounded work,
sleeping systems, deterministic state, and scale-aware representation.

Existing machines, blocks, tools, armor, menus, and utility items are prototype stress fixtures.
They are converted to the engine to expose missing contracts and performance limits; they are not
automatically permanent Game Era content.

## Documentation

Project documentation is available in the [`docs`](docs/) directory.

Recommended starting points:

- [Mod Vision](docs/01-vision/MOD_VISION.md)
- [Starting Conditions](docs/01-vision/STARTING_CONDITIONS.md)
- [Project Constitution](docs/01-vision/PROJECT_CONSTITUTION.md)
- [Gameplay Pillars](docs/02-game-design/GAMEPLAY_PILLARS.md)
- [System Architecture](docs/03-engineering/SYSTEM_ARCHITECTURE.md)
- [Civilization Blueprint](docs/01-vision/CIVILIZATION_BLUEPRINT.md)
- [Knowledge Web](docs/02-game-design/progression/KNOWLEDGE_WEB.md)
- [Engineering Documentation](docs/03-engineering/README.md)
- [Modpack Author Guide](docs/04-integration/MODPACK_AUTHOR_GUIDE.md)

## Development

### Requirements

- Java 21
- Minecraft 1.21.1
- NeoForge
- Gradle

### Build

On Windows:

```powershell
./gradlew.bat build
```

On Linux or macOS:

```bash
./gradlew build
```

Build artifacts are generated in:

```text
build/libs/
```

### Run the Development Client

On Windows:

```powershell
./gradlew.bat runClient
```

On Linux or macOS:

```bash
./gradlew runClient
```

## Contributing

Contributions, testing, documentation improvements, and compatibility work are welcome.

Please read [`CONTRIBUTING.md`](CONTRIBUTING.md) before submitting changes.

## License

See [`LICENSE`](LICENSE) for licensing information.

---

**Project Skyblock** — Build a civilization from almost nothing.
