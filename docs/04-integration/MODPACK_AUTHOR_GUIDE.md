# Modpack Author Guide

Project Skyblock is intended to become a modpack building block, but its public integration API is
not frozen yet. Pack customizations should use documented data and adapter surfaces rather than
internal simulation classes.

## Supported Direction

Recommended customization areas include:

- recipes and tags;
- loot and progression values;
- research requirements;
- datapacks;
- future KubeJS bindings over the public API;
- optional compatibility modules.

KubeJS is not required for Project Skyblock to run. Java owns the authoritative simulation; scripts
will customize and orchestrate only through validated public contracts.

## Material Data

Project Skyblock material definitions use mod-specific resource roots to avoid colliding with
unrelated mods that also use generic material folders.

```text
data/<namespace>/projectskyblock/materials/<id>.json
data/<namespace>/projectskyblock/processing_routes/<id>.json
```

A reload prepares materials, routes, and source provenance as one candidate. Any malformed or cross-reference-invalid candidate is rejected in full and the previous known-good
snapshot remains active.

## Integration Rule

Do not import or reflect into internal implementation packages. Wait for a documented extension
point or public API contract. The engine is greenfield and internal contracts may still change as
prototype stress tests expose real requirements.
