# Backend Milestone 18 — Tiered Developer Validation Environments

## Purpose

Create permanent, isolated standalone and full-integration development clients so optional tooling
can stress Project Skyblock without becoming a runtime requirement, contaminating baseline worlds,
or leaking into release artifacts.

## Implemented

### Isolated Gradle runs

- Replaced the ambiguous clean client run with `standaloneClient` and the explicit
  `run-standalone\` working directory.
- Retained `integrationClient` with the separate `run-integration\` working directory.
- Limited helper JAR loading to `integrationClientAdditionalRuntimeClasspath` from
  `dev\mods\integration\`.
- Preserved normal `clean`, `test`, `check`, and `build` behavior independently of helper JARs.

### Reproducible helper-mod bootstrap

- Added a tracked intent manifest for the approved NeoForge 1.21.1 helper set.
- Added a Windows PowerShell bootstrapper that resolves compatible Modrinth versions, recursively
  follows required dependencies, verifies SHA-512 hashes, and writes an ignored local lock file.
- Reuses the exact lock by default and requires an explicit `-Refresh` to evaluate newer builds.
- Refuses unmanaged extra JARs instead of silently changing the validation environment.
- Added a typed Gradle-native offline validator for lock/JAR verification.

### Packaging isolation

- Added `verifyReleaseArtifact` to `check`.
- Implemented it as a typed task with declared archive and prefix inputs so task execution does not
  reach back into the Gradle project model and remains compatible with the configuration cache.
- Rejects nested JARs and development workspace paths in the Project Skyblock release artifact.
- Expanded repository validation to enforce the two run directories, integration-only helper
  classpath, tracked manifest, ignored lock/JAR state, and required setup tools.

### Test-world isolation

- Established separate standalone and integration copies of the validated TEST world and options.
- Prevented generated helper configs, KubeJS data, logs, and world metadata from crossing between
  profiles.

### Corrected validated-world seed

- Replaced the provisional world copies after local testing showed they contained only the color-block fixture area.
- Seeded both profiles from the user-supplied Milestone 17 validated TEST world, including the established machine, cable, hopper, chest, persistence, processing, and wake-path rigs.
- Omitted only the transient `session.lock`; all durable world and player data is preserved.

### Helper release-policy correction

- Local resolution showed that JEI has compatible NeoForge 1.21.1 builds but no build classified as a stable release by Modrinth.
- JEI now has an explicit prerelease allowance. Selection remains ordered as `release`, `beta`, then `alpha`; all other direct projects retain their own tracked policy.
- Setup and offline validation output now include the selected version channel.

### Windows PowerShell REST-array correction

- Local execution under Windows PowerShell 5.1 exposed Modrinth version results as a nested `System.Object[]`, causing `date_published` to resolve to multiple values.
- The resolver now normalizes API collections before compatibility filtering and uses validated invariant `DateTimeOffset` sort keys.
- This correction affects only developer-environment resolution; it has no runtime, gameplay, save, API, or release dependency impact.

### Native Gradle validation correction

- Local testing showed that `validateIntegrationEnvironment` incorrectly required a system-wide `python` command, which is not part of the approved Windows/JDK workflow.
- The task now performs manifest/lock comparison, filename validation, exact SHA-512 verification, direct/dependency classification, and unmanaged-JAR rejection natively inside a typed Gradle task.
- Added transactional migration of ignored local helper state between complete source replacement packages to avoid needless redownloads during harness hardening.

### Groovy helper-resolution correction

- Local execution showed closure delegation resolving `requireString()` against the task instance.
- Validator traversal now uses explicit loops with class-qualified static helper calls.
- No gameplay, save, API, dependency, or runtime behavior changed.

## Approved Initial Helper Set

- JEI
- Jade
- The One Probe
- CraftTweaker
- KubeJS
- ProbeJS
- Curios API
- Spark
- ModernFix

JEI is used for the first recipe-viewer baseline. EMI is deliberately deferred to an independent
later check rather than loaded alongside JEI without a distinct validation purpose.

## Architectural Boundaries

- The standalone environment is the authority for proving no accidental optional dependency.
- The integration environment is an observation and compatibility harness, not a gameplay owner.
- No third-party JAR is committed, published, nested, or added as a required dependency.
- Exact helper versions are local validation evidence and may change only through an explicit lock
  refresh.
- This milestone creates the test infrastructure; it does not prematurely implement or freeze
  Project Skyblock-specific public integrations.

## Validation Available in the Delivery Sandbox

- Repository validator passes with the new environment contracts.
- Integration manifest and Gradle-native lock validator pass repository structural validation.
- Release-package layout, ignored development paths, and documentation links are checked.
- The sandbox cannot resolve external Gradle or Modrinth downloads, so the Windows bootstrap,
  helper-client launch, and full Gradle build remain required local gates.

## Gameplay, Save, API, and Dependency Impact

- **Gameplay:** no gameplay, progression, economy, or balance change.
- **Save:** no engine save-schema change; the delivery provides two independent copies of the
  validated development world.
- **API:** no public API is added or frozen.
- **Dependencies:** no new Project Skyblock runtime dependency; helper mods are local development
  files loaded only by the integration run.

## Local Runtime Validation Completed

- The integration environment loaded all approved direct helpers and required dependencies.
- Existing machines, overlays, menus, worlds, and save/reload behavior remained functional.
- The dedicated Repair Gem Curios slot appeared and the equipped gem performed its repair behavior.
- Curios remained absent from the standalone profile, preserving the ordinary-inventory fallback.
- The exact helper lock remains ignored local validation state by design.
- The next complete `clean test build` is retained as a regression gate in the Milestone 19A test
  delivery; no unreported Gradle result is claimed here.


### Integration launch-classpath correction

- The first integration launch preserved gameplay but its Mods screen matched standalone.
- The dedicated run source set now owns the optional helper runtime graph.
- Standalone and integration classpaths are explicitly isolated.
- No production dependency, save format, gameplay, balance, or public API changed.


### Repair Gem Curios slot migration finding

- Full integration gameplay testing showed that the Repair Gem server-side bridge
  existed, but the 1.16-era dedicated slot registration had not been migrated.
- Curios 1.21.x slot registration is now implemented through authoritative datapack
  resources: slot type, player entity assignment, item tag, icon, and localization.
- The implementation adds no Curios compile-time or required runtime dependency.
- Added a packaged-resource JUnit regression test and repository schema checks.
- Gameplay intent is restored rather than rebalanced: one dedicated Repair Gem slot,
  default drop behavior, no cosmetic slot, and no render toggle.


### Deferred slot artwork

The first Repair Gem slot icon proves resource registration but resembles the equipped item too
closely. Final empty-slot artwork is deferred to the Game Era and must use the permanent transparent
grayscale-outline rule. No engine milestone time is spent iterating presentation-only artwork.
