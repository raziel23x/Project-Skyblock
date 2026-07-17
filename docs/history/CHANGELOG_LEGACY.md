## Unreleased

### Added
- Added the Structural Energy Frame, a build-focused cable variant with identical FE behavior to the Basic Energy Cable.
- Added a crafting recipe and creative-tab entry for the Structural Energy Frame.

### Changed
- Refined Thermal Generator and Material Crusher inventory/status spacing for final in-game testing.
- Preserved the Basic Energy Cable chamfer, pulse, connector collar, and machine compatibility.

- Fixed the Material Crusher active front grille so its warm glow appears visibly between the bars instead of remaining black.
### GUI alignment polish
- Raised both machine player inventories to the same shared baseline.
- Repositioned the Thermal Generator status block to preserve clear spacing between gauge labels and inventory.
- Nudged the Material Crusher status indicator into alignment with the lower control area.

- Fixed the Material Crusher active front so all grille bars remain solid while a soft glow appears only behind them.
# Project Skyblock Changelog

## Unreleased


- Polished Basic Energy Cable visuals with a restrained animated energy pulse and recessed machine-side socket collars.
- Added a dedicated brighter cyan socket trim to the Thermal Generator Mk I top face.
- Varied Thermal Generator exhaust particle type, velocity, and rise for less repetitive smoke.
### Basic Energy Cable final visual polish
- Added subtle darker conduit edges, a restrained center highlight, and a darker underside while preserving seamless straight runs.
- Added graphite machine-only connector collars so cables visibly plug into FE-capable blocks without reintroducing seams between adjacent cable blocks.
- Left FE networking and transfer behavior unchanged.

### Final Machine System v1.0 polish
- Centered the Thermal Generator status block beneath its gauges and separated it from gauge labels.
- Shifted the Material Crusher status indicator for cleaner alignment while freezing the completed Crusher layout.
- Softened the Basic Energy Cable highlight and darkened its outer jacket while preserving seamless straight runs.
- Established the current machine GUI and cable visuals as the v1.0 baseline.


- Reworked the Thermal Generator Mk I status display into a compact two-line readout below the gauges.
- Replaced the oversized status square with a small activity LED and separated generation state from FE output rate for readability.
- Redesigned the active Material Crusher front grille with solid bars, removed the disconnected orange indicator square, and added a unified soft chamber glow.

### Material Crusher identity and exterior activity

- Renamed **Cobblestone Crusher** to **Material Crusher** because its datapack-driven recipes support more than cobblestone.
- Renamed the Crusher registry path, Java classes, menu, block entity, models, textures, loot table, language keys, and GUI assets to `material_crusher`.
- Added synchronized `active` and four-frame `gear_frame` blockstates.
- Added four active side-panel texture frames so the exterior cogs visibly rotate while processing.
- Added an illuminated active front chamber texture.
- Added input-matched material dust at the front opening while processing block items.
- Added smoke and ash particles from the rear ventilation panel while active.
- Updated the GUI title, block name, machine config description, `README.md`, `TODO.md`, and `DECISIONS.md`.


### Basic Energy Cable framework

- Added the Basic Energy Cable block and item.
- Added six-way visual and collision connections to adjacent cables and standard NeoForge FE-capable blocks.
- Added connected-network scanning with one deterministic controller cable per network.
- Added server-side FE transfer from extract-capable endpoints to receive-capable endpoints.
- Added a configurable Basic Energy Cable network transfer limit.
- Added blockstate, multipart models, texture, item model, loot table, recipe, language entry, and creative-tab entry.
- Kept item and fluid transport intentionally out of scope.
- Updated `README.md` to describe the completed Crusher, Thermal Generator Mk I, and power systems.
- Added organized root `TODO.md` and `DECISIONS.md` documents.

- Fixed Thermal Generator Mk I FE export so stored power is pushed into adjacent standard NeoForge energy receivers, including the rear face of the Material Crusher.

