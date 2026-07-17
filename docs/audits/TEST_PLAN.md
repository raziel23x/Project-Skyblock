# Test Plan — Audit Pass 1 Baseline

## Build

```cmd
gradlew.bat clean build
```

Expected:

- Build completes successfully.
- No missing model, texture, recipe, tag, or loot-table errors.
- No duplicate registry IDs.

## Client startup

```cmd
gradlew.bat runClient
```

Verify:

- Main menu loads.
- A new world can be created.
- No Project Skyblock exceptions appear in `latest.log`.

## Existing-content regression

Test the Repair Gem, wooden/flint equipment, reagents, Mixing Bowl, all current generators, Material Crusher, Thermal Generator Mk I, and straight/branched Basic Energy Cable networks.

## Performance baseline

Place large groups of generators, crushers, thermal generators, and branched cable networks. Record server tick time, log spam, capability invalidation, block updates, idle ticking, and cable rescans before adding new generator types.
