# Backend Engineering Standards

> Status: **Approved for the current architecture milestone**
>
> This document defines the mandatory backend engineering standards for Project Skyblock.
> It governs gameplay logic, machine systems, persistence boundaries, performance, testing,
> and completion criteria. Frontend presentation must not compensate for weak backend design.

---

## 1. Purpose

Project Skyblock is a backend-first, performance-first NeoForge mod intended for long-lived
modpacks and large automated worlds.

The backend must remain:

- deterministic,
- testable,
- scalable,
- maintainable,
- data-driven where practical,
- minimally coupled to Minecraft internals,
- safe for dedicated servers,
- extensible through stable public contracts.

A feature is not complete merely because it works once in a development world. It must work
predictably at scale and remain understandable to future contributors.

---

## 2. Backend-First Architecture

Gameplay behavior belongs in backend systems.

The following layers must not contain authoritative gameplay logic:

- screens,
- renderers,
- models,
- animations,
- particles,
- sounds,
- client-only classes.

These layers may display or represent state, but they must not determine it.

### Responsibility boundaries

| Layer | Responsibility |
|---|---|
| Block | Placement, removal, interaction entry points, and world-facing behavior |
| Block entity or host object | Runtime ownership and integration with the Minecraft world |
| Controller | Authoritative machine or system behavior |
| Data model | Typed runtime state |
| Scheduler | Decides when work is allowed to execute |
| Persistence adapter | Converts typed state to and from platform persistence |
| Networking adapter | Synchronizes only required state changes |
| GUI | Displays state and submits validated user intent |
| Renderer | Visual representation only |

### Mandatory rule

> Removing every GUI, model, animation, sound, and particle must not break gameplay behavior.

---

## 3. Typed State

Internal gameplay systems must use typed Java objects.

Examples include:

```java
MachineState
ProcessState
MaintenanceState
ComponentState
ResourcePortState
NetworkState
```

Gameplay code must not use generic persistence containers as runtime state.

Avoid:

- raw tag structures,
- stringly typed property maps,
- unvalidated arbitrary objects,
- repeated registry lookups inside hot paths,
- duplicated state owned by multiple systems.

A single authoritative owner must exist for each mutable value.

---

## 4. TPS Performance Standards

TPS performance is a Project Skyblock responsibility.

A feature that produces unnecessary server work is defective even when its gameplay behavior
appears correct.

### Core rule

> No system performs work merely because a server tick occurred.

Tick execution must be justified by active work, a scheduled deadline, or a relevant state change.

### Idle behavior

Idle machines and systems must sleep.

An idle machine must not continually:

- search for recipes,
- scan inventories,
- query neighbors,
- rebuild capabilities,
- inspect transport routes,
- serialize state,
- send packets,
- allocate temporary collections.

Wake conditions may include:

- inventory contents changed,
- fluid contents changed,
- energy or mechanical input changed,
- a neighbor connection changed,
- output capacity changed,
- configuration changed,
- a scheduled process deadline arrived,
- maintenance state changed,
- the machine was loaded and requires validation.

### Bounded work

Every recurring backend operation must have a defined upper bound.

Work queues must:

- limit work per tick,
- carry remaining work forward,
- avoid starving other systems,
- expose queue depth for diagnostics,
- recover safely after chunk unloads or invalid targets.

### World scanning

Unbounded world scanning is prohibited.

Any spatial search must define:

- maximum radius,
- maximum visited positions,
- triggering event,
- cache lifetime,
- invalidation conditions.

Large structures and transport systems should update incrementally after topology changes rather
than repeatedly rediscovering unchanged state.

### Caching

Cache expensive or repeated results when their invalidation conditions are known.

Likely cache targets include:

- matched recipes,
- resolved ports,
- transport endpoints,
- network topology,
- registry-derived immutable values,
- machine configuration,
- capability or service references where platform-safe.

A cache without a defined invalidation strategy must not be introduced.

### Allocations

Hot paths should avoid unnecessary allocation.

Do not create new streams, collections, tags, wrapper objects, or formatted strings during routine
machine execution unless profiling demonstrates that the cost is insignificant.

Readability remains important; object reuse must not create unsafe shared mutable state.

---

## 5. Scheduling

Project Skyblock machine systems must use explicit scheduling rather than universal polling.

The scheduler should distinguish at least:

```java
SLEEPING
READY
SCHEDULED
ACTIVE
BLOCKED
INVALID
```

Suggested behavior:

- **SLEEPING**: no work is scheduled.
- **READY**: a state change requires reevaluation.
- **SCHEDULED**: work is due at a known future tick.
- **ACTIVE**: bounded processing work is occurring.
- **BLOCKED**: progress cannot continue until a relevant external change.
- **INVALID**: machine state requires repair, migration, or safe shutdown.

