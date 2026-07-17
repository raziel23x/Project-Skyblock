# Local Development Workspace

This directory holds developer-only integration assets.

Place NeoForge 1.21.1 compatibility and diagnostic mod JARs in `dev/mods/`.
The JARs are ignored by Git and are loaded only by the Gradle `integrationClient` run.

Suggested test mods include EMI, Jade, The One Probe, Curios, Ex Deorum, and Spark.
Do not commit third-party JAR files.
