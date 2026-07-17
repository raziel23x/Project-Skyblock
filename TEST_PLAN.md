# Test Plan — Audit Pass 1 Baseline

Run these tests before adding the first legacy migration batch.

## Build

```cmd
gradlew.bat clean build
```

Expected:

- Build completes successfully.
- No missing model, texture, recipe, tag, or loot-table errors.
- No duplicate registry IDs.
- Generated resources are not accidentally committed unless intentionally managed.

## Client startup

```cmd
gradlew.bat runClient
```

Verify:

- Main menu loads.
- A new world can be created.
- No Project Skyblock exceptions appear in `latest.log`.

## Existing-content regression

Test:

- Repair Gem in inventory, offhand, armor context, and Curios when installed.
- Wooden and Flint armor recipes and equipping.
- Wooden and Flint shears.
- Flint tool mining tiers.
- Reagent items and blocks.
- Mixing Bowl container/remainder behavior.
- Cobblestone Generator manual extraction and upward automation.
- Water and Lava Generator bucket interaction and fluid extraction.
- Material Crusher fuel fallback, FE input, sided inventory, recipe processing, and GUI synchronization.
- Thermal Generator fuel/lava behavior, FE storage, and output.
- Straight and branched Basic Energy Cable networks.

## Performance baseline

Place:

- 100 Cobblestone Generators
- 100 fluid generators
- 50 Material Crushers
- 50 Thermal Generators
- A branched cable network connecting multiple producers and consumers

Observe:

- Server tick time
- Log spam
- repeated capability invalidation
- unnecessary block updates
- machines ticking while idle
- cable network rescans

Record results before adding new generator types so regressions are measurable.
