# Gradle Integration Testing

Project Skyblock provides two client runs through NeoForged ModDevGradle.

## Clean client

Run:

```bash
./gradlew runClient
```

This client uses `run-clean/` and loads only Project Skyblock and its declared dependencies. Use it to confirm that no accidental optional-mod dependency has been introduced.

## Integration client

Place NeoForge 1.21.1 test-mod JARs in:

```text
dev/mods/
```

Then run:

```bash
./gradlew runIntegrationClient
```

This client uses `run-integration/`. JARs in `dev/mods/` are added through the run-specific `integrationClientAdditionalRuntimeClasspath` configuration. They are not included in the published Project Skyblock JAR and are not exposed to downstream consumers.

Suggested integration coverage:

- EMI for recipe and category visibility
- Jade for block and block-entity information
- The One Probe for alternate HUD integration
- Curios for optional equipment compatibility
- Ex Deorum for skyblock progression interaction
- Spark for performance profiling

Keep all third-party JARs, test worlds, generated configs, screenshots, and benchmark output out of Git.

## Verification

Run the clean client first, then the integration client. A feature is not considered compatibility-tested merely because it works with the integration mods present; it must also launch without them.
