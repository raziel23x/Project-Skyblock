# Local Developer Validation Workspace

Project Skyblock uses two isolated client environments.

## Standalone client

```cmd
gradlew.bat runStandaloneClient
```

Working directory: `run-standalone\`

This client loads Minecraft, NeoForge, Project Skyblock, and only genuinely required libraries. It
must remain functional when every optional helper mod is absent.

## Integration client

First resolve and download the tracked helper set into the ignored development directory:

```cmd
powershell -NoProfile -ExecutionPolicy Bypass -File tools\setup_integration_mods.ps1
```

Then validate the exact local lock and JAR hashes:

```cmd
gradlew.bat validateIntegrationEnvironment
```

Launch the isolated helper-mod client:

```cmd
gradlew.bat runIntegrationClient
```

Working directory: `run-integration\`

The tracked manifest is `dev\integration-mods.json`. The bootstrapper resolves compatible
NeoForge 1.21.1 files from Modrinth, follows required dependencies, verifies SHA-512 hashes, and
writes the exact selected versions to the ignored `dev\integration-mods.lock.json` file. Existing
lock data is reused by default; use `-Refresh` only when intentionally evaluating newer builds.

Third-party JARs are stored under `dev\mods\integration\`, ignored by Git, loaded only by the
integration run, and never published or bundled with Project Skyblock.
