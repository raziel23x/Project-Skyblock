# ADR-0002 — Simulation Owns Authoritative State

## Status

Accepted.

## Context

Duplicating amounts or lifecycle flags in block entities creates divergence, complicates testing, and makes capabilities accidental owners.

## Decision

Backend state objects are the sole authority for simulation resources and lifecycle state.

## Consequences

Persistence and platform adapters must translate to and from validated backend state. They may cache views but may not maintain competing truth.
