# Engine Roadmap

## Era Sequence

```text
Research Era
    ↓
Engine Era
    ↓
Engine Baseline
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
- Machine composition proof and shared dirty-state ownership
- Machine processing component
- Machine runtime persistence contract and NeoForge block-entity bridge
- Level-scoped scheduler driver
- First engine-owned machine and NeoForge energy capability adapter

### Near-Term Planned

- Inventory capability adapter proven by a sided processing machine
- First engine-owned processing-machine migration

### Later Planned When Proven Necessary

- Machine fluid component
- Machine gas component
- network fairness and contention
- fluid and gas network behavior
- chemistry-supporting resource properties

## Engine Baseline

At the end of the Engine Era, capture the implemented simulation, adapters, contracts, tests, known limitations, and expected future needs. This is a comparison point, not an engine freeze. See [Engine Baseline and Validation](ENGINE_BASELINE_AND_VALIDATION.md).

## Archive

Research Era implementations are archived after the backend replacement is proven. They are not allowed to remain as accidental production architecture.

## Journal Era

The Journal becomes the first production feature and the central progression interface.

## Game Era

Production content implements knowledge-driven progression, material decomposition, ecology, processing, logistics, energy, chemistry, and civilization systems through the validated engine.

## Potential Engine Extraction

After the Engine Era baseline is captured and the simulation APIs have proven stable through real gameplay and additional real projects, evaluate extracting the Minecraft-independent backend into a standalone NeoForge library mod shared by Project Skyblock and future mods. This is a future direction evaluated through evidence, not a current publishing milestone.

Extraction should be considered only when:

- multiple mods genuinely need the engine;
- core packages remain free of Project Skyblock-specific dependencies;
- component and simulation APIs have stabilized;
- independent automated tests exist;
- long-term versioned public API maintenance is justified.

Until then, backend packages and platform adapters remain cleanly separated without introducing premature publishing, compatibility, or public-API obligations. Valid engine capabilities are not removed merely because Project Skyblock has not consumed them yet; removal requires an affirmative architectural justification. See ADR-0010 and the engine baseline policy.
