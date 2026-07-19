# Machine Component Catalog

## Component Contract

A reusable machine component should:

- own or delegate authoritative backend state;
- enforce component-local invariants and access rules;
- remain independent of Minecraft and NeoForge;
- expose immutable diagnostics;
- mark the correct dirty categories after meaningful change;
- wake the owning participant when new work may be possible;
- avoid knowledge of menus, rendering, packets, and recipes.

## MachineEnergyComponent

**Status:** Implemented in Milestone 8.

Owns a `SimulationEnergyState` relationship and controls external receive/extract and internal produce/consume operations. It enforces capacity, throughput, and access mode. Changes mark persistence, client sync, and scheduler dirty state and signal the owner to wake.

## MachineInventoryComponent

**Status:** Implemented in Milestone 9.

Owns authoritative Minecraft-independent slot contents using `SimulationItemKey` and `SimulationItemStack`. Each slot has explicit capacity, external access, and an insertion rule. External `insert`/`extract` operations are separated from internal `store`/`consume` operations. The component validates restored state, exposes immutable slot snapshots and diagnostics, marks persistence/client-sync/scheduler dirty state, and wakes its owner after meaningful changes. Minecraft `ItemStack` and NeoForge item handlers remain adapter concerns.

## MachineFluidComponent

**Status:** Planned.

Expected to support capacity, amount, flow limits, temperature, and optional quality or mixture metadata where gameplay requires it. Minecraft fluid capabilities will be adapters.

## MachineGasComponent

**Status:** Planned.

Expected to support amount, volume, pressure, temperature, flow, and containment. The exact abstraction will be proven by a concrete science or industrial milestone.

## MachineThermalComponent

**Status:** Planned.

Will connect machine-owned thermal state and operating behavior to the thermal engine without placing temperature logic inside a block entity.

## MachineProcessingComponent

**Status:** Planned.

Will coordinate bounded process state, progress, requirements, outputs, blocking reasons, and component interactions. It will not hard-code all recipes or production content into the engine.
