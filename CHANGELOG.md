# Changelog

## Unreleased — Engine Milestone 13

- Added the reusable Machine Processing Component and composed runtime integration.
- Replaced the old resource-generator TODO backlog with an engine and real-mod development plan.
- Integrated project engineering principles into the canonical documentation structure.
- Defined the Proof of Concept migration and legacy archive procedure.

# Release Candidate Readability and Cable Housing Fix

- Rebuilt the Basic Energy Cable geometry with a graphite outer housing and a recessed animated cyan energy core.
- Rebuilt the cable inventory model and machine collar to match the in-world cable.
- Increased graphite dominance and narrowed the cyan channel on the Structural Energy Frame.
- Changed the Thermal Generator gauge labels to high-contrast orange and cyan with text shadows.
- Preserved FE transfer, machine logic, GUI coordinates, particles, and network behavior.

# Project Skyblock Changelog

## Unreleased — full machine and cable audit

### Fixed
- Corrected Thermal Generator lava-to-FE remainder accounting so partial millibucket energy is carried into later ticks instead of being lost.
- Clamped persisted Thermal Generator conversion counters to non-negative values when loading older or malformed saves.
- Updated the Material Crusher visual-state writer to modify the current world blockstate instead of a potentially stale ticker snapshot.
- Moved the Material Crusher `Inventory` label below the status row while preserving the tested slot-grid positions.
- Made the Creative Energy Cell push FE through the standard NeoForge energy capability instead of special-casing only the Material Crusher.
- Added an unsided Material Crusher item-handler view for capability consumers that query without a face.
- Removed an accidentally packaged source-art ZIP from the runtime resource directory.

### Refactored
- Added a one-tick cable topology cache. Each connected Basic Energy Cable / Structural Energy Frame network is now scanned once per server tick rather than once from every cable block entity.
- Split cable topology discovery from FE transfer logic into `EnergyCableNetworkManager`.
- Rebuilt Structural Energy Frame models as graphite outer rails with narrow animated cyan energy channels and graphite machine collars.
- Added `tools/validate_project.py` for JSON, model, texture, PNG, and packaged-resource validation.

### Audited
- Reviewed registrations, capabilities, menus, screens, block entities, persistence, shift-click handling, recipes, loot tables, blockstates, models, textures, and language resources.
- Verified the Material Crusher recipe/output flow, dual FE/furnace-fuel power fallback, sided automation, progress synchronization, animation state, and output insertion rules.
- Verified the Thermal Generator fuel queue, lava tank, FE export, persistence, capability exposure, status synchronization, and GUI slot alignment.
- Verified Basic Energy Cable and Structural Energy Frame interoperability and deterministic network-controller selection.

## Current machine-system milestones

### Material Crusher
- Data-driven crushing recipes with primary and optional byproduct outputs.
- FE or furnace-fuel operation with configurable preference and costs.
- Sided item automation, animated exterior gears, chamber glow, particles, FE gauge, and progress GUI.

### Thermal Generator Mk I
- Converts furnace fuel into lava-equivalent thermal fuel and lava into FE.
- Exposes item, fluid, and FE capabilities with animated exterior state and exhaust particles.

### Energy network
- Six-direction Basic Energy Cable connections and machine collars.
- Structural Energy Frame visual variant with identical network behavior.
- Configurable per-network transfer limit and deterministic controller processing.

Historical development notes were preserved in `docs/CHANGELOG_LEGACY.md`.

## Release candidate visual lock

- Rebuilt the Structural Energy Frame with unique, graphite-dominant assets so it can no longer resolve to the legacy all-cyan lattice textures.
- Added narrow animated cyan channels recessed inside the graphite rails.
- Added reinforced junction nodes and a short machine-side flange with a cyan gasket.
- Updated the Structural Energy Frame inventory model to match the in-world design.
- Removed obsolete Structural Energy Frame texture files.
- Corrected the final Material Crusher `Inventory` label overlap without moving tested inventory or hotbar slots.
- Preserved the audited FE network, Thermal Generator, Material Crusher, cable transfer, particle, and GUI behavior.
