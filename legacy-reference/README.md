# Legacy Reference Staging

This directory is separate from active NeoForge code.

## Rules

- Never place Forge 1.16 Java directly under `src/main/java`.
- Never bulk-copy generated resources into `src/main/resources`.
- Preserve legacy behavior, assets, and recipes only as references.
- Rebuild retained features using current NeoForge architecture.
- Move a feature into active code only when implementation, resources, configuration, and tests are ready together.
