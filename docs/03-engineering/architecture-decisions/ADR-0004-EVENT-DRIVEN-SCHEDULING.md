# ADR-0004 — Event-Driven Scheduling

## Status

Accepted.

## Context

Permanent tick loops waste work on idle machines and networks.

## Decision

Simulation participants wake after meaningful changes or when scheduled work becomes due.

## Consequences

Every system must define wake conditions, blocked behavior, and future scheduling explicitly.
