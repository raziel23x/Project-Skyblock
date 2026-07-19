# Machine Framework Architecture

> Status: **Draft for architecture review**
>
> This document defines the first Project Skyblock machine-backend contracts:
>
> - `MachineState`
> - `MachineController`
> - `MachineScheduler`
> - `DirtyStateTracker`
>
> It defines responsibilities and relationships only. It does not commit the project to a final
> implementation before the reference machine proves the design.

---

## 1. Scope

The first machine-framework milestone provides a reusable backend foundation for machines without
introducing machine-specific behavior.

This milestone must support:

- authoritative typed state,
- bounded backend execution,
- sleeping idle machines,
- explicit wake conditions,
- isolated persistence,
- change-driven synchronization,
- deterministic testing.

This milestone does not include:

- crusher-specific processing,
- transport networks,
- maintenance,
- machine components,
- custom screens,
- rendering,
- animation,
- sound,
- final persistence adapters.

---

## 2. Design Principles

The framework follows these rules:

1. State is typed and authoritative.
2. Controllers contain gameplay behavior.
3. Schedulers decide when controllers execute.
4. Dirty tracking records required external work.
5. Minecraft integration remains at the boundary.
6. Idle machines do not poll.
7. Recurring work is bounded.
8. Public contracts remain small.
9. Machine-specific logic does not leak into shared infrastructure.
10. No raw NBT enters gameplay packages.

---

## 3. High-Level Relationship

```text
Minecraft / NeoForge host
        |
        v
MachineController
        |
        +------ reads and mutates ------> MachineState
        |
        +------ requests scheduling ----> MachineScheduler
        |
        +------ records side effects ---> DirtyStateTracker
```

The host object owns integration with the world.

The controller owns machine behavior.

The state owns machine data.

The scheduler owns execution timing.

The dirty tracker owns deferred external actions.

---

# 4. MachineState

## Purpose

`MachineState` is the authoritative typed runtime state for one machine instance.

It contains data.

It does not perform world access, scheduling, networking, serialization, rendering, or recipe
discovery by itself.

## Responsibilities

`MachineState` may own:

- current lifecycle state,
- active process state,
- progress,
- configuration,
- cached identifiers,
- validated machine-owned resource state,
- timestamps required for deterministic scheduling,
- failure or blocked reason,
- state version where required.

## Non-responsibilities

`MachineState` must not:

- access a level,
- scan neighbors,
- send packets,
- mark a block entity changed,
- serialize itself directly to NBT,
- perform recipe searches,
- call scheduler methods,
- contain client-only state,
- contain arbitrary untyped maps.

## Initial contract

The first implementation should begin as a marker or minimal interface:

```java
public interface MachineState {
}
```

Machine families may provide typed implementations:

```java
public final class ProcessingMachineState implements MachineState {
    // Typed fields only.
}
```

Do not add getters or setters to the root interface until multiple implementations prove a shared
requirement.

## Ownership

One controller instance owns one state instance.

The host may hold both, but must not independently mutate controller-owned state.

Persistence adapters may construct or restore state, but ordinary gameplay code remains the only
runtime mutator.

## Invariants

A state implementation must always be valid after public construction or restoration.

Invalid persistence input must be normalized before the controller receives it.

Examples:

- progress cannot be negative,
- duration cannot be zero for an active process,
- sleeping state cannot have immediate scheduled work,
- invalid registry identifiers cannot remain unresolved indefinitely,
- mutually exclusive lifecycle flags cannot coexist.

---

# 5. MachineController

## Purpose

`MachineController` contains authoritative machine behavior.

It evaluates events, mutates state, requests future execution, and records required external work.

## Responsibilities

A controller may:

- handle machine creation and load validation,
- respond to inventory or resource changes,
- start, advance, block, complete, or cancel processes,
- determine whether future execution is needed,
- request scheduler changes,
- mark dirty categories,
- expose read-only snapshots or views for integration,
- handle bounded scheduled work.

