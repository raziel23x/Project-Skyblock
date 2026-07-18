# Logistics System

## Purpose

Logistics moves items, fluids, energy, entities, and information through the civilization. It should begin with physical handling and grow into organized networks.

## Progression

### Manual Logistics

- Player inventory
- Chests
- Barrels
- Buckets
- Minecarts
- Boats where applicable

### Mechanical Logistics

- Hoppers
- Chutes
- Conveyors
- Pipes
- Pumps
- Item sorting

### Networked Logistics

- Routed inventories
- Request systems
- Bulk storage
- Cross-dimensional transfer
- Monitoring

## Core Design Rules

- Vanilla storage and redstone remain useful.
- Project logistics should solve scale and organization, not invalidate chest rooms immediately.
- Throughput, distance, filtering, and power should create meaningful differences.
- Cross-dimensional logistics should be late and infrastructure-heavy.

## Item Logistics

Capabilities may include:

- Directional transfer
- Filtering
- Priority
- Round-robin distribution
- Batch handling
- Overflow routing

## Fluid Logistics

Capabilities may include:

- Pumping
- Pressure or head requirements if used
- Filtering
- Metering
- Temperature compatibility
- Safe handling

## Information Logistics

Control systems may transmit:

- Machine state
- Resource levels
- Requests
- Redstone-like signals
- Safety shutdowns

## Vanilla Integration

Hoppers, minecarts, droppers, dispensers, water streams, and redstone remain valid. Modded storage networks may integrate through optional compatibility.

## Restrictions

- Avoid unlimited transfer with no infrastructure.
- Avoid making physical layout irrelevant too early.
- Avoid forcing every player into a single storage mod.
- Network failure should be diagnosable.

## Open Questions

- Does the base project include digital storage or only integration hooks?
- How are chunk boundaries handled?
- How are unloaded systems simulated?
- What is the intended performance budget for large networks?

---

## Feature Review Checklist

Before implementation, confirm that the proposed feature:

- complements vanilla Minecraft rather than disabling it;
- has an understandable cause and effect;
- introduces knowledge before automation;
- supports the journey from survival to civilization;
- avoids unnecessary grind and arbitrary recipe chains;
- can be expressed through data where practical;
- has a clear place in progression;
- defines failure, recovery, and renewable paths;
- does not accidentally invalidate several other systems.

## Status

Design specification. Exact values, recipes, machine costs, and timings remain subject to playtesting.
