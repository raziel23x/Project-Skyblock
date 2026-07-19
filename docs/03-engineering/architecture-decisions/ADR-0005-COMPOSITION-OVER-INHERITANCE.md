# ADR-0005 — Composition Over Inheritance

## Status

Accepted.

## Context

Energy, inventory, fluids, gases, thermal state, and processing vary independently.

## Decision

Machines are assembled from reusable focused components rather than deep machine class hierarchies.

## Consequences

Components require narrow contracts and explicit coordination. Shared behavior is added only when multiple real machines prove it.