## Non-responsibilities

A controller must not:

- render,
- construct screens,
- own block models,
- send raw packets,
- write raw persistence data,
- directly tick forever,
- scan unbounded world regions,
- mutate unrelated machines,
- trust client-provided state,
- depend on a concrete block entity when an integration context is sufficient.

## Proposed contract

```java
public interface MachineController<S extends MachineState> {

    S state();

    void onLoaded(MachineContext context);

    void onUnloaded(MachineContext context);

    void onChanged(MachineContext context, MachineChange change);

    MachineExecutionResult execute(
        MachineContext context,
        MachineExecutionBudget budget
    );
}
```

This is a design target, not a frozen API.

The reference implementation may prove that some methods should be renamed, combined, or removed.

## MachineContext

The controller needs a narrow integration boundary instead of direct access to every Minecraft
object.

A future `MachineContext` may expose only required services:

```java
public interface MachineContext {

    long gameTime();

    MachinePosition position();

    MachineScheduler scheduler();

    DirtyStateTracker dirtyState();

    MachineServices services();
}
```

The context should not become a generic service locator.

Every exposed dependency must have a demonstrated consumer.

## MachineChange

External changes should be expressed as typed events.

Possible examples:

```java
public sealed interface MachineChange {
    record InventoryChanged(ResourcePortId port) implements MachineChange {}
    record NeighborChanged(Direction direction) implements MachineChange {}
    record ConfigurationChanged() implements MachineChange {}
    record OutputAvailabilityChanged() implements MachineChange {}
    record ScheduledWake() implements MachineChange {}
}
```

Do not create a large event hierarchy before the reference machine requires it.

A small enum may be preferable initially.

## Execution result

Scheduled execution should return an explicit result.

Possible shape:

```java
public sealed interface MachineExecutionResult {

    record Sleep() implements MachineExecutionResult {}

    record ScheduleAt(long gameTime) implements MachineExecutionResult {}

    record ScheduleAfter(long ticks) implements MachineExecutionResult {}

    record ContinueNextTick() implements MachineExecutionResult {}

    record Blocked(MachineBlockReason reason) implements MachineExecutionResult {}
}
```

The controller must not silently remain active.

Every execution returns the next scheduling decision.

## Execution budget

Controllers must receive a bounded work budget where a task could expand.

Possible shape:

```java
public interface MachineExecutionBudget {

    boolean tryConsume(int units);

    int remainingUnits();
}
```

Simple machines may consume one unit per execution.

Complex systems must stop when the budget is exhausted and schedule continuation.

---

# 6. MachineScheduler

## Purpose

`MachineScheduler` decides when machine controllers execute.

It prevents universal per-tick polling and centralizes bounded machine work.

## Responsibilities

The scheduler must:

- register loaded machines,
- unregister unloaded machines,
- wake sleeping machines,
- schedule machines for a future game time,
- deduplicate repeated scheduling requests,
- process bounded work per server tick,
- carry excess work forward,
- safely ignore stale or unloaded entries,
- expose inexpensive diagnostics.

## Non-responsibilities

The scheduler must not:

- contain machine-specific recipes,
- mutate machine state directly,
- own persistence,
- inspect inventories,
- perform transport routing,
- send client packets,
- keep chunks loaded,
- execute unloaded machines.

## Scheduler states

The framework should support these conceptual states:

```text
UNREGISTERED
SLEEPING
READY
SCHEDULED
EXECUTING
BLOCKED
INVALID
```

These may not all require a stored enum.

The implementation should store only what is necessary.

## Proposed contract

```java
public interface MachineScheduler {

    void register(MachineHandle machine);

    void unregister(MachineHandle machine);

    void wake(MachineHandle machine);

    void scheduleAt(MachineHandle machine, long gameTime);

    void cancel(MachineHandle machine);

    SchedulerMetrics metrics();
}
```

## MachineHandle

