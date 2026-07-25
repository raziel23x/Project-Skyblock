# Backend Milestones

Milestone documents record completed and candidate architectural slices. They describe purpose, implementation, boundaries, validation, and deliberate deferrals.

## Completed

1. [Explicit Simulation Scheduler](BACKEND_MILESTONE_1_SCHEDULER.md)
2. [Thermal Core](BACKEND_MILESTONE_2_THERMAL_CORE.md)
3. [Energy Simulation](BACKEND_MILESTONE_3_ENERGY_SIMULATION.md)
4. [Energy Network Topology](BACKEND_MILESTONE_4_ENERGY_NETWORK_TOPOLOGY.md)
5. [Energy Network Transfer](BACKEND_MILESTONE_5_ENERGY_NETWORK_TRANSFER.md)
6. [Network Sleeping](BACKEND_MILESTONE_6_NETWORK_SLEEPING.md)
7. [Machine Framework Core](BACKEND_MILESTONE_7_MACHINE_FRAMEWORK_CORE.md)
8. [Machine Energy Component](BACKEND_MILESTONE_8_MACHINE_ENERGY_COMPONENT.md)
9. [Machine Inventory Component](BACKEND_MILESTONE_9_MACHINE_INVENTORY_COMPONENT.md)
10. [Machine Thermal Component](BACKEND_MILESTONE_10_MACHINE_THERMAL_COMPONENT.md)
11. [Scheduler Wake Coalescing](BACKEND_MILESTONE_11_SCHEDULER_WAKE_COALESCING.md)
12. [Machine Composition Proof](BACKEND_MILESTONE_12_MACHINE_COMPOSITION_PROOF.md)
13. [Machine Processing Component](BACKEND_MILESTONE_13_MACHINE_PROCESSING_COMPONENT.md)
14. [Machine Runtime Persistence Bridge](BACKEND_MILESTONE_14_MACHINE_RUNTIME_PERSISTENCE_BRIDGE.md)
15. [Level-Scoped Scheduler Driver](BACKEND_MILESTONE_15_LEVEL_SCOPED_SCHEDULER_DRIVER.md)
16. [First Engine-Owned Machine](BACKEND_MILESTONE_16_FIRST_ENGINE_OWNED_MACHINE.md)
17. [Baseline Hardening and Contract Stabilization](BACKEND_MILESTONE_17_BASELINE_HARDENING.md)
18. [Tiered Developer Validation Environments](BACKEND_MILESTONE_18_TIERED_DEVELOPER_VALIDATION.md)

## Completed Candidates Awaiting Baseline Integration

19A. [Inventory Transactions and Item Capability Adapter](BACKEND_MILESTONE_19A_INVENTORY_TRANSACTIONS_AND_ITEM_ADAPTER.md)

19B1. [Combustion State and Persistence](BACKEND_MILESTONE_19B1_COMBUSTION_STATE_AND_PERSISTENCE.md)

19B2. [Material Crusher Engine Migration](BACKEND_MILESTONE_19B2_MATERIAL_CRUSHER_ENGINE_MIGRATION.md)

## Active Candidate

20A. [Typed Transport Network Core](BACKEND_MILESTONE_20A_TYPED_TRANSPORT_NETWORK_CORE.md)

Milestone 19 was split after audit evidence showed that inventory adaptation, combustion persistence,
and the Material Crusher migration deserve independent validation boundaries. Milestone 20 is split
so generic typed topology and contention, energy-specific dispatch, and live cable migration are
proven independently. The numbering is intentionally flexible; architecture is not constrained to an
obsolete milestone count or premature Mk tier plan.
