# Developer Validation Environment

## Purpose

Maintain a small, reproducible environment that exercises Project Skyblock integration boundaries
without the startup cost and diagnostic noise of a large modpack.

Third-party JARs are development-only. They are never bundled into or published with Project
Skyblock.

## Validation Tiers

### Tier 1 — Bare Core

- Minecraft
- NeoForge
- Project Skyblock

This tier proves that no optional integration has become an accidental runtime dependency.

### Tier 2 — Optional Integration Baseline

- JEI
- Jade
- The One Probe
- CraftTweaker
- Curios API
- Spark
- ModernFix

Each integration is tested both present and absent where practical. Jade and TOP should also be
validated independently so neither provider masks the other's loading problems.

### Tier 3 — Scripting Validation

- KubeJS
- ProbeJS

KubeJS is an optional consumer of the Project Skyblock public API. It may customize and orchestrate
supported behavior, but it never owns authoritative simulation state or bypasses engine validation.

### Tier 4 — Large Ecosystem

Validate in a realistic modpack only after the smaller tiers pass. Large packs are useful for
finding ecosystem interactions but are not the primary place to diagnose Project Skyblock itself.

## Required Checks

- Launch with every optional integration absent.
- Launch with each integration independently where meaningful.
- Confirm optional classes are not loaded when their mod is absent.
- Confirm development dependencies do not appear in the release artifact.
- Exercise reload, persistence, world restart, dimension change, and server shutdown boundaries.
- Capture Spark evidence before making performance claims.
- Record exact tested versions in the validation report for each baseline candidate.

## Workspace

Place NeoForge 1.21.1 development-only mod JARs in `dev/mods/` and launch the Gradle
`integrationClient` run. Do not commit third-party JARs.