### Fixed
- Fixed the Thermal Generator GUI so the complete 3x9 player inventory and hotbar slot frames are rendered.

This file is the single running changelog for Project Skyblock. New completed work should be appended here instead of creating feature-specific changelog files.

### Material Crusher rebuild and animated GUI

Target: Minecraft 1.21.1, NeoForge 21.1.235, Java 21.

## Crusher backend rebuild

- Replaced the corrupted monolithic Crusher block entity implementation with a clean maintainable rewrite.
- Removed duplicated nested definitions for processing results, power sources, output handlers, sided handlers, and energy storage.
- Split Crusher responsibilities into dedicated classes:
  - `MaterialCrusherBlock`
  - `MaterialCrusherBlockEntity`
  - `CrusherInventory`
  - `CrusherSidedItemHandler`
  - `CrusherEnergyStorage`
  - `CrusherProcessing`
  - `CrusherProcessingResult`
  - `CrusherPowerSource`
- Standardized the internal inventory to four valid slots:
  - Slot 0: processing input
  - Slot 1: furnace fuel
  - Slot 2: primary output
  - Slot 3: byproduct output
- Corrected the previous invalid slot-range crash involving slot 3.
- Added strict slot validation so invalid items cannot enter machine slots.
- Added strict sided NeoForge item automation:
  - Top exposes input insertion only.
  - Bottom exposes primary and byproduct extraction only.
  - Horizontal sides expose fuel insertion only.
- Prevented items from passing directly through the machine.
- Preserved inventory contents through block entity save/load.
- Preserved progress, burn time, burn duration, energy, and active power state through save/load.

## Hybrid FE and furnace-fuel power

- Added receive-only external FE capability support.
- Added a safe internal energy-consumption method for machine processing.
- Corrected the issue where the machine previously processed without actually reducing stored FE.
- Made FE the preferred power source whenever sufficient energy is available.
- Added automatic furnace-fuel fallback when FE is unavailable.
- Used the NeoForge 1.21.1-compatible fuel API:
  - `stack.getBurnTime(RecipeType.SMELTING)`
- Preserved support for the Creative Energy Cell as an FE test source.
- Added active power-source tracking for FE, fuel, and idle states.

## Crusher processing behavior

- Cobblestone always produces one Gravel.
- Cobblestone can produce one configurable extra Gravel.
- Gravel always produces one Sand.
- Gravel can produce one configurable extra Sand.
- Gravel can produce configurable Flint as a byproduct.
- Sand can produce `exdeorum:dust` only when Ex Deorum is installed and the item exists.
- No Project Skyblock dust item was introduced.
- Added output-capacity checks before consuming input or power.
- Prevented processing when either the primary output or byproduct output cannot accept the result.
- Kept processing speed, FE usage, and random output chances configuration-driven.

## Crusher block and world presentation

- Preserved the current distinct front, rear, and side Crusher textures and models.
- Preserved player-facing horizontal placement behavior.
- Preserved correct block, inventory, and dropped-item models.
- Preserved the front crushing opening.
- Preserved the rear ventilation panel.
- Preserved the side gear panels.
- Preserved front-facing working particles.
- Preserved current loot table and language resources.

## Crusher container menu

- Added `MaterialCrusherMenu`.
- Added server-authoritative menu opening from block interaction.
- Added a distance and block validity check through `stillValid`.
- Added all four machine slots to the container.
- Added the full player inventory and hotbar.
- Protected both output slots against manual insertion.
- Added shift-click routing:
  - Valid Crusher ingredients route to the input slot.
  - Valid furnace fuels route to the fuel slot.
  - Machine outputs route to the player inventory.
  - Other player items move between inventory and hotbar normally.
- Added synchronized container data for:
  - Processing progress
  - Total processing time
  - Remaining burn time
  - Total burn time
  - Stored FE
  - FE capacity
  - Active power source
  - Working state

## Crusher GUI

