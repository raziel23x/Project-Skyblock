# Backend Milestone 11 — Scheduler Wake Coalescing

## Purpose

Protect the event-driven scheduler from duplicate execution and stale ready-queue state when a participant signals a wake while it is already executing.

This milestone was added after reviewing how machine energy, inventory, and thermal components signal their owner after meaningful state changes. Those signals can occur inside machine execution as well as from external adapters. The scheduler therefore needs an explicit in-flight wake contract before machine composition is proven.

## Problem Found

A wake request received while a participant was `ACTIVE` could place that same participant into the ready queue during its current execution. The participant's returned result could then change its lifecycle to sleeping, blocked, or scheduled while a stale ready entry remained queued.

That created two risks:

- accidental same-tick reevaluation;
- a stale `queuedReady` marker preventing later legitimate wake requests from being enqueued.

Both outcomes conflict with deterministic bounded execution and low server overhead.

## Implementation

`SimulationScheduler` now:

- coalesces repeated wake requests;
- records wake requests received during active execution;
- never requeues an active participant immediately;
- applies the participant's execution result first;
- schedules one reevaluation for the following game tick when an in-flight wake was recorded;
- preserves invalid participants as terminal until explicitly replaced;
- clears deferred wake state when a participant is removed.

## Performance Contract

- One participant executes at most once per scheduler tick.
- Multiple wake requests before execution collapse into one ready entry.
- Multiple wake requests during execution collapse into one next-tick reevaluation.
- Sleeping and blocked participants still perform no recurring polling.
- Stale scheduled tickets remain invalidated through generation checks.

## Automated Validation

`SimulationSchedulerTest` covers:

- sleeping participants do not recur;
- repeated wake requests are coalesced;
- wakes during execution are deferred until the next tick;
- a deferred wake correctly overrides a longer returned schedule for prompt reevaluation.

## Deliberate Deferrals

This milestone does not add machine processing, composition, persistence codecs, or Minecraft adapters. It strengthens the scheduler contract required by those systems.

## Result

The scheduler is now safe for component-driven wake signals during composed machine execution without duplicate same-tick work or lost future wakeups.
