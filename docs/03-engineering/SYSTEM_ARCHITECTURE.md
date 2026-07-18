# System Architecture

## Dependency Flow

```text
Vision
  ↓
Gameplay pillars and progression
  ↓
Capabilities
  ↓
Data definitions and registries
  ↓
Runtime systems
  ↓
Extension points and integrations
  ↓
Player experience
```

Lower layers implement the intent of the layers above them. Implementation details must not redefine the project vision by accident.

## Core Layers

### Design Layer

The Constitution, Starting Conditions, Gameplay Pillars, Civilization Blueprint, and progression documents define the non-code contract.

### Capability Layer

Capabilities describe what the game must make possible: renewable resources, processing, storage, power, logistics, research, biology, and dimensional advancement.

### Data Layer

Registries, tags, recipes, JSON resources, datapacks, configuration, and KubeJS scripts define content and balancing values.

### Runtime Layer

Java code validates data, executes mechanics, synchronizes state, preserves save compatibility, and exposes extension points.

### Integration Layer

Optional integrations adapt external systems to Project Skyblock's capabilities without making those mods mandatory for core progression.

## Change Rule

When adding a feature, update the highest affected layer first. A new mechanic should begin as a documented capability and progression decision before it becomes a runtime implementation.