- Added `MaterialCrusherScreen`.
- Added menu registration and client screen registration.
- Added a dedicated Crusher GUI texture.
- Enlarged the GUI to provide a clear machine-focused layout.
- Added separate visual positions for:
  - Input slot
  - Fuel slot
  - Primary output slot
  - Byproduct output slot
  - Player inventory
  - Player hotbar
- Added a live green processing bar.
- Added a live vertical blue FE gauge.
- Added exact FE stored/capacity text.
- Added a live power-source label showing FE, Fuel, or Idle.
- Added hover tooltips for progress, fuel, and FE values.

## Animated machine presentation

- Added two animated cog sprites in the Crusher chamber.
- Made the left cog rotate clockwise while processing.
- Made the right cog rotate counterclockwise while processing.
- Added a vertically moving crusher head.
- Added furnace-style animated fuel flames.
- Added extra chamber flames while fuel is the active processing source.
- Added an animated highlight across the processing bar.
- Added a pulsing highlight through the FE gauge while FE is active.
- Added subtle input-slot vibration while processing.
- Added client-side stone dust particles inside the GUI.
- Added orange processing sparks.
- Added blue FE sparks while FE is the active power source.
- Added a larger particle burst when a processing operation completes.
- Added a stone-like completion clunk sound.

## Final visual alignment correction

- Corrected the cog pair appearing off-center in the machine chamber.
- Defined the chamber center as GUI X=89 based on the actual texture bounds.
- Shifted the complete animated mechanism six pixels left from its previous center.
- Positioned the cog centers symmetrically at eleven pixels to either side of the chamber center.
- Centered the crusher head using the same shared machine center.
- Repositioned the central fuel flames relative to the same center.
- Added shared animation-layout constants so future visual changes cannot independently drift out of alignment.
- Kept dust and spark effects centered on the corrected mechanism.

## Compatibility and scope

- Targeted Minecraft 1.21.1.
- Targeted NeoForge 21.1.235.
- Targeted Java 21.
- Kept mod ID `projectskyblock`.
- Kept package root `raziel23x.projectskyblock`.
- Did not replace current working Crusher block textures or models with older 1.16.5 assets.
- Used the original 1.16.5 implementation only as a behavioral and visual reference.
- Kept optional integration isolated from standalone functionality.
- Kept the tested processing backend unchanged during GUI visual improvements.

## Reagent block forms

- Added Red Reagent Block.
- Added Green Reagent Block.
- Added Blue Reagent Block.
- Added matching block items.
- Added the original reagent block textures.
- Added block models and inventory item models.
- Added blockstate definitions.
- Added loot tables so the blocks drop themselves.
- Added creative-tab entries.
- Added English display names.
- Kept reagent block recipes intentionally deferred for later recipe development.

## Data-driven Crusher recipes

- Added the custom `projectskyblock:crushing` recipe type and serializer.
- Replaced hardcoded Cobblestone, Gravel, Sand, Flint, and Ex Deorum output rules with datapack JSON recipes.
- Added built-in crushing recipes for Cobblestone to Gravel and Gravel to Sand with optional extra output and byproduct chances.
- Added a conditional Sand to `exdeorum:dust` recipe that loads only when Ex Deorum is installed.
- Removed the obsolete Crusher output-chance config entries; output counts and chances now belong to individual recipe JSON files.
- Kept global processing time, FE capacity, FE use, fuel settings, GUI behavior, automation, and save data unchanged.
- Updated input validation and shift-click routing to query the active server recipe manager.
- Crusher recipes now reload with datapacks and can be overridden or extended without rebuilding the mod.

### Crusher recipe JSON format

```json
{
  "type": "projectskyblock:crushing",
  "ingredient": { "item": "minecraft:cobblestone" },
  "result": { "id": "minecraft:gravel", "count": 1 },
  "extra_result_count": 1,
  "extra_result_chance": 0.1,
  "byproduct": { "id": "minecraft:flint", "count": 1 },
  "byproduct_chance": 0.15
}
```

