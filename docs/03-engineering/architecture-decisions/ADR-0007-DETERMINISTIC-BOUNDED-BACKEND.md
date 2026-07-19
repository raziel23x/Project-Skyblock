# ADR-0007 — Deterministic and Bounded Backend

## Status

Accepted.

## Context

Unstable iteration and unbounded searches create inconsistent outcomes and server performance risk.

## Decision

Ordering and work limits are explicit throughout the simulation.

## Consequences

Schedulers, routes, transfers, and processing must define deterministic ordering and bounded work.
