# Test Plan — Engine Era Validation

## Milestone 20A typed transport network contract

The Gradle suite must include and pass `TransportTopologyTest`,
`TransportStepReservationsTest`, `TransportDispatchPlannerTest`, and
`TransportNetworkParticipantTest` together with all prior engine and boundary tests.

The contract must prove arbitrary typed channels, independent FE/item/fluid/gas native-unit
throughput, channel-filtered routing, shared-edge contention, stale-topology failure, exact planning
accounting, rotating fairness, bounded execution, and stable no-progress sleeping. Planning must not
mutate endpoints or introduce Minecraft/NeoForge types into the simulation package.

Standalone and integration smoke tests must confirm the existing world, Material Crushers, Thermal
Generators, Creative Energy Cell, cable fixture, and optional helper environment still load and shut
down cleanly. No M20A runtime behavior is expected from the live Basic Energy Cable.

## Milestone 19B2 Material Crusher migration contract

The Gradle suite must include and pass `MaterialCrusherLogicTest`,
`MaterialCrusherLegacySnapshotMigrationTest`, `MaterialCrusherItemHandlerTest`, and
`MaterialCrusherEnergyStorageTest` together with all prior engine and boundary tests.

Runtime validation must cover both standalone and integration profiles. Use the existing placed
crusher as a legacy-save fixture before placing a fresh crusher. Verify FE-only processing,
fuel-only processing, configured source preference, lava-bucket remainder handling, blocked outputs
without input/power loss, top/side/bottom automation, menu extraction, save/reload persistence,
`/reload` reevaluation, and clean shutdown. Confirm the Thermal Generators, Creative Energy Cell, and
all unrelated prototype fixtures remain unchanged.

## Build and repository gates

```cmd
gradlew.bat clean test build --warning-mode all
```

Expected:

- Tests and build succeed.
- Repository and release-artifact validation succeed.
- No Project Skyblock-owned Gradle deprecation warning remains.
- No development helper JAR or run-directory content appears in the release JAR.


## Milestone 19B1 combustion and persistence contract

The Gradle test suite must include and pass `MachineCombustionComponentTest` together with the
updated runtime, persistence, and NBT codec suites. Verify ignition and consumption bounds, no
redundant scheduler wake flag during internal burn progress, complete snapshot validation, schema-3
round trip, schema-1/schema-2 empty-combustion migration, and failure isolation.

Milestone 19B1 does not migrate a block entity. Standalone and integration launches remain regression
gates only; no new visible machine behavior should appear.

## Milestone 19A inventory contract

The Gradle test suite must include and pass:

- `MachineInventoryTransactionTest`
- `MachineInventoryViewTest`
- `MinecraftItemStackCodecTest`
- `EngineItemHandlerAdapterTest`

Verify that simulated capability calls do not mutate or wake the inventory, one multi-slot commit
wakes once, stale transactions fail closed, menu extraction can return input items, automation
respects sided access, component-bearing items round-trip exactly, transient or non-canonical state fails closed, and
undecodable state cannot be extracted or overwritten.

Milestone 19A does not register the new adapter on a block entity. In-game validation is therefore a
regression launch of the standalone and integration clients, not a claim that the Material Crusher
has already migrated.

## Standalone client

```cmd
gradlew.bat runStandaloneClient
```

Verify:

- The Mods screen contains Minecraft, NeoForge, Project Skyblock, and only required libraries.
- The preserved standalone TEST world loads.
- Generator, cable, crusher, automation, save/load, and Creative Energy Cell wake behavior still
  match the Milestone 17 baseline.
- No optional integration classloading or missing-mod errors appear.

## Integration client

```cmd
powershell -NoProfile -ExecutionPolicy Bypass -File tools\setup_integration_mods.ps1
gradlew.bat validateIntegrationEnvironment
gradlew.bat runIntegrationClient
```

Verify:

- The exact helper versions printed by the validator match the local ignored lock.
- JEI, Jade, The One Probe, CraftTweaker, KubeJS, ProbeJS, Curios API, Spark, ModernFix, and required
  dependencies appear in the Mods screen.
- The independent integration TEST world loads without changing the standalone world.
- Existing Project Skyblock prototype behavior remains functional.
- Curios Repair Gem behavior works installed and remains optional in the standalone profile.
- KubeJS/ProbeJS and CraftTweaker startup/reload boundaries do not own or corrupt engine state.
- Spark evidence is captured before performance claims.
- Clean Save and Quit followed by reload produces no Project Skyblock lifecycle errors.

## Evidence to retain

- Console output from the helper bootstrap and integration validator.
- Standalone and integration Mods-screen screenshots.
- Screenshots of the same prototype setup operating in both worlds.
- `run-standalone\logs\latest.log` and `run-integration\logs\latest.log` only when an error or
  suspicious warning occurs.
- GitHub Actions result after the tracked Milestone 18 changes are committed.


## Curios Repair Gem Regression

1. Launch `runIntegrationClient` and open `TEST-INTEGRATION`.
2. Open the Curios inventory and confirm one dedicated slot named **Repair Gem**.
3. Confirm the prototype slot icon loads and the slot accepts only
   `projectskyblock:repair_gem` through the default Curios tag validator.
4. Move the Repair Gem out of the normal inventory and into the dedicated slot.
5. Damage a repairable tool, keep it in the normal player inventory, and wait at least
   one configured repair interval (default: 20 ticks).
6. Confirm durability increases by the configured amount (default: 1) while the gem
   remains equipped.
7. Remove the gem from the Curios slot and confirm passive repair stops.
8. Save and quit, reload the world, and confirm the slot and equipped gem persist.
9. Launch `runStandaloneClient` separately and confirm Curios remains absent while the
   Repair Gem still repairs from the ordinary inventory.
