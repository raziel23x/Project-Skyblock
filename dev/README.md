# Local Development Workspace

This directory holds developer-only integration assets.

Place NeoForge 1.21.1 compatibility and diagnostic mod JARs in `dev/mods/`. The JARs are
ignored by Git and are loaded only by the Gradle `integrationClient` run.

Permanent validation candidates are JEI, Jade, The One Probe, CraftTweaker, Curios API, Spark,
and ModernFix. KubeJS and ProbeJS form the optional scripting-validation tier. Test Project
Skyblock by itself before either integration tier, then use a large modpack only after the small
environments pass.

See [`docs/03-engineering/DEVELOPER_VALIDATION_ENVIRONMENT.md`](../docs/03-engineering/DEVELOPER_VALIDATION_ENVIRONMENT.md).
Do not commit third-party JAR files.
