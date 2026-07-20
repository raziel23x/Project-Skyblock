# Proof of Concept Migration Plan

## Purpose

Use the working PoC content as a playable integration test for the simulation engine, then preserve the superseded implementation in this archive.

## Migration Order

1. Machine processing lifecycle
2. Material Crusher
3. Creative power source
4. Thermal Generator
5. Basic Energy Cable and multi-machine networks
6. Cobblestone, water, and lava utility generators
7. Sided inventory, fuel, energy, GUI, and remaining-face behavior
8. Test equipment, blocks, and items

## Required Proof Before Retirement

A PoC feature may be archived only when:

- its engine-driven replacement compiles;
- automated backend tests pass;
- local Gradle test and build pass;
- its essential behavior is verified in-game;
- the replacement obeys TPS, dirty-state, persistence, and adapter rules;
- the migration ledger identifies both the retired and replacement locations.

## Archive Procedure

1. Copy the superseded code, assets, and data into the matching legacy-reference directory.
2. Add context notes when copied files depend on removed APIs or generated resources.
3. Update `MIGRATION_LEDGER.md` with status, replacement, validation, and archive path.
4. Remove the superseded implementation from active source and resource paths.
5. Re-run tests and build to prove the archive is not part of runtime output.

## Historical Note

The PoC was not designed as a final machine framework. It was a sequence of working ideas. Concerns about reusable standards, replaceable parts, duplicated machine behavior, TPS, and excessive NBT eventually changed the project from "make another machine" into "build one simulation system every machine can reuse."
