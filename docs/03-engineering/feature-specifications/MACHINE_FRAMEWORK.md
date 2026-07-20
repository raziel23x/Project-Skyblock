# Machine Framework Feature Specification

## Status

Core framework and reusable component expansion are implemented through Backend Milestone 13.

## Goal

Provide a Minecraft-independent framework for machines with authoritative typed state, bounded event-driven execution, explicit activity, immutable diagnostics, and reusable resource components.

## Implemented Core

- `MachineId`
- `MachineActivity`
- `MachineState`
- `MachineLogic`
- `MachineParticipant`
- `MachineDiagnostics`
- `MachineEnergyComponent`
- `MachineInventoryComponent`
- `MachineThermalComponent`
- `MachineProcessingComponent`

## Responsibility Split

### MachineState

Owns machine lifecycle bookkeeping and typed machine-owned state. It does not access the world, render, send packets, or serialize raw NBT.

### MachineLogic

Implements bounded machine-specific backend behavior. It receives simulation context and state and returns an explicit simulation result.

### MachineParticipant

Connects machine state and logic to the scheduler. It translates execution results into sleeping, scheduled, blocked, active, or invalid activity.

### Components

Focused reusable components own or delegate resources such as energy, inventory, fluids, gases, thermal state, and processing state.

## Planned Components

- MachineFluidComponent
- MachineGasComponent

## Integration Rules

A block entity may host a machine participant and expose adapters, but it may not duplicate authoritative state or create a second lifecycle system. Menus and screens consume diagnostics or synchronized views. Capabilities delegate to components.

## Non-Goals

The framework does not define final production recipes, machine art, GUI layout, animations, sounds, research unlocks, or Game Era balancing.
