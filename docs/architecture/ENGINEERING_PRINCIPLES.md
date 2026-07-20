# Project Skyblock Engineering Principles

> These principles guide every engineering decision made for Project Skyblock.
> They complement the project's Constitution by defining *how* we build the
> engine, rather than *what* the project is.

# 1. TPS Is Sacred

Every system must justify the work it performs.

Prefer:
- Sleeping over polling.
- Event-driven updates over continuous ticking.
- Bounded work over unbounded scans.
- Shared simulation over duplicated logic.

A feature that performs unnecessary work is considered incomplete,
even if it is functionally correct.

# 2. NBT Is Persistence, Not Runtime

NBT exists to restore state after loading a world.

Do not treat NBT as a runtime database.

Persist only data required to reconstruct the simulation.

Avoid storing:
- Cached values
- Recomputable state
- Runtime-only objects
- Large object graphs

The simulation owns authoritative runtime state.

# 3. One Problem, One Solution

If multiple machines solve the same problem,
that problem belongs in the engine.

Machine-specific code should be kept small by composing reusable
simulation components.

# 4. Minecraft Is the Platform

Minecraft and NeoForge provide:
- Rendering
- Player interaction
- Networking
- Persistence
- Capabilities

The engine provides:
- Scheduling
- Simulation
- Processing
- Energy
- Thermal
- Inventory
- Future fluid and gas simulation

Platform code adapts the engine rather than replacing it.

# 5. Architecture Before Features

Do not introduce shortcuts simply to finish a machine.

When repeated logic appears, move it into reusable engine code.

# 6. Measure Before Optimizing

Avoid premature optimization.

Once a real bottleneck is identified, improving it becomes part of
good engineering.

# 7. Preserve Project History

Working prototypes are valuable.

When a Proof of Concept implementation is replaced by the engine,
move it into `legacy-reference/` instead of deleting it.

Record the migration in `MIGRATION_LEDGER.md`.

# 8. The Engine Exists to Build Better Mods

The engine is not the final product.

Its purpose is to make gameplay:
- More maintainable
- More consistent
- More reusable
- More performant

Players should benefit from the architecture even if they never know it exists.

---

## Engineering Review Checklist

Before completing a feature, ask:

- Does this protect TPS?
- Is unnecessary ticking avoided?
- Is runtime state kept out of NBT?
- Can this logic be reused elsewhere?
- Does the simulation remain authoritative?
- Is this improving the engine instead of adding technical debt?
- Will this still make sense a year from now?

If the answer to any of these is "no", revisit the design before merging.
