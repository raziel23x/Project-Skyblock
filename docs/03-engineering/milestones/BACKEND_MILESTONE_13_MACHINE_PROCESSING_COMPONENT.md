# Backend Milestone 13 — Machine Processing Component

## Purpose

Add reusable, recipe-independent processing lifecycle state to composed machines without embedding recipes, inventory transactions, energy policy, or Minecraft APIs into the component.

## Implemented

- `MachineProcessingStatus`
- `MachineProcessingComponent`
- `MachineProcessingDiagnostics`
- processing ownership in `MachineComponentState`
- processing diagnostics in composed runtime snapshots
- shared dirty-state and wake ownership through `MachineRuntime`
- automated lifecycle, validation, dirty-state, and wake tests

## Lifecycle

```text
IDLE -> RUNNING -> READY_TO_COMPLETE -> IDLE
             \
              -> BLOCKED -> RUNNING
```

Machine logic starts an operation, advances bounded units, blocks when an external requirement is unavailable, commits outputs, and then explicitly completes the operation.

## Architectural Boundaries

The component does not:

- match recipes;
- consume energy;
- mutate inventories;
- evaluate temperature requirements;
- insert outputs;
- access Minecraft, NeoForge, NBT, menus, or packets.

Those responsibilities remain explicit in machine logic and focused resource components. This avoids turning processing into a hidden monolith.

## TPS Decisions

Ordinary progress marks persistence and client-sync state but does not issue an additional scheduler wake. Running machine logic already returns its next scheduling decision. Lifecycle transitions still mark scheduler state and issue a coalesced wake.

## Additional Architectural Improvement

Processing was integrated directly into the existing composed machine runtime because the PoC migration requires energy, inventory, thermal, and processing state to share one authoritative dirty tracker and wake path.

## Deliberate Deferrals

- recipe registry and matching contracts
- transactional multi-component commits
- fuel abstraction
- persistence codecs
- platform adapters
- final crusher behavior and balancing

These will be added only when the reference vertical slice proves their exact requirements.

## Validation

Automated tests cover operation start, bounded progress, blocking, resuming, completion, restoration validation, transition wake behavior, and avoidance of redundant progress wakes.

Container Gradle execution remained unavailable because the Gradle distribution could not be downloaded. Local `gradlew.bat test` and `gradlew.bat build` remain the authoritative validation steps.
