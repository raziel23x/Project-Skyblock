# Developer Validation Environment

## Purpose

Maintain two small, reproducible client environments that prove optional integrations remain
optional and that integration diagnostics do not contaminate the standalone baseline.

Third-party JARs are development-only. They are never bundled into or published with Project
Skyblock and never own authoritative simulation state.

## Environment A — Standalone Core

Run with:

```cmd
gradlew.bat runStandaloneClient
```

Working directory: `run-standalone\`

Expected runtime contents:

- Minecraft
- NeoForge
- Project Skyblock
- only genuinely required libraries

This environment proves that no recipe viewer, overlay, profiler, scripting system, equipment API,
or optimization mod has become an accidental dependency.

## Environment B — Full Integration Validation

Prepare the locally locked helper set with:

```cmd
powershell -NoProfile -ExecutionPolicy Bypass -File tools\setup_integration_mods.ps1
```

Validate the lock and downloaded hashes with:

```cmd
gradlew.bat validateIntegrationEnvironment
```

Launch with:

```cmd
gradlew.bat runIntegrationClient
```

Working directory: `run-integration\`

The initial tracked helper set is:

- JEI for item and recipe visibility
- Jade for observation overlays
- The One Probe as an independent observation provider
- CraftTweaker for optional scripting and reload consumption
- KubeJS for optional public-API scripting and orchestration
- ProbeJS for scripting-surface inspection
- Curios API for optional equipment integration
- Spark for scheduler and runtime profiling
- ModernFix for lifecycle, reload, and compatibility stress

JEI is the initial recipe-viewer baseline. EMI remains a later independent compatibility check when
it provides a distinct test need; two recipe viewers are not loaded together merely to increase the
mod count.

## Version and Dependency Locking

`dev/integration-mods.json` is the tracked intent manifest. It records project slugs, roles, target
Minecraft version, loader, and whether a project may use a prerelease when no compatible release
exists.

`tools/setup_integration_mods.ps1`:

1. queries the Modrinth API for NeoForge 1.21.1 builds;
2. prefers releases and permits prereleases only where the tracked manifest allows them;
3. follows required Modrinth dependencies recursively;
4. selects each primary JAR;
5. verifies the published SHA-512 hash before installation;
6. writes the exact resolved graph to the ignored `dev/integration-mods.lock.json`;
7. refuses unmanaged extra JARs in the integration directory.

The lock is reused by default so repeated launches use the same files. Passing `-Refresh` is an
explicit decision to resolve a newer compatible graph. Exact tested versions belong in validation
evidence, not in the permanent source manifest.

## Isolation Boundaries

- Standalone and integration clients use separate writable game directories.
- Each environment has its own options, configs, logs, crash reports, generated scripts, and world
  copy.
- Integration helper JARs enter the integration client only through the source-free `integrationRun`
  source set and its `integrationHelperRuntime` configuration.
- Helper JARs are ignored by Git and absent from normal runtime and published dependency metadata.
- `verifyReleaseArtifact` runs as part of `check`, uses declared task inputs rather than execution-time
  project-model access, and rejects nested JARs or development workspace paths in the Project
  Skyblock release JAR.
- Optional integration classes must not load when their target mod is absent.
- A helper may observe or call supported adapters/public contracts; it may not become authoritative.

## Required Validation Matrix

### Standalone

- Launch the preserved standalone test world.
- Confirm only Minecraft, NeoForge, Project Skyblock, and required libraries are present.
- Repeat the validated generator, cable, crusher, automation, persistence, and wake-path checks.
- Confirm no optional-mod classloading or missing-mod errors.

### Integration

- Confirm every locked helper and required dependency appears in the Mods screen.
- Confirm the same Project Skyblock prototype baseline still loads and operates.
- Validate current Curios Repair Gem behavior installed and absent.
- Record what Jade, TOP, and JEI can observe before Project Skyblock-specific adapters are added.
- Exercise KubeJS, ProbeJS, and CraftTweaker startup/reload boundaries without allowing scripts to
  own core state.
- Capture Spark evidence before making performance claims.
- Confirm ModernFix does not expose lifecycle, reload, or shutdown defects.
- Cleanly save and quit, then verify the integration world reloads independently of the standalone
  world.

## Gradle Validator Implementation Rule

Validation tasks use explicit loops and class-qualified helper calls where Groovy closure delegation could redirect method resolution to a Gradle task instance.

## Local Validation Toolchain

`gradlew.bat validateIntegrationEnvironment` is implemented as a typed Gradle task. It requires no system-wide Python installation. The task parses the tracked manifest and ignored exact-version lock, verifies every downloaded JAR with SHA-512, rejects unmanaged JARs, confirms the direct-project set, and prints the exact locked environment.

When installing a complete replacement source package, use `tools\restore_local_validation_state.ps1` to migrate the ignored lock and verified helper JARs from the previous project folder without downloading them again.

## Windows PowerShell Compatibility

The bootstrapper supports Windows PowerShell 5.1 and newer PowerShell releases. Modrinth JSON arrays are normalized explicitly before filtering and sorting because Windows PowerShell 5.1 may expose an API array as one nested `System.Object[]` value. Publication dates are parsed invariantly and rejected with a version-specific error when malformed.

## Helper Version Selection Policy

The integration bootstrapper always prefers the newest compatible Modrinth `release`. A tracked project may explicitly allow fallback to `beta`, then `alpha`, only when no compatible release exists. This is a development-client policy and does not create a Project Skyblock runtime dependency. JEI currently requires that fallback for the NeoForge 1.21.1 validation target. The generated lock records the exact selected channel, version, file, source URL, and SHA-512 hash.

## Preserved Validation World

Both profiles are seeded from the same user-supplied Milestone 17 `run-clean/saves/TEST` snapshot that passed machine, cable, automation, persistence, processing-resume, and Creative Energy Cell wake validation. The snapshot is copied into separate writable world folders so helper-mod metadata cannot contaminate the standalone authority. Only the transient Minecraft `session.lock` file is omitted from the delivery.

## Deliberate Boundary of Milestone 18

Milestone 18 establishes reproducible environments and packaging isolation. It does not claim that
Jade, TOP, JEI, KubeJS, CraftTweaker, or ProbeJS already expose complete Project Skyblock-specific
adapters. Those integrations are implemented only when a real public contract is ready to prove,
and each must also be tested with its target mod absent.

## Run-Classpath Isolation

The standalone client launches from `main`. The integration client launches from a source-free
`integrationRun` source set whose runtime classpath contains the normal Project Skyblock runtime plus
locally verified helper JARs supplied by `integrationHelperRuntime`. The helper configuration is
absent from standalone execution, compilation, publication metadata, and release artifacts.

`runIntegrationClient` depends on `validateIntegrationEnvironment`, preventing a missing lock,
hash mismatch, missing JAR, or unmanaged JAR from silently producing a false standalone-equivalent
integration launch.

## Curios Slot Validation

The integration profile must expose one localized `Repair Gem` Curios slot, accept the
Repair Gem through `curios:repair_gem`, preserve the equipped stack across save/reload,
and allow the existing server-side repair loop to detect it. The standalone profile
must remain free of Curios while retaining ordinary-inventory Repair Gem behavior.
