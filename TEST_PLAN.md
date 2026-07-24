# Test Plan — Engine Era Validation

## Build and repository gates

```cmd
gradlew.bat clean test build --warning-mode all
```

Expected:

- Tests and build succeed.
- Repository and release-artifact validation succeed.
- No Project Skyblock-owned Gradle deprecation warning remains.
- No development helper JAR or run-directory content appears in the release JAR.

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
3. Confirm the slot uses the Repair Gem icon and accepts only
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