- `ingredient` and `result` are required.
- `extra_result_count` defaults to `0`.
- `extra_result_chance` defaults to `0.0` and is clamped to `0.0` through `1.0`.
- `byproduct` is optional.
- `byproduct_chance` defaults to `0.0` and is clamped to `0.0` through `1.0`.

## GUI output panel correction

- Restored both right-side output slot frames from the last known-good GUI texture.
- Removed the previous oversized and overlapping arrow artwork.
- Added a compact processing arrow centered in the empty gap between the Crusher chamber and output slots.
- Kept every arrow pixel outside both output-slot frames.
- Preserved the right-aligned live FE text and the tested Crusher logic.

- Removed the stray partial process-arrow fragment between the Crusher output slots without changing the slot frames or main arrow.

### Machine framework
- Added a reusable receive-only machine energy storage with internal FE consumption.
- Added a reusable validated machine inventory with centralized dirty marking.
- Added a reusable sided item-handler view for controlled automation access.
- Refactored the Material Crusher energy, inventory, and sided handlers onto the shared machine foundation without changing its behavior.

### Added
- Began the Thermal Power System foundation.
- Added reusable validated machine fluid tanks with centralized dirty-state callbacks.
- Added reusable generator FE storage that accepts internally generated power and exposes extraction-only FE externally.
- Added furnace-fuel-to-thermal-fuel conversion helpers using the NeoForge 1.21.1-compatible `ItemStack#getBurnTime(RecipeType.SMELTING)` API.
- Added shared thermal fuel producer and consumer interfaces for future generators and machines.
- Added configurable Thermal Generator Mk I tank capacity, FE buffer, generation rate, extraction rate, FE-per-mB value, and solid-fuel conversion rate.

### Design
- Kept Project Skyblock standalone with no required external power or storage mods.
- Prepared the generator to interoperate through standard NeoForge FE and fluid capabilities.
- Deferred a standalone player-facing battery block until the Thermal Generator is tested with common optional storage mods.

### Added
- Added the functional Thermal Generator Mk I block, block entity, menu, and client screen.
- Added a furnace-fuel input slot using NeoForge furnace burn times.
- Added a lava-only internal fluid tank exposed through the standard NeoForge fluid capability.
- Added an extraction-only internal FE buffer exposed through the standard NeoForge energy capability.
- Added configurable FE generation, tank capacity, battery capacity, and output limits.
- Added inventory, tank, FE, and burn-state persistence.
- Added basic blockstate, model, texture, loot table, language, and creative-tab entries.

### Fixed
- Fixed the Thermal Generator fuel item handler to use the current shared sided-handler constructor.

### Changed
- Added a subtle pulsing internal glow behind the Material Crusher rear grille while active.
- Changed Thermal Generator solid-fuel handling so furnace fuels no longer generate FE directly.
- Furnace fuels now convert into lava-equivalent millibuckets using the configured burn-tick conversion rate.
- Solid fuel and externally supplied lava now share the same internal tank and the same FE-generation path.
- Fuel items are consumed only when their complete converted lava amount fits in the tank, preventing partial conversion or lost fuel.

### Fixed
- Thermal Generator now accepts large furnace fuels such as Blocks of Coal even when the lava tank is partially filled.
- Added a persistent solid-fuel conversion queue so the full lava-equivalent value is transferred into the tank over time without losing fuel.
- Clarified that vanilla Minecraft provides charcoal items but no charcoal block.

### Fixed
- Added hover tooltips to the Thermal Generator lava tank and FE storage gauges.
- Lava tooltip now displays the exact stored amount and capacity in millibuckets.
- FE tooltip now displays the exact stored energy and capacity.

### Fixed
- Aligned the Thermal Generator player inventory and hotbar with the Material Crusher layout.
- Standardized the three inventory rows, hotbar spacing, and inventory label position across both machine GUIs.

