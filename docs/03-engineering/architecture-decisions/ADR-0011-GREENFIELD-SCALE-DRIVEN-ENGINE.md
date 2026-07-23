# ADR-0011 — Greenfield, Scale-Driven Engine Construction

- **Status:** Accepted
- **Date:** 2026-07-23

## Context

Project Skyblock originally began as a conventional Minecraft and NeoForge mod. Its blocks,
machines, tools, armor, menus, and utility items were created as prototypes to discover what the
platform could support.

The engine became necessary when development started asking a different class of question:

- What happens with 100 active objects?
- What happens with 1,000 or 10,000?
- How can gameplay represent extremely large quantities without creating one Java object or one
  tick operation for every represented unit?
- Can inactive systems sleep?
- Can equivalent work be aggregated?
- Can runtime cost scale with meaningful activity and change rather than raw object count?

There was no previous Project Skyblock simulation engine to replace. The engine is being designed
and built from first principles as increasingly demanding prototype workloads expose real
requirements.

## Decision

Treat the Project Skyblock simulation engine as a **greenfield, scale-driven engine construction
project**.

Existing gameplay objects are temporary validation fixtures. They may be converted to the engine
to stress scheduler, persistence, resource, networking, adapter, and integration boundaries. Their
conversion does not make them permanent game content, and they must not dictate final progression,
balance, naming, or content design.

The engine should make runtime work scale with meaningful activity and state changes wherever
practical. Large conceptual quantities should normally be represented by compact authoritative
state, aggregation, batching, indexing, snapshots, or scheduled bounded work—not by multiplying
independently ticking Minecraft objects.

## Consequences

- Use **engine construction**, **contract hardening**, and **prototype migration** rather than
  calling the overall effort an engine rewrite.
- A prototype is retained only while it provides unique validation evidence.
- Each prototype migration must identify the engine contracts it stresses and convert discoveries
  into reusable implementation and regression tests.
- Sleeping, event-driven wakeups, bounded work, cached topology, immutable publication, and targeted
  recalculation remain core scaling tools.
- The engine does not claim that billions of independently simulated block entities are practical.
  It exists so gameplay concepts do not require that representation.
- Final Game Era content is designed after the engine contracts are proven; prototype content may
  then be archived or removed.

## Alternatives Considered

1. Continue using ordinary per-block tick logic and optimize individual machines later.
2. Design the entire engine upfront without prototype pressure.
3. Treat prototype machines as the permanent content roadmap.
4. Claim arbitrary object-count scalability without changing the representation model.

These alternatives either defer the real scaling problem, encourage speculative abstractions, or
confuse test fixtures with the final game.
