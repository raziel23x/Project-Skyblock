# Architecture Overview

## Dependency Rule

`simulation` packages may depend on Java and other simulation packages. They must not import Minecraft or NeoForge classes.

## Ownership Map

| Concern | Authoritative Owner | Adapter / Consumer |
|---|---|---|
| Scheduling | `SimulationScheduler` | server integration |
| Machine lifecycle | `MachineState` / `MachineParticipant` | block entity |
| Energy | `SimulationEnergyState` | NeoForge Energy adapter |
| Thermal state | `ThermalState` | machine/world adapter |
| Network topology | `EnergyNetworkTopology` | cable discovery adapter |
| Dirty categories | `DirtyStateTracker` | persistence and sync layers |
| UI presentation | immutable diagnostics | menus and screens |

## Package Map

```text
simulation.core      shared execution contracts
simulation.thermal   thermal state and transfer
simulation.energy    energy state, networks, and runtime
simulation.machine   machine lifecycle and logic
simulation.machine.component
                     reusable machine-owned capabilities
```

## Architectural Test

A new backend class belongs in `simulation` only when it can be understood, constructed, and tested without a Minecraft world, block position, block entity, menu, packet, or NeoForge capability.
