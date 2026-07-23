# Engine Guiding Principles

These principles govern all Engine Era work.

## 1. Simulation Owns Authoritative State

Energy, temperature, machine state, inventories, fluids, gases, processing state, and future simulation resources belong to backend state objects. Minecraft objects host and expose that state but do not redefine it.

## 2. Minecraft Is an Adapter

Block entities, capabilities, menus, packets, and rendering connect the simulation to Minecraft. They are integration layers, not the source of gameplay truth.

## 3. Greenfield Engine First, Final Gameplay Later

The simulation engine is being created from first principles, not rewritten from an earlier engine.
Research Era objects may be converted as prototype stress fixtures, but they do not dictate the
production architecture or become permanent content merely because they were migrated.

## 4. Composition Over Inheritance

Machines should be assembled from focused components such as energy, inventory, fluid, gas, thermal, and processing components. Large inheritance trees are avoided.

## 5. Determinism Over Convenience

Equivalent inputs and state must produce equivalent results. Ordering, budgets, transfers, and scheduling must not depend on accidental collection or world traversal order.

## 6. Idle Systems Sleep

No system performs recurring work solely because a server tick occurred. Participants wake because state changed, scheduled work became due, or an external event made progress possible.

## 7. Work Is Bounded

Scheduler executions, searches, transfers, and processing steps must have explicit limits. Large systems must degrade predictably rather than causing unbounded tick work.

Where practical, runtime cost scales with meaningful activity and change rather than raw object
count. Extremely large conceptual quantities use compact state, aggregation, caching, snapshots,
or batching instead of one independently ticking object per represented unit.

## 8. Capabilities Are Compatibility Layers

NeoForge energy, item, and fluid capabilities may expose simulation resources. They must not become the authoritative storage model.

## 9. Persistence Is Quarantined; It Does Not Run the Game

NBT exists only at host-required save/load hooks and is treated as contaminated boundary data. It is decoded once, validated into typed snapshots, and discarded. The engine never stores `CompoundTag`, SNBT, or NBT-encoded blobs as runtime state, and no gameplay path serializes every tick.

## 10. Avoid Premature Abstraction

A reusable abstraction is added only when a real milestone demonstrates the need. Future fluids, gases, chemistry, and material decomposition shape boundaries but are not implemented early merely because they are planned.

## 11. Documentation Is Part of the Deliverable

Architectural changes are unfinished until the relevant manual, ADR, catalog, roadmap, and milestone record are updated.