### Changed
- Standardized Project Skyblock machine player inventories on the Thermal Generator slot colors and spacing.
- Updated the Material Crusher player inventory and hotbar to use the shared machine GUI inventory renderer.
- Updated the Thermal Generator lava and FE gauges to use the Crusher-style raised vertical frame, highlight stripe, and measurement ticks.
- Added shared client-side machine GUI rendering helpers for inventory slots and vertical gauges.

### Fixed
- Centered the Material Crusher player inventory and hotbar within its wider GUI.
- Matched the Crusher inventory slot spacing, colors, and label alignment to the Thermal Generator machine GUI standard.

### Fixed
- Removed the obsolete baked Crusher inventory grid that appeared behind the centered shared machine inventory layout.

### Changed
- Added synchronized exterior active-state animation to Thermal Generator Mk I.
- Added a four-frame front turbine and rear cooling-fan animation while generating FE.
- Added a brighter active firebox and status light on the front face.
- Added rear exhaust smoke and occasional front flame/lava particles while active.
- Kept fuel conversion, lava storage, FE generation, and cable output behavior unchanged.

### Changed
- Reworked the Thermal Generator Mk I rear face into a full-panel exhaust and cooling grille.
- Expanded active exhaust particles across the complete rear grille instead of emitting from a small corner.
- Kept the animated cooling fan visible behind the full-width grille while the generator is active.

### Changed
- Polished the Thermal Generator Mk I full-panel exhaust so smoke originates just inside the rear grille and spreads across nearly the entire vent face.
- Added a subtle four-frame pulse to the active front firebox and warm status lighting.
- Kept FE generation, lava conversion, cable transfer, inventory, and GUI behavior unchanged.

### Changed
- Reworked the Material Crusher active front into one cohesive soft chamber glow behind the complete grille.
- Restored every dark grille bar, including the bar crossing the upper-left indicator area.
- Added a subtle four-frame brightness pulse while keeping the inactive front unchanged.

### Changed
- Unified the Thermal Generator Mk I and Material Crusher status presentation.
- Lowered the Thermal Generator status block for improved separation from the gauges.
- Replaced the Material Crusher's legacy red lamp and `Power: FE` text with a compact activity LED and `Processing`/`Idle` status.
- Kept FE storage in the Material Crusher title bar to avoid duplicate information.

### Changed
- Finalized the shared machine-status GUI layout before the powered-machine Git checkpoint.
- Simplified the Material Crusher progress caption to a centered percentage above the progress bar.
- Kept the Material Crusher status LED and Processing/Idle text separate from the progress display.
- Lowered and aligned the Thermal Generator status LED and two-line generation readout without overlapping the player inventory label.
- Standardized compact status presentation for future Project Skyblock machine GUIs.

### Changed
- Removed the legacy baked red status lamp from the Material Crusher GUI.
- Standardized both machine status LEDs on a darker mounted industrial bezel.
- Added clearer vertical separation between the Thermal Generator status readout and player inventory label.
- Raised the Material Crusher progress percentage slightly for cleaner centering above its progress bar.
- Redesigned the Basic Energy Cable with a slimmer blue conduit, smooth center highlight, and dark industrial connector collars.
- Preserved the existing FE network, connection, and transfer behavior while reducing cable texture noise.

### Final cable and GUI polish
- Rebuilt the Basic Energy Cable visual model as one uninterrupted conduit without per-block collars or rings.
- Added a seamless insulated blue cable texture so straight runs read as one continuous connection.
- Raised the Material Crusher status block slightly and lowered its percentage for cleaner spacing.
- Raised the Thermal Generator status block to restore breathing room above the inventory label.
### Basic Energy Cable subtle geometry polish
- Added quarter-pixel chamfers to the cable's outer edges while preserving its established silhouette and dimensions.
- Softened elbows and dense junctions through the shared chamfered arm and core geometry.
- Refined machine connector collars with a shorter stepped lip and thin cyan gasket.
- Kept cable connections, FE transfer, animation timing, and network behavior unchanged.
