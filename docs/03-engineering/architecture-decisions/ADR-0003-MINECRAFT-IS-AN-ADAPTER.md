# ADR-0003 — Minecraft Is an Adapter

## Status

Accepted.

## Context

Direct world coupling makes backend logic difficult to test and encourages per-tick polling.

## Decision

Minecraft and NeoForge objects host, persist, expose, and present the simulation but do not define its behavior.

## Consequences

Simulation packages cannot import Minecraft or NeoForge. Integration code must translate events and data across the boundary.
