# Backend Milestone 19B2 — Material Crusher Engine Migration

## Purpose

Use the existing Material Crusher as a disposable vertical-slice stress fixture for the engine
contracts established through Milestones 19A and 19B1. The migration removes independent
block-entity ticking and duplicated resource ownership while preserving the prototype's established
observable behavior for regression testing.

Conversion does not promote the crusher to permanent Game Era content. It remains only while it
provides distinct evidence for inventory transactions, combustion, processing, capabilities,
persistence, synchronization, datapack reload, and scheduler sleep/wake behavior.

## Audit Finding

The legacy block entity directly owned an item handler, FE storage, progress counters, burn counters,
power-source state, NBT keys, recipe queries, fuel queries, sided wrappers, and one server ticker per
placed crusher. That implementation duplicated contracts already owned by the simulation runtime and
could not prove atomic completion or idle sleeping.

Milestone 19B2 replaces that split authority with one `MachineRuntime`. Minecraft and NeoForge remain
boundary adapters for recipes, furnace fuel, item conversion, capabilities, menus, rendering, and
legacy NBT decoding.

## Implemented Candidate

### Engine-owned crusher logic

- Added a Minecraft-independent `MaterialCrusherLogic` over engine-owned inventory, energy,
  combustion, and processing components.
- Added narrow recipe, fuel, and settings ports; no Minecraft type crosses into simulation code.
- Preserved FE-first or fuel-first policy, config-driven process duration, FE cost, furnace-fuel burn
  scaling, and lava-bucket container remainder behavior.
- Performs maximum-output admission before consuming power.
- Completes input consumption and primary/byproduct insertion in one optimistic inventory
  transaction.
- Blocks without consuming input when output space is unavailable.
- Preserves partial progress while power is unavailable and resumes after an inventory or energy
  wake.
- Stops recurring work while idle or stably blocked.

### Platform shell and adapters

- `MaterialCrusherBlockEntity` now extends `EngineMachineBlockEntity` and owns no independent item,
  energy, combustion, or processing state.
- Removed the block's per-instance server ticker.
- Replaced legacy crusher handlers with restricted `EngineItemHandlerAdapter` views for top input,
  side fuel, bottom output, unsided access, and player-menu access.
- Added a receive-only FE gate over the engine energy adapter so runtime config still controls
  external FE acceptance.
- Kept recipe matching, result rolling, furnace-fuel lookup, item-component conversion, menu sync,
  particles, blockstate animation, and capabilities at the NeoForge boundary.
- Added an engine-wide reload reevaluation signal after an authoritative datapack reload so sleeping
  machines can discover changed recipes without polling.

### Legacy save migration

- Detects the legacy crusher NBT keys only when no engine snapshot exists.
- Decodes all four legacy slots through the canonical Minecraft item boundary codec.
- Normalizes and clamps progress, burn values, FE, and thermal state through a tested
  Minecraft-independent migration helper.
- Converts legacy progress into the current typed processing snapshot using a temporary legacy
  process identity, then adopts the current recipe identity on first execution.
- Writes only the current schema-3 engine snapshot after migration.
- Preserves established menu and visual presentation long enough for the first authoritative engine
  execution to refresh it.

### Removed split-authority classes

The obsolete crusher-owned inventory, energy storage, sided-handler, and power-source classes are
removed from active source. Shared legacy base classes remain only where unrelated temporary
fixtures still use them.

## Preserved Prototype Behavior

- Existing crushing recipes, result chances, process duration, FE cost, fuel multiplier, and power
  preference.
- Top input, horizontal fuel input, bottom output, and unsided menu/manual behavior.
- Input and fuel shift-click routing, output extraction, GUI layout, progress/fuel/energy gauges,
  animation, particles, and sounds.
- FE receipt and furnace-fuel fallback, including lava-bucket-to-bucket remainder behavior.
- Existing placed-machine inventories, progress, fuel burn reservoir, and stored FE.

## Automated Coverage

- FE-powered completion and atomic input/output commit.
- Power loss preserving progress and waking after FE arrives.
- Output blockage consuming neither input nor power and settling without polling.
- Output extraction waking a blocked crusher.
- Fuel ignition, multiplier application, and container remainder insertion.
- Legacy process identity adoption without losing progress.
- Legacy snapshot normalization, clamping, slot preservation, and canonical idle state.
- Capability validation wrappers and dynamic FE receipt gating.

## Architectural Boundaries

- No Minecraft, NeoForge, Mojang, NBT, or SNBT type enters the simulation package.
- Recipe and fuel ports expose resolved simulation values, not host objects.
- Capability adapters own no authoritative resource state.
- The level scheduler, not the block, controls execution cadence.
- The migration does not freeze a public API.
- The Material Crusher remains a disposable stress fixture and may be retired after its evidence is
  no longer unique.

## Validation Available in the Delivery Sandbox

- All Minecraft-independent simulation sources compile under Java 21 with `-Xlint:all -Werror`.
- All 73 pure simulation tests pass, including the new crusher logic and legacy migration suites.
- Changed NeoForge boundary classes compile against the NeoForge/Minecraft API surface using strict
  dependency stubs for unavailable external libraries.
- The complete Gradle dependency graph and live client remain authoritative Windows validation gates.

## Gameplay, Save, API, and Dependency Impact

- **Gameplay:** intended to remain behaviorally equivalent; this is an ownership and scheduling
  migration, not a balance change.
- **Save:** legacy crusher keys migrate once into schema 3; current engine snapshots remain schema 3.
- **API:** internal prototype classes and platform adapters change; no stable public API is declared.
- **Dependencies:** none.
- **Other fixtures:** Thermal Generators, Creative Energy Cell, cables, generators, tools, armor, and
  unrelated prototype content are untouched.

## Runtime Validation Gate

Windows validation must prove clean tests/build, legacy world loading, both developer profiles,
FE-only and fuel-only processing, source preference, blocked outputs, sided automation, bucket
remainders, save/reload persistence, datapack reload reevaluation, and clean shutdown. The candidate
is not accepted until those gates pass.
