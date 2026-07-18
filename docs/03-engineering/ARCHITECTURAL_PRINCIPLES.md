# Architectural Principles

These principles connect Project Skyblock's design goals to implementation decisions.

## Data Before Special Cases

Java provides stable frameworks. Registries, tags, JSON, datapacks, configuration, and scripts define content wherever practical.

## Capabilities Before Implementations

Design the renewable capability first—such as stone production, fluid handling, or material processing—then choose the initial block, machine, recipe, or integration that provides it.

## Systems over Isolated Features

A new feature should participate in existing resource, research, energy, logistics, or progression systems. Avoid one-off code paths that cannot be extended or reused.

## Stable Extension Points

Public APIs, events, tags, registries, datapacks, and KubeJS hooks are contracts. Prefer additive change, document compatibility expectations, and avoid exposing internal implementation details as accidental APIs.

## Deterministic and Testable Behavior

Core mechanics should have explicit inputs, outputs, limits, and failure conditions. Randomness must be bounded and documentable. Important rules should be testable without relying only on manual gameplay.

## Optional Integrations Stay Optional

External mods may provide alternate recipes, storage, transport, or automation, but the core progression graph must remain solvable without them unless a modpack deliberately changes that contract.

## Documentation Mirrors Dependencies

Vision defines why. Game design defines what. Architecture defines how systems relate. Feature specifications define implementation contracts. Reference documentation defines canonical names and data.
