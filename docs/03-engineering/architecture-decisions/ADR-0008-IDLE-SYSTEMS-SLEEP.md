# ADR-0008 — Idle Systems Sleep

## Status

Accepted.

## Context

Polling is not accepted as a default execution model.

## Decision

Participants with no possible work remain asleep or blocked until a relevant change occurs.

## Consequences

Systems must emit wake signals when state, topology, resources, configuration, or scheduled time could permit progress.
