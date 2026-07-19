# Backend Milestone 9 — Machine Inventory Component

## Purpose

Introduce the first backend-owned machine inventory model without coupling authoritative item state to Minecraft `ItemStack`, item handlers, block entities, menus, or recipes.

## Implemented Contracts

### Inventory identity and quantity

- `SimulationItemKey`
- `SimulationItemStack`

`SimulationItemKey` provides a stable `namespace:path` identity suitable for adapters and persistence codecs. `SimulationItemStack` stores an immutable item identity, quantity, and maximum stack size. Empty stacks use a single validated value rather than a Minecraft object.

### Machine slot configuration

- `MachineInventoryAccess`
- `MachineInventorySlotRule`
- `MachineInventorySlotDefinition`

Each slot has an explicit capacity, external access mode, and insertion rule. Slot rules are backend contracts and do not inspect Minecraft registries or tags directly; adapters must translate platform information into simulation decisions.

### Authoritative component and observability

- `MachineInventoryComponent`
- `MachineInventorySlotSnapshot`
- `MachineInventoryDiagnostics`

The component owns slot contents, validates insertion and extraction, distinguishes external operations from internal machine operations, provides immutable snapshots, validates restored state, marks dirty categories, and wakes the owning participant after meaningful changes.

## Operation Model

External adapters use:

- `insert(slot, stack)`
- `extract(slot, quantity)`

Owning machine logic uses:

- `store(slot, stack)`
- `consume(slot, quantity)`

Internal operations intentionally bypass external input/output access while still respecting slot capacity and insertion rules. This mirrors the energy component distinction between network-facing operations and machine-owned production or consumption.

## Invariants

1. An inventory contains at least one slot.
2. Every slot has positive capacity.
3. Quantities and limits are non-negative.
4. A non-empty stack always has an item identity and positive maximum stack size.
5. A slot never exceeds its configured capacity or the stored item's maximum stack size.
6. Different item identities never merge.
7. Restored state must pass the same slot rules and capacity checks as live state.
8. Invalid slot indexes and negative requests fail immediately.
9. Rejected or zero-effect operations do not mark state dirty or wake the owner.
10. Meaningful changes mark persistence, client synchronization, and scheduler dirty flags and signal the owner to wake.

## Persistence Boundary

`MachineInventorySlotSnapshot` and `SimulationItemStack` are immutable, serialization-friendly values. This milestone does not define a Minecraft NBT, registry, or codec implementation. Platform adapters will translate item registry identities and persisted data into validated simulation values.

## Automated Validation

`MachineInventoryComponentTest` covers:

- partial insertion and returned remainder;
- slot capacity enforcement;
- insertion-rule rejection;
- different-item merge rejection;
- external versus internal access behavior;
- dirty-state and wake signaling;
- validated restoration;
- invalid slot and negative-request failures.

## Ownership Boundary

All authoritative inventory state introduced by this milestone lives under the simulation packages. Existing Research Era inventories remain prototypes and are not migrated by this milestone.

Minecraft and NeoForge code may later expose this state through item-handler adapters, but adapters must not duplicate slot contents or bypass component validation.

## Deliberately Deferred

- NeoForge `IItemHandler` adapter;
- Minecraft item registry translation;
- NBT or codec implementation;
- sided slot maps and automation routing;
- inventory-wide insertion strategies;
- transactional multi-slot operations;
- machine composition proof;
- production-machine migration.

## Completion Status

Implementation and automated tests are included. Local Gradle execution must be completed in the normal development environment because the delivery environment could not download the Gradle distribution. The Minecraft-independent production classes were compiled directly with Java 21 as a structural validation step.
