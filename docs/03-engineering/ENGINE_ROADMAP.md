# Engine Roadmap

## Era Sequence

```text
Research Era
    ↓
Engine Era
    ↓
Archive
    ↓
Journal
    ↓
Game Era
```

## Research Era

Existing blocks, machines, items, menus, recipes, and compatibility code are prototypes and evidence. They remain available while the production engine is built.

## Engine Era

### Completed

- Simulation foundation
- Scheduler
- Thermal core
- Energy simulation
- Energy network topology
- Energy network transfer
- Network sleeping
- Machine framework
- Machine energy component
- Machine inventory component
- Machine thermal component
- Scheduler wake coalescing and active-execution safety

### Near-Term Planned

- machine composition proof
- persistence contracts and codecs
- Minecraft/NeoForge adapters
- reference vertical slice

### Later Planned When Proven Necessary

- Machine fluid component
- Machine gas component
- Machine processing component
- network fairness and contention
- fluid and gas network behavior
- chemistry-supporting resource properties

## Archive

Research Era implementations are archived after the backend replacement is proven. They are not allowed to remain as accidental production architecture.

## Journal Era

The Journal becomes the first production feature and the central progression interface.

## Game Era

Production content implements knowledge-driven progression, material decomposition, ecology, processing, logistics, energy, chemistry, and civilization systems through the validated engine.

## Potential Engine Extraction

After the Engine Era is complete and the simulation APIs have proven stable through real gameplay, evaluate extracting the Minecraft-independent backend into a standalone NeoForge library mod. This is a future consideration, not a current milestone or commitment.

Extraction should be considered only when:

- multiple mods genuinely need the engine;
- core packages remain free of Project Skyblock-specific dependencies;
- component and simulation APIs have stabilized;
- independent automated tests exist;
- long-term versioned public API maintenance is justified.

Until then, backend packages and platform adapters should remain cleanly separated without introducing premature publishing, compatibility, or public-API obligations.
