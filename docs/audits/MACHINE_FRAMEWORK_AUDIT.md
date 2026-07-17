# Machine Framework Audit

## Purpose

Document the current machine responsibilities and define a safe, incremental path toward shared machine infrastructure without changing gameplay behavior.

This audit covers the currently documented machine systems:

- Material Crusher
- Thermal Generator Mk I
- Mixing Bowl

## Naming Standard

All new packages, classes, methods, fields, resources, and commits must use names that clearly describe their responsibility.

Avoid vague names such as:

- `Common`
- `Core`
- `Future`
- `Misc`
- `Stuff`
- `Thing`
- `Utils`
- `Manager`
- `Helper`

These names may only be used when the full name still explains the exact purpose, such as `InventoryTransferHelper`.

Prefer names such as:

- `MachineEnergyStorage`
- `MachineInventoryHandler`
- `MachineRecipeProcessor`
- `MachineStateSerializer`
- `MachineMenuData`
- `MaterialCrusherRecipeCache`
- `ThermalGeneratorFuelConverter`

## Current Machine Responsibilities

### Material Crusher

Documented responsibilities:

- Datapack-driven crushing recipes
- FE-first processing
- Furnace-fuel fallback
- Strict sided item automation
- Processing progress
- Fuel state
- Energy state
- GUI synchronization
- Active block state
- Exterior animation and particles
- Optional Ex Deorum output compatibility

### Thermal Generator Mk I

Documented responsibilities:

- Furnace-fuel conversion
- Lava input through NeoForge fluid capability
- Internal lava-equivalent storage
- Internal FE storage
- FE generation
- Adjacent energy output
- Persistent fuel queue
- Persistent fluid contents
- Persistent energy contents
- Active block state
- Exterior animation and particles

### Mixing Bowl

Documented responsibilities:

- Reusable crafting container behavior
- Crafting recipe participation
- Item durability or reusable-item handling
- Recipe and remainder behavior

The Mixing Bowl should not be forced into the powered-machine framework unless its implementation actually shares machine lifecycle behavior.

## Shared-System Candidates

The following systems are candidates for extraction only after direct code comparison confirms meaningful duplication.

### Energy Storage

Potential shared responsibilities:

- FE capacity
- FE receive limits
- FE extract limits
- Serialization
- Change notifications
- Menu synchronization
- Capability exposure

Recommended descriptive class name:

```java
MachineEnergyStorage
```

Do not create an abstract energy class until both powered machines require the same behavior.

### Inventory Handling

Potential shared responsibilities:

- Item storage
- Slot validation
- Sided insertion
- Sided extraction
- Change notifications
- Serialization
- Capability exposure

Recommended descriptive names:

```java
MachineInventoryHandler
MachineSlotDefinition
SidedInventoryAccess
```

### Processing Progress

Potential shared responsibilities:

- Current progress
- Required progress
- Progress reset
- Processing state
- Menu synchronization
- Persistence when appropriate

Recommended descriptive name:

```java
MachineProcessingProgress
```

### Recipe Lookup

Potential shared responsibilities:

- Recipe lookup
- Recipe caching
- Cache invalidation
- Input matching
- Output validation

Recommended descriptive names:

```java
MachineRecipeLookup
CachedMachineRecipe
```

Recipe types must remain machine-specific where their inputs, outputs, or processing rules differ.

### Machine State Serialization

Potential shared responsibilities:

- Energy save/load
- Inventory save/load
- Progress save/load
- Active-state persistence
- Version-safe field naming

Recommended descriptive name:

```java
MachineStateSerializer
```

A serializer should not become a dumping ground. Machine-specific values remain in their machine implementation.

### Menu Synchronization

Potential shared responsibilities:

- Energy values
- Progress values
- Fuel values
- Fluid values
- Active state

Recommended descriptive names:

```java
MachineMenuData
MachineDataSlot
```

Only values needed by the client menu should be synchronized.

## Systems That Should Remain Machine-Specific

### Material Crusher

Keep these responsibilities in Material Crusher code:

- Crushing recipe rules
- FE-versus-fuel decision logic
- Crusher slot layout
- Crusher-sided automation rules
- Crusher processing effects
- Optional Ex Deorum compatibility

### Thermal Generator Mk I

Keep these responsibilities in Thermal Generator code:

- Fuel-to-lava-equivalent conversion
- Lava consumption rules
- FE generation rate
- Energy output behavior
- Thermal fluid tank rules
- Thermal visual effects

### Mixing Bowl

Keep reusable crafting behavior separate unless direct code inspection proves that it shares a genuine lifecycle component with powered machines.

## Proposed Package Organization

Do not reorganize the entire source tree in one commit.

Use descriptive domain packages only when shared code is actually extracted.

```text
machine/
├── energy/
├── inventory/
├── processing/
├── recipes/
├── serialization/
└── synchronization/
```

Machine-specific code should remain grouped by machine:

```text
machine/
├── materialcrusher/
├── thermalgenerator/
└── mixingbowl/
```

Follow the repository's existing package naming style. Do not introduce underscores into Java package names if the existing project uses compact lowercase package names.

## Incremental Refactoring Order

### Step 1 — Direct Code Comparison

Compare the Material Crusher and Thermal Generator implementations for duplicated:

- Energy storage
- Inventory handling
- Save/load logic
- Capability registration
- Menu data
- Active-state updates
- Server tick structure

Do not extract code merely because two classes contain similar-looking methods.

### Step 2 — Extract the Smallest Proven Shared Component

Begin with the smallest component that has the same responsibility and behavior in both machines.

Likely candidates:

1. Energy storage
2. Menu data synchronization
3. State serialization helpers
4. Inventory capability exposure

### Step 3 — Migrate One Machine

Move one machine to the shared component.

Build and test before touching the second machine.

### Step 4 — Migrate the Second Machine

Move the second machine only after the first migration is stable.

### Step 5 — Remove Confirmed Duplication

Delete old duplicated code only after both machines use the shared component and pass testing.

## Required Testing After Every Refactor

### Build

```cmd
gradlew.bat clean build
```

### Client Launch

```cmd
gradlew.bat runClient
```

### Material Crusher

- Accepts valid inputs
- Rejects invalid inputs
- Uses FE before fuel
- Uses furnace fuel when FE is unavailable
- Preserves progress correctly
- Preserves inventory correctly
- Sided automation still follows existing rules
- GUI values remain synchronized
- Active visual state remains correct

### Thermal Generator Mk I

- Accepts furnace fuel
- Accepts lava through fluid capability
- Preserves queued fuel conversion
- Preserves tank contents
- Preserves stored FE
- Generates the configured FE amount
- Pushes FE to adjacent receivers
- GUI values remain synchronized
- Active visual state remains correct

### Mixing Bowl

- Remains reusable
- Participates in recipes correctly
- Returns or preserves the expected container item
- Is unaffected by powered-machine refactoring

## Completion Criteria

The machine framework audit is complete when:

- Current machine responsibilities are documented.
- Shared responsibilities are confirmed through direct code inspection.
- Machine-specific behavior is clearly separated.
- Naming follows the self-documenting-name rule.
- The first shared component is selected.
- No gameplay behavior has changed.
- The project builds and launches successfully.

## Developer Tip

Extract shared code only when two or more implementations have the same responsibility and behavior. Similar-looking code is not automatically reusable code.