The scheduler should avoid depending on a concrete block entity.

A lightweight handle may contain:

```java
public interface MachineHandle {

    MachineId id();

    boolean isLoaded();

    MachineController<?> controller();

    MachineContext context();
}
```

The final design must avoid repeatedly allocating handles during routine execution.

## Queue behavior

The scheduler should use two conceptual queues:

1. **Ready queue** for immediate bounded work.
2. **Timed queue** for future wakeups.

Required properties:

- duplicate ready entries are prevented,
- replacing a timed wake with an earlier wake is supported,
- stale timed entries are detectable,
- unloaded machines are discarded safely,
- one failing controller does not stop the entire queue,
- failures are rate-limited and observable.

## Tick budget

The scheduler must have a configurable or internally defined work limit per server tick.

The first implementation should measure:

- number of executions,
- total machine time,
- ready queue depth,
- timed queue depth,
- deferred work count,
- failed execution count.

Do not promise an exact millisecond limit before profiling establishes a baseline.

## Chunk lifecycle

On chunk unload:

- the machine is unregistered,
- pending ready work is canceled,
- timed entries become stale or are removed,
- no strong reference may prevent unloading.

On chunk load:

- state is restored,
- the controller validates state,
- scheduling is reconstructed from typed state,
- invalid overdue timestamps are normalized,
- the machine sleeps unless work is actually required.

---

# 7. DirtyStateTracker

## Purpose

`DirtyStateTracker` records which external actions are required after state changes.

It separates state mutation from platform side effects.

## Dirty categories

Initial categories:

```java
public enum DirtyFlag {
    PERSISTENCE,
    CLIENT_SYNC,
    SCHEDULER,
    NEIGHBOR_NOTIFICATION,
    COMPARATOR,
    VISUAL_STATE
}
```

Only proven categories should remain.

## Responsibilities

The tracker must:

- mark dirty categories,
- query whether a category is dirty,
- clear a category after successful handling,
- clear multiple handled categories efficiently,
- expose a compact snapshot for integration,
- avoid allocating in common mutation paths.

## Non-responsibilities

The tracker must not:

- serialize data,
- send packets,
- notify neighbors,
- schedule controllers directly,
- determine whether a value changed,
- clear flags before external work succeeds.

## Proposed contract

```java
public interface DirtyStateTracker {

    void mark(DirtyFlag flag);

    void markAll(DirtyFlag... flags);

    boolean isDirty(DirtyFlag flag);

    int snapshot();

    void clear(DirtyFlag flag);

    void clearMask(int handledMask);
}
```

A bit mask is likely appropriate because the category set is small and fixed.

The public API should not expose implementation details unless this improves integration without
coupling callers.

## Mutation pattern

```java
if (state.setProgress(newProgress)) {
    dirty.mark(DirtyFlag.PERSISTENCE);

    if (visibleProgressChanged(oldProgress, newProgress)) {
        dirty.mark(DirtyFlag.CLIENT_SYNC);
    }
}
```

State mutation methods should report whether a value actually changed.

Unchanged assignments must not create dirty work.

---

# 8. Lifecycle

## Placement

1. Platform host is created.
2. Typed state receives defaults.
3. Controller is created.
4. Scheduler registers the machine after the world is available.
5. Controller validates initial conditions.
6. Controller sleeps or requests work.

## Load

1. Persistence adapter reads platform data.
2. Adapter validates and migrates it.
3. Adapter constructs typed state.
4. Controller is created.
5. Controller receives `onLoaded`.
6. Scheduler registration is reconstructed.
7. Overdue scheduled work is handled safely.

## External change

1. Host or service detects a relevant change.
2. A typed `MachineChange` is sent to the controller.
3. Controller mutates typed state if required.
4. Controller marks dirty categories.
5. Controller requests a wake or future schedule.
6. Host flushes platform side effects at a safe point.

## Scheduled execution

