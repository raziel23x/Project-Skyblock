# Changelog

## Unreleased — Engine Milestone 19B1

- Added a Minecraft-independent combustion reservoir for durable remaining and total burn work.
- Integrated combustion into machine runtime composition, diagnostics, capture, validation, and transactional restore.
- Added machine snapshot schema 3 while preserving schema 1 and 2 reads as empty combustion state.
- Added focused combustion, persistence, shared-dirty-state, and schema-migration regression tests.
- Kept the Material Crusher, gameplay balance, recipes, capability registration, and visible behavior unchanged.

## Unreleased — Engine Milestone 19A

- Added atomic multi-slot inventory transactions with stale-candidate rejection and one-wake commit semantics.
- Added separate automation and player-menu views over simulation-owned slot state.
- Added canonical typed data-component persistence encoding using namespaced registry keys, exact round-trip verification, and no NBT/SNBT runtime transport.
- Added a fail-closed NeoForge item capability adapter with simulation safety and boundary diagnostics.
- Added focused transaction, view, codec, and capability regression tests.
- Kept the Material Crusher, gameplay balance, save schema, runtime dependencies, and public API unchanged.

## Unreleased — Engine Milestone 18

### Milestone 18 Repair Gem Curios slot restoration
- Restored the dedicated Repair Gem Curios slot using the Curios 1.21.x datapack model.
- Added player slot assignment, exact item tag validation, a localized slot name, and a
  dedicated 16x16 slot icon.
- Preserved the existing guarded reflection bridge and inventory fallback, so Curios
  remains optional and the Repair Gem still works without it.
- Added packaged-resource JUnit coverage and repository schema validation.


### Milestone 18 integration runtime-classpath correction
- Local testing proved that the integration client launched but loaded only Minecraft, NeoForge, and Project Skyblock.
- Replaced the ineffective per-run additional-runtime configuration with a dedicated `integrationRun` source set and isolated `integrationHelperRuntime` configuration.
- Bound the standalone client to `main` and the integration client to `integrationRun`.
- Made verified lock/JAR validation an automatic prerequisite of integration-client launch.


### Milestone 18 Groovy helper-resolution correction
- Replaced closure-based iteration in the typed integration validator with explicit loops.
- Qualified static helper calls to prevent Groovy from resolving them against the task instance.
- Added repository validation rejecting `eachWithIndex` in Gradle build logic.


### Milestone 18 native validation correction
- Replaced the local `python`-backed integration verification task with a typed, configuration-cache-safe Gradle task using the JDK JSON/Groovy runtime and SHA-512 APIs.
- Removed the redundant Python integration validator so the Gradle task is the single authoritative offline lock/JAR validator.
- Added a transactional PowerShell helper that preserves ignored integration locks and verified helper JARs across complete replacement-package installs.
- Local integration validation now requires only the existing JDK/Gradle toolchain.


### Milestone 18 Windows PowerShell resolver correction
- Normalized Modrinth REST array responses explicitly because Windows PowerShell 5.1 can preserve a JSON array as one nested `System.Object[]` pipeline value.
- Replaced the ambiguous date cast used by version sorting with invariant, validated `DateTimeOffset` parsing and precise malformed-response diagnostics.
- Added repository checks preventing regression to the unsafe nested-array sort pattern.


### Milestone 18 helper-version policy correction
- Allowed JEI prerelease selection for the NeoForge 1.21.1 developer client after the live resolver confirmed that compatible JEI files are published on the beta channel.
- Kept release-first selection and made resolved release channels explicit in setup and validation output.
- Strengthened manifest validation for roles and prerelease-policy types.


### Milestone 18 validation-world correction
- Replaced the provisional color-block-only world copies with the user-supplied, validated Milestone 17 TEST world containing the established machine, cable, automation, persistence, and wake-path rigs.
- Copied the validated world independently into the standalone and integration profiles while omitting only the transient `session.lock` file.


- Added isolated standalone and full-integration NeoForge client environments.
- Added a tracked helper-mod intent manifest and a Windows PowerShell bootstrapper that resolves
  compatible Modrinth files, follows required dependencies, verifies SHA-512 hashes, and writes an
  ignored exact-version lock.
- Added offline integration-environment validation and refused unmanaged helper JARs.
- Added configuration-cache-safe release-artifact isolation checks so development JARs and run
  directories cannot be packaged with Project Skyblock.
- Preserved independent standalone and integration copies of the validated TEST world.
- Kept every helper mod development-only and avoided adding or freezing public integration APIs.

## Unreleased — Engine Milestone 17

- Hardened the greenfield simulation engine baseline before additional prototype stress migrations.
- Clarified machine, cable, connection, and route transfer limits as per-operation contracts and reserved shared aggregate budgeting for the network scheduler.
- Made machine snapshot restore transactional and added schema migration for opaque adapter-owned item state.
- Bounded typed item-state payloads and explicitly rejected NBT/SNBT runtime codecs.
- Isolated scheduler participant and deferred platform-integration failures so one invalid workload cannot stop unrelated simulation work.
- Made energy-network topology publication deterministic and reject conflicting parallel connection definitions.
- Made material and processing-route reload publication atomic with last-known-good retention and empty-candidate rejection.
- Namespaced Project Skyblock custom datapack roots to avoid collisions with unrelated mods.
- Added regression, failure-injection, codec, reload, topology, thermal, and item-state tests.
- Added CI, project validation, documentation-manifest tooling, and the permanent tiered developer validation plan.
- Documented the engine as greenfield, scale-driven development and the existing gameplay objects as temporary stress-test prototypes.

## Unreleased — Engine Milestone 16

- Migrated the Creative Energy Cell to the engine-owned runtime and level scheduler.
- Added the first NeoForge energy capability adapter over backend-owned state.
- Removed the Creative Energy Cell's independent server ticker.
- Added load-time reevaluation, post-execution platform integration, and backend logic tests.

## Unreleased — Engine Milestone 15

- Added one bounded simulation scheduler per logical server level.
- Added execution observation so platform integration flushes only machines that actually ran and became dirty.
- Connected `EngineMachineBlockEntity` lifecycle to the level scheduler manager.
- Added deterministic level-unload cleanup without per-machine tick loops.

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
