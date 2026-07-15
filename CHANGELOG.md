# Project Skyblock Changelog

## Unreleased

This file is the single running changelog for Project Skyblock. New completed work should be appended here instead of creating feature-specific changelog files.

### Cobblestone Crusher rebuild and animated GUI

Target: Minecraft 1.21.1, NeoForge 21.1.235, Java 21.

## Crusher backend rebuild

- Replaced the corrupted monolithic Crusher block entity implementation with a clean maintainable rewrite.
- Removed duplicated nested definitions for processing results, power sources, output handlers, sided handlers, and energy storage.
- Split Crusher responsibilities into dedicated classes:
  - `CobblestoneCrusherBlock`
  - `CobblestoneCrusherBlockEntity`
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

- Added `CobblestoneCrusherMenu`.
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

- Added `CobblestoneCrusherScreen`.
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
- Refactored the Cobblestone Crusher energy, inventory, and sided handlers onto the shared machine foundation without changing its behavior.
