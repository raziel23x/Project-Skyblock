# Backend Milestone 2 - Thermal Core

## Purpose

Establish one deterministic thermal authority for machines, generators, cables,
and future simulation participants. Minecraft-specific facts remain outside the
thermal package and are translated into `ThermalEnvironment` values by the
platform integration layer.

## Added or completed

- Fixed-point thermal energy storage with retained sub-milli-kelvin energy.
- Stateless `ThermalEngine` for generated heat and ambient exchange.
- Explicit `ThermalEnvironment` boundary.
- Operating, hot, cold, and shutdown condition derivation.
- Equilibrium-safe body-to-body heat transfer.
- Immutable per-step diagnostics.
- Shared temperature constants and Celsius conversion helper.

## Important behavior

`ThermalState` stores authoritative thermal energy in microjoules. Temperature
is a derived milli-kelvin value. Small transfers therefore accumulate instead
of vanishing through integer division.

`ThermalTransfer` caps transfers at equilibrium. Even a very large conductance
or transfer limit cannot make the originally hotter body become colder than the
other body in one solve.

`ThermalEngine` does not inspect dimensions, biomes, rain, neighboring fluids,
or blocks. NeoForge integration will produce a `ThermalEnvironment` later.

## Deliberately deferred

- Scheduler participant adapter.
- NeoForge world/biome/weather environment provider.
- Persistence codecs and client synchronization.
- Machine, generator, and cable attachment.
- Active cooling devices and multi-node thermal networks.

Existing gameplay remains unchanged until a later milestone attaches legacy
block entities to this thermal authority.
