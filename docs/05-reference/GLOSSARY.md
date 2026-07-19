# Glossary

## Age of Civilization
A broad stage of social and technical capability. Preferred over “tech tier.”

## Automation Stage
The degree to which a process has moved from manual work to automated production.

## Bootstrap Acquisition
The first reliable method for obtaining a resource.

## Civilization Core
The shared progression model connecting the mod's systems.

## Discovery
A learned capability or piece of knowledge.

## Industrial Production
A scalable production method intended for established infrastructure.

## Knowledge Web
The non-linear network of discoveries and connected systems.

## Optional Integration
Compatibility code that activates only when another mod is installed.

## Process
A transformation of inputs into outputs. Preferred over “generator” when no generation occurs.

## Renewable Production
A sustainable resource-production method that removes permanent scarcity.

## Engine Terms

### Authoritative State
The single backend-owned source of truth for a simulation value.

### Capability Adapter
A Minecraft or NeoForge compatibility layer that exposes backend state without owning it.

### Dirty Flag
A category of external work required after state changes, such as persistence, client synchronization, or scheduler reevaluation.

### Machine Component
A focused reusable backend object providing one machine capability or resource family.

### Simulation Participant
An object that can be registered with and executed by the simulation scheduler.

### Simulation Budget
A fixed allowance limiting the work one execution may perform.

### Wake Event
A meaningful change that makes an idle or blocked participant eligible for reevaluation.

### Blocked
A lifecycle condition in which work cannot proceed until an external change occurs.

### Sleeping
A lifecycle condition in which no recurring work is required.

### Topology
The nodes, connections, and routes that describe a network independently of resource transfer.

### Diagnostics
An immutable observation of backend state intended for testing, debugging, synchronization, or presentation.
