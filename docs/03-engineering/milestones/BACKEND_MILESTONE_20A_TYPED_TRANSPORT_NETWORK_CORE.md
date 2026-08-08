# Backend Milestone 20A — Typed Transport Network Core

## Status

**Accepted post-baseline milestone.** Implementation and automated coverage passed the complete
Windows Gradle suite plus standalone and locked-integration runtime validation. The immutable M17
baseline tag is not moved by this acceptance.

## Purpose

Replace the premature assumption that transport progression must be hard-coded as Mk 1, Mk 2, and
Mk 3 cable classes with a reusable Minecraft-independent network foundation. The engine owns physical
topology, typed channel throughput, deterministic route planning, shared-edge reservation, fairness,
and scheduler sleep/wake behavior. Gameplay data may later name and balance transport profiles in any
form without changing the engine.

Milestone 20A does not migrate the live Basic Energy Cable. It proves the backend contracts that the
energy migration will consume in Milestones 20B and 20C.

## Audit Finding

The existing energy-only topology and live cable prototype encode transfer limits directly in an
energy connection. The live cable still scans and transfers through NeoForge capabilities and cannot
prove independent item, fluid, gas, or future-channel speeds. A simple Mk hierarchy would freeze
content and balance decisions before the engine knows which transport forms the Game Era needs.

The shared parts are physical connectivity, deterministic routing, per-edge contention, fair planning,
and sleeping. Resource mutation, loss, endpoint transactions, batching, and validation remain typed
channel-policy responsibilities.

## Implemented

### Typed physical topology

- Added stable transport node, channel, profile, and request identifiers.
- Added immutable transport profiles containing independent native-unit throughput for any registered
  channel.
- Added one physical connection graph filtered by channel support during route discovery.
- Preserved deterministic minimum-hop routing with stable node-id tie breaking.
- Added physical and channel-filtered connected-component queries.
- Rejected conflicting parallel connection profiles and cyclic route values.

### Shared-edge reservation

- Added per-step reservations scoped to exactly one channel.
- Each route atomically reserves the same native-unit amount across every traversed edge.
- Competing routes consume one shared edge budget rather than each receiving the full nominal speed.
- Energy reservations do not consume item, fluid, gas, or other channel budgets.
- Topology mutation invalidates an existing reservation set before further reads or writes.
- Invalid route validation occurs before reservation state changes.

### Deterministic fairness and planning

- Added immutable source-to-target dispatch requests and exact request-level decisions.
- Requests sort by stable identifier, then rotate first access by a monotonic fairness sequence.
- Stable lexical ordering therefore cannot permanently starve later requests across steps.
- Plans distinguish fully planned, partial, capacity-deferred, unroutable, and zero-unit requests.
- Every requested native unit is accounted as planned, deferred, or unroutable.
- Planning mutates no source, target, Minecraft capability, inventory, tank, or gas container.

### Scheduler sleep and diagnostics

- Added one generic scheduled participant for a typed transport topology.
- Progressing work continues on the next simulation step.
- Pending no-progress work receives one unchanged-fingerprint confirmation, then sleeps instead of
  polling forever.
- Endpoint or topology events request work and wake the scheduler for reevaluation.
- Runtime diagnostics preserve per-channel results and never add incompatible units together.

## Deliberate Boundaries

- The engine defines no Mk 1, Mk 2, Mk 3, copper, reinforced, universal, or other gameplay tier.
- Profiles are data-facing throughput definitions, not product names or progression commitments.
- Cross-channel physical-budget conversion is deferred. FE, item count, fluid volume, and gas amount
  are not mathematically interchangeable without concrete gameplay policy.
- Energy loss and source/receiver mutation are deferred to the energy channel policy in Milestone 20B.
- Item transaction semantics wait for a real item-network consumer.
- Fluid and gas channel policies wait for authoritative `MachineFluidComponent` and
  `MachineGasComponent` contracts.
- The live Basic Energy Cable, Structural Energy Frame, machines, capabilities, recipes, assets,
  menus, and saves remain unchanged.

## Automated Coverage

- Independent FE, item, fluid, and gas throughput in one profile.
- Channel-filtered route availability and deterministic equal-hop selection.
- Shared-edge contention and independent per-channel budgets.
- Atomic invalid-plan rejection and stale-topology reservation failure.
- Multi-request partial planning and exact unit accounting.
- Cross-step fairness rotation preventing stable-id starvation.
- Unroutable versus capacity-deferred diagnostics.
- Progress-to-quiescence scheduling, stable blocked sleeping, explicit wake, and bounded execution.
- Per-channel diagnostics that do not aggregate incompatible units.

## Gameplay, Save, API, and Dependency Impact

- **Gameplay:** none. No transport profile is registered as final content and no cable behavior changes.
- **Save:** none. No persistence schema changes.
- **API:** internal engine contract; no stable public API is frozen by milestone acceptance.
- **Dependencies:** none.
- **Performance:** topology remains event-driven; planning is bounded by supplied requests and route
  size; stable blocked networks sleep.

## Acceptance Evidence

Milestone 20A passed the complete Windows Gradle test/build gate and both isolated runtime profiles.
The standalone profile proved no accidental optional dependency, while the locked integration profile
proved helper-mod isolation and regression behavior. Existing fixture behavior remained unchanged.

With M20A accepted, `DOC-SPRING-001` is the current prerequisite. After it closes, M20B1 begins with a
guarded audit and then proves transactional energy endpoints over this transport core. M20C may
migrate the Basic Energy Cable only after the required energy policy is proven.
