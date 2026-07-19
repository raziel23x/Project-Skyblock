# Backend Milestone 10 — Machine Thermal Component

## Purpose

Connect the existing Minecraft-independent thermal core to reusable machine composition without moving temperature, heat, operating-range, or ambient-exchange authority into block entities or platform capabilities.

## Implemented Contracts

### External heat access

- `MachineThermalAccess`

The access mode independently controls whether external adapters may add heat, remove heat, do both, or do neither. Internal machine operations remain separate from external access.

### Authoritative machine component

- `MachineThermalComponent`

The component composes `ThermalState`, `ThermalProperties`, and `ThermalEngine`. It exposes authoritative temperature, thermal energy, heat capacity, operating condition, external heat operations, internal process operations, bounded environmental exchange, restoration, diagnostics, snapshots, dirty signaling, and wake signaling.

### Observability and persistence boundary

- `MachineThermalSnapshot`
- `MachineThermalDiagnostics`

Snapshots contain immutable authoritative values suitable for persistence and synchronization adapters. Diagnostics add lifetime heat-flow counters, environmental net heat, current condition, and change count.

## Operation Model

External adapters use:

- `receiveHeat(amount)`
- `extractHeat(amount)`

Owning machine logic uses:

- `generateHeat(amount)`
- `consumeHeat(amount)`
- `step(environment, generatedHeat)`

Internal operations intentionally bypass external access restrictions. All paths still mutate the same authoritative `ThermalState`.

## Thermal Semantics

Temperature is derived from fixed-point thermal energy and heat capacity. Small heat transfers are retained even when they are not large enough to change the exposed milli-kelvin value.

`ThermalProperties` defines:

- conductance;
- minimum operating temperature;
- maximum operating temperature;
- shutdown temperature.

These are operating conditions, not arbitrary hard clamps. Absolute zero remains the lower physical bound enforced by `ThermalState`.

## Environmental Exchange

The component delegates exchange calculations to `ThermalEngine`. Minecraft-facing code translates biome, dimension, weather, nearby fluids, or machine enclosure facts into a narrow `ThermalEnvironment` value. The backend never queries the world directly.

Positive environmental exchange means heat entered the machine. Negative exchange means heat left it. Exchange is bounded by conductance, energy needed to reach ambient equilibrium, and the environment's per-tick maximum.

## Invariants

1. Thermal energy and heat requests are non-negative.
2. Heat capacity remains positive and immutable for a `ThermalState`.
3. Temperature never crosses below absolute zero.
4. External operations obey `MachineThermalAccess`.
5. Internal machine operations do not depend on external access.
6. Zero-effect operations do not mark state dirty or wake the owner.
7. Meaningful changes mark persistence, client synchronization, and scheduler dirty flags.
8. Meaningful changes wake the owning participant.
9. Restored thermal energy mutates the authoritative state rather than creating a duplicate value.
10. Operating and shutdown conditions are derived from shared thermal properties.

## Automated Validation

`MachineThermalComponentTest` covers:

- external versus internal heat operations;
- operating-range and shutdown conditions;
- bounded ambient cooling and diagnostics;
- generated heat through a full thermal step;
- dirty-state and single wake signaling;
- immutable snapshot values;
- restored authoritative energy;
- negative-request rejection;
- zero-effect operation behavior.

## Ownership Boundary

The component contains no Minecraft or NeoForge imports. Block entities, biomes, dimensions, fluids, menus, packets, capabilities, and save formats remain outside the thermal authority boundary.

## Deliberately Deferred

- NeoForge or third-party heat capability adapters;
- biome and dimension environment translation;
- neighboring-machine thermal networks;
- machine composition proof;
- active cooling equipment;
- phase changes and fluid/gas coupling;
- production-machine migration.

## Completion Status

Implementation, documentation, and automated tests are included. The delivery environment performed direct Java compilation of the Minecraft-independent production sources. Full Gradle test, build, and client validation must be run in the normal local development environment because the Gradle distribution could not be downloaded in the isolated delivery environment.