1. Scheduler selects a ready machine.
2. Scheduler verifies that it remains loaded and valid.
3. Scheduler gives the controller a bounded budget.
4. Controller performs bounded work.
5. Controller returns the next scheduling decision.
6. Scheduler applies that decision.
7. Dirty platform work is flushed separately.

## Unload

1. Controller receives `onUnloaded` if required.
2. Scheduler unregisters the machine.
3. Dirty persistent state is flushed according to platform lifecycle requirements.
4. References that could retain the chunk are released.

## Removal

1. Machine processing stops.
2. Scheduler entries are canceled.
3. Owned resources are dropped, transferred, or discarded according to machine rules.
4. State is no longer eligible for persistence.
5. Integration references are released.

---

# 9. Threading Assumptions

Initial machine gameplay execution occurs on the logical server thread.

The first implementation must not introduce asynchronous mutation of machine state.

Async work may be considered later only for pure calculations that:

- do not access the world,
- do not access mutable registries,
- do not mutate machine state,
- return results that are validated again on the server thread,
- demonstrate a measured benefit.

No machine API should imply thread safety unless it is intentionally provided and tested.

---

# 10. Testing Requirements

## MachineState tests

Test:

- valid construction,
- invariant enforcement,
- equality or snapshots where relevant,
- invalid restoration normalization,
- state-change detection.

## MachineController tests

Test:

- load into sleeping state,
- wake after relevant change,
- irrelevant change does not wake,
- active processing,
- blocked processing,
- completion,
- cancellation,
- bounded work,
- deterministic scheduling result.

## MachineScheduler tests

Test:

- registration,
- unregistration,
- duplicate wake prevention,
- earlier rescheduling,
- stale timed entries,
- unloaded handles,
- budget exhaustion,
- deferred work,
- controller exception isolation,
- deterministic queue order where promised.

## DirtyStateTracker tests

Test:

- individual flags,
- combined flags,
- duplicate marks,
- selective clearing,
- snapshot behavior,
- unchanged state creates no dirty flags.

## Integration tests

GameTests should prove:

- a sleeping machine causes no repeated controller execution,
- inventory insertion wakes the machine,
- blocked output does not poll,
- freeing output wakes the machine,
- chunk unload removes scheduler work,
- chunk reload reconstructs scheduling,
- client sync occurs only after relevant changes.

---

# 11. Package Boundaries

Suggested package structure:

```text
dev.projectskyblock.machine.api
dev.projectskyblock.machine.state
dev.projectskyblock.machine.controller
dev.projectskyblock.machine.scheduler
dev.projectskyblock.machine.dirty
dev.projectskyblock.machine.platform
dev.projectskyblock.machine.test
```

Minecraft- and NeoForge-specific types should be restricted to `platform` and integration packages
where practical.

Shared API packages must not import:

```text
net.minecraft.nbt.*
net.minecraft.client.*
```

Avoid adding a public `impl` package to the supported API surface.

---

# 12. Architecture Decisions Still Open

These decisions must be proven by the reference implementation:

- whether `MachineState` is an interface or an abstract base type,
- whether controllers receive context per method or at construction,
- whether immediate changes and scheduled wakes share one event type,
- whether the scheduler is per level, per server, or internally partitioned per level,
- whether dirty flags use an enum set or integer mask internally,
- whether machine handles are stable objects or compact identifiers,
- how scheduler work budgets are configured,
- how diagnostics are exposed in production builds.

These are deliberately open.

Do not resolve them through speculation alone.

---

# 13. Architecture Freeze Condition

This design becomes approved for implementation when:

- responsibilities have no unresolved overlap,
- ownership is explicit,
- no gameplay code requires raw persistence types,
- sleeping behavior is demonstrable,
- scheduler work is bounded,
- chunk lifecycle is defined,
- tests can be written without custom GUI code,
- the design can support one simple reference processing machine.

Once approved, implement the smallest viable contracts and test them before introducing the
crusher feature.
