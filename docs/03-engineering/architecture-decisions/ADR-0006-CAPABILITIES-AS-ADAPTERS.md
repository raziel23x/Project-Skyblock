# ADR-0006 — Capabilities Are Compatibility Layers

## Status

Accepted.

## Context

Capability-owned storage would couple authority to the platform and undermine deterministic simulation.

## Decision

NeoForge capabilities expose backend resources but do not own them.

## Consequences

Adapters must enforce backend access and throughput rules and cannot bypass component invariants.