Blocked machines must subscribe to or be awakened by the condition that can unblock them. They
must not poll every tick to ask whether the condition changed.

---

## 6. Dirty-State Tracking

State changes must be explicit.

A dirty-state tracker should separate at least:

- persistence dirty,
- client synchronization dirty,
- comparator or redstone dirty,
- neighbor notification dirty,
- scheduler dirty,
- visual-state dirty.

Changing one value must not automatically trigger every category.

Examples:

- Progress advanced internally: scheduler dirty only unless the visible value crossed a sync threshold.
- Inventory changed: persistence dirty, scheduler dirty, and possibly client sync dirty.
- Cosmetic animation phase changed: client-side only.
- Machine configuration changed: persistence, sync, scheduling, and possibly neighbor notification.

Dirty flags should be cleared only after the associated work succeeds.

---

## 7. Networking

The server is authoritative.

Clients may request actions but may not decide machine outcomes.

### Network rules

- Do not send machine state every tick.
- Synchronize only changed values.
- Use thresholds for rapidly changing display values where exact per-tick updates are unnecessary.
- Validate every client request on the server.
- Do not trust client inventory, component, energy, temperature, or progress values.
- Keep packets small and purpose-specific.
- Avoid synchronizing state that the client does not display.
- Batch compatible changes when this reduces packet overhead without harming responsiveness.

Animations should derive from compact state such as:

```java
running
startGameTime
processDuration
warningLevel
```

rather than receiving a new animation position every tick.

---

## 8. Persistence and NBT Quarantine

Project Skyblock avoids NBT by default.

### Mandatory rules

- NBT must not be used as an internal gameplay data structure.
- Machine logic must not directly read or write NBT.
- No NBT imports are permitted in ordinary machine, process, maintenance, or transport packages.
- No per-tick serialization.
- No `CompoundTag`, SNBT text, or encoded NBT blob may be hidden inside an opaque runtime field.
- Opaque adapter payloads must use a documented typed/component codec, not NBT-as-a-bag-of-data.
- No persistence work unless state is dirty or the platform explicitly requires it.
- Prefer typed codecs, data components, attachments, registries, and supported typed serializers.

When NeoForge or Minecraft requires an NBT-facing hook, the interaction must be isolated inside a
dedicated persistence adapter.

```text
Minecraft persistence
        ↓
Platform adapter
        ↓
Typed Project Skyblock state
        ↓
Gameplay systems
```

The adapter must:

- own all persistence keys,
- validate loaded data,
- provide defaults,
- handle version migration,
- reject or repair invalid values safely,
- convert immediately into typed state,
- prevent persistence types from escaping into gameplay code.

NBT is a quarantined platform detail, not a Project Skyblock architecture. Decode it once at the host boundary, validate it into typed state, and discard it.

---

## 9. Data-Driven Content

Java provides reusable behavior. Data defines content where practical.

Data-driven definitions may include:

- recipes,
- process durations,
- inputs and outputs,
- component requirements,
- wear values,
- temperatures,
- pressures,
- machine limits,
- maintenance consumables,
- inspection descriptions,
- generator feedstocks,
- progression requirements.

Data must be validated during load.

Invalid data should produce clear diagnostics identifying:

- resource location,
- field,
- invalid value,
- expected constraint.

Invalid definitions must not create uncontrolled tick errors or silently corrupt worlds.

---

## 10. Automated Testing

Manual factory construction is not an acceptable primary testing method.

Testing must be automated at multiple layers.

### Unit tests

Pure backend systems should be testable without starting a client.

Unit tests should cover:

- state transitions,
- recipe matching,
- process calculations,
- wear calculations,
- resource accounting,
- scheduler decisions,
- dirty-state behavior,
- migration logic,
- validation failures.

### GameTests

GameTests should cover Minecraft integration, including:

- placement and removal,
- chunk unload and reload,
- persistence,
- capability or service exposure,
- neighbor changes,
- inventory insertion and extraction,
- blocked outputs,
- redstone or control behavior,
- network formation and separation,
- failure recovery.

### Generated stress scenarios

Development commands or test utilities must generate test layouts automatically.

Examples:

```text
/ps dev spawn projectskyblock:crusher 1000
/ps dev benchmark projectskyblock:crusher idle
/ps dev benchmark projectskyblock:crusher active
/ps dev benchmark network mixed 5000
/ps dev cleanup
```

These tools should:

- place deterministic layouts,
- configure machines,
- insert test inputs,
- start or block processes,
- collect timing metrics,
- report results,
- remove the generated test area.

They must be unavailable to ordinary survival players unless explicitly enabled.

---

## 11. Profiling and Diagnostics

