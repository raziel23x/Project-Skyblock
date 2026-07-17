# Project Skyblock TODO

This file tracks planned work and future ideas. Completed user-facing features belong in `README.md`; completed changes belong in `CHANGELOG.md`.

## Current Priority

- [x] Compile and in-game test the Basic Energy Cable network
- [x] Test cable chains between Thermal Generator Mk I and Material Crusher
- [ ] Test the Material Crusher rear FE connector in-game
- [ ] Test branched cable networks with multiple receivers
- [ ] Confirm compatibility with third-party standard FE storage and machines
- [ ] Complete shared Machine Framework v1
- [ ] Complete shared machine synchronization helpers
- [x] Establish shared machine GUI status, gauge, and inventory layout

## Resource Generation Vision: Alchemy to Chemistry

Replace conventional sieve-and-ore skyblock progression with resource manufacturing based on alchemy, chemistry, separation, reduction, crystallization, and industrial processing.

Players should begin with primitive reagents and observations, then advance into powered chemical processing and synthetic material production.

### Stage 1 — Primitive Alchemy

Machines and tools:

- [ ] Mortar & Pestle
- [ ] Primitive Kiln
- [ ] Distillation Pot
- [ ] Evaporation Basin

Processes and materials:

- [ ] Plant extraction
- [ ] Salt production
- [ ] Charcoal production
- [ ] Sulfur collection
- [ ] Clay processing
- [ ] Lime and quicklime production
- [ ] Vinegar production
- [ ] Ash production
- [ ] Basic reagent preparation

### Stage 2 — Proto Chemistry

Machines:

- [ ] Washer
- [ ] Filter
- [ ] Basic Centrifuge
- [ ] Drying Tray or Drying Oven

Processes:

- [ ] Mineral separation
- [ ] Ore-bearing dust washing
- [ ] Dust purification
- [ ] Precipitation
- [ ] Filtration
- [ ] Basic crystallization

Example chain:

`Stone → Material Crusher → Mineral Dust → Washing → Iron-bearing Sand → Chemical Processing → Iron`

### Stage 3 — Industrial Chemistry

Machines:

- [ ] Electrolyzer
- [ ] Chemical Reactor
- [ ] Mixer
- [ ] Pressure Vessel
- [ ] Distillation Column
- [ ] Advanced Furnace
- [ ] Crystallizer
- [ ] Press

Processes:

- [ ] Water electrolysis
- [ ] Acid production
- [ ] Base production
- [ ] Metal reduction
- [ ] Chemical synthesis
- [ ] Gas processing
- [ ] Solvent extraction
- [ ] Electrorefining

### Stage 4 — Advanced Materials

- [ ] Steel
- [ ] Bronze
- [ ] Brass
- [ ] Electrum
- [ ] Invar
- [ ] Constantan
- [ ] Stainless Steel
- [ ] Graphite
- [ ] Silicon
- [ ] Lithium
- [ ] Nickel
- [ ] Chromium
- [ ] Titanium

Chemistry-based alloy examples:

- Copper + Zinc → Brass
- Iron + Carbon → Steel
- Iron + Chromium + Nickel → Stainless Steel

### Stage 5 — Synthetic Resource Production

Replace direct ore generation with industrial manufacturing chains.

- [ ] Hydrogen and oxygen from water
- [ ] Sulfuric acid production
- [ ] Metal leaching
- [ ] Copper solution electrorefining
- [ ] Iron oxide reduction
- [ ] Synthetic quartz or silicon processing
- [ ] Controlled crystal growth
- [ ] Renewable reagent loops
- [ ] Waste recovery and recycling

Example chains:

`Water → Electrolysis → Hydrogen + Oxygen`

`Copper-bearing solution → Electrorefining → Pure Copper`

`Iron Oxide + Reducing Agent → Iron`

### Periodic Element Progression

Unlock useful elements and compounds as progression milestones rather than exposing every recipe immediately.

- [ ] Hydrogen
- [ ] Oxygen
- [ ] Carbon
- [ ] Sodium
- [ ] Chlorine
- [ ] Sulfur
- [ ] Iron
- [ ] Copper
- [ ] Zinc
- [ ] Nickel
- [ ] Chromium
- [ ] Titanium
- [ ] Silicon
- [ ] Lithium

### Laboratory Journal and Research

- [ ] Laboratory Journal item
- [ ] Experiment logging
- [ ] Reaction discovery
- [ ] Process notes
- [ ] Research progression
- [ ] Recipe unlocks through successful experiments
- [ ] Journal chapters for Alchemy, Separation, Chemistry, Metallurgy, and Industrial Processing
- [ ] Record failed or incomplete experiments without consuming permanent unlocks
- [ ] Integrate discovered recipes with JEI or EMI visibility

## Machines

Current:

- [x] Thermal Generator Mk I
- [x] Material Crusher
- [x] Basic Energy Cable
- [x] Structural Energy Frame

Planned:

- [ ] Automated Mixing Bowl
- [ ] Reagent Infuser
- [ ] Battery Block
- [ ] Capacitor Bank
- [ ] Alloy Furnace
- [ ] Centrifuge
- [ ] Chemical Reactor
- [ ] Electrolyzer
- [ ] Mixer
- [ ] Distillation Column
- [ ] Crystallizer
- [ ] Press
- [ ] Pressure Vessel
- [ ] Thermal Generator Mk II
- [ ] Thermal Generator Mk III
- [ ] Additional reagent-powered machines

## Renewable Utility Resource Generators

Only renewable building and utility resources belong in this system. Generator recipes should represent progression rewards rather than starting shortcuts.

### Early progression

- [ ] Clay Generator
- [ ] Gravel Generator
- [ ] Sand Generator
- [ ] Red Sand Generator

### Nether progression

- [ ] Netherrack Generator
- [ ] Soul Sand Generator
- [ ] Blackstone Generator
- [ ] Basalt Generator

### Later progression

- [ ] End Stone Generator
- [ ] Obsidian Generator
- [ ] Quartz Generator

## Energy

- [x] Thermal Generator Mk I
- [x] Internal generator FE buffer
- [x] Basic Energy Cable implementation
- [ ] Visible directional cable pulse
- [ ] Dim idle cables and brighten active cables
- [ ] Scale pulse speed with FE transfer rate
- [ ] Advanced Energy Cable
- [ ] Ultimate Energy Cable
- [ ] Decide whether standalone players need a craftable Basic Energy Cell

## Compatibility

### Completed

- [x] Curios Repair Gem support
- [x] Ex Deorum Crusher dust support

### Planned optional integrations

- [ ] EMI / JEI recipe display support
- [ ] Create
- [ ] Mekanism
- [ ] Thermal Series
- [ ] Immersive Engineering
- [ ] PneumaticCraft
- [ ] Applied Energistics 2
- [ ] Refined Storage
- [ ] Pipez

## Polish and Documentation

- [ ] Standardize visible FE connectors on every powered machine
- [ ] Shared machine sounds
- [ ] Shared machine animation helpers
- [x] Thermal Generator Mk I exterior active animation
- [x] Thermal Generator Mk I full-panel exhaust polish
- [ ] Datapack recipe documentation
- [ ] Refresh the CurseForge description after the next stable release

## Future Ideas

- [ ] Machine upgrade cards or augments
- [ ] Additional thermal fuel integrations
- [ ] Advanced energy storage, only if it fills a standalone gameplay need