Performance claims require measurements.

Development diagnostics should expose at least:

```text
Loaded machines
Sleeping machines
Ready machines
Active machines
Blocked machines
Scheduled operations
Work queue depth
Machine processing time
Transport processing time
Maintenance processing time
Packets sent
Persistence writes
Topology rebuilds
```

Metrics should be aggregated and inexpensive. Diagnostic tooling must not become a significant
source of TPS loss.

When a performance issue is reported:

1. Reproduce when possible.
2. Request profiler evidence.
3. Identify Project Skyblock's measured contribution.
4. Fix Project Skyblock if it is responsible.
5. Do not accept blame or reject blame without evidence.

---

## 12. Scale Requirements

Every machine system must be designed with large factories in mind.

Required test classes:

- one active instance,
- one hundred mixed instances,
- one thousand idle instances,
- one thousand active or scheduled instances where practical,
- large connected transport networks,
- many separate small networks,
- repeated chunk loading and unloading,
- blocked outputs and full buffers,
- malformed or migrated saved state.

Exact performance budgets should be established from measured baselines on the project's reference
development hardware and dedicated-server test environment.

Until those baselines exist, no arbitrary millisecond promise should be documented.

---

## 13. Failure Safety

Performance protection takes priority over endlessly retrying invalid work.

A backend system must fail safely when it encounters:

- invalid data,
- missing registry entries,
- removed optional integrations,
- corrupted state,
- impossible network topology,
- unloaded destinations,
- exceeded work limits.

Safe outcomes include:

- sleeping,
- entering a blocked state,
- disabling the affected process,
- logging a rate-limited diagnostic,
- preserving recoverable player data.

Repeated exceptions or repeated log spam every tick are prohibited.

---

## 14. Public Extension Points

Stable extension points should use typed contracts.

Preferred extension mechanisms:

- datapacks,
- KubeJS events and builders,
- registries,
- tags,
- NeoForge events,
- documented Java APIs,
- optional compatibility adapters.

Extensions must not require consumers to modify internal machine classes.

Public APIs should avoid exposing:

- implementation-specific collections,
- raw NBT,
- mutable internal state,
- client-only types,
- unstable Minecraft internals where a project-owned abstraction is practical.

---

## 15. Reference Backend Contracts

The first backend milestone is limited to:

```java
MachineState
MachineController
MachineScheduler
DirtyStateTracker
```

These contracts must be proven before expanding into the full machine feature set.

### MachineState

Owns typed authoritative state required by the controller.

### MachineController

Evaluates state changes and performs bounded gameplay work.

### MachineScheduler

Wakes, schedules, blocks, sleeps, and removes controllers without requiring every machine to poll
every tick.

### DirtyStateTracker

Records which external actions are required after state changes.

This milestone does not include:

- custom GUI,
- custom renderer,
- animations,
- sound design,
- transport implementation,
- maintenance implementation,
- component implementation,
- complete crusher implementation.

Those systems may influence API design only when a concrete incompatibility is identified.

---

## 16. Completion Criteria

A backend feature is complete for its current milestone only when:

- [ ] Its purpose and ownership are documented.
- [ ] Authoritative state is typed.
- [ ] Gameplay logic is server-side.
- [ ] Idle behavior sleeps or performs negligible work.
- [ ] Recurring work is bounded.
- [ ] Cache invalidation is defined.
- [ ] Persistence boundaries are isolated.
- [ ] NBT does not appear in gameplay code.
- [ ] Client synchronization is change-driven.
- [ ] Unit tests cover pure logic.
- [ ] GameTests cover required integration behavior.
- [ ] Stress layouts can be generated automatically.
- [ ] Profiling data has been collected.
- [ ] Failure behavior is safe and rate-limited.
- [ ] Public extension points are documented where applicable.
- [ ] No known blocker prevents the next reference implementation.

### Definition of finished

> Finished means the feature satisfies the approved milestone and supports the next planned layer.
> It does not mean every imaginable future capability has already been implemented.

Once these criteria are met, the milestone is frozen. Changes require a demonstrated defect,
measured performance problem, platform compatibility issue, or approved architecture decision.

---

## 17. Non-Goals for This Milestone

The following are intentionally deferred:

- visual polish,
- final GUI layouts,
- animated machine models,
- detailed sound systems,
- final balance values,
- every possible machine type,
- speculative abstractions without a current consumer,
- optimization of startup-only code without profiling evidence,
- exact performance guarantees before benchmark baselines exist.

---

## 18. Governing Principle

> Project Skyblock should never make the server perform unnecessary work.

Performance is not a cleanup phase. It is part of the feature design.

Backend correctness comes first.
Scalability comes with correctness.
Frontend polish comes after both.
