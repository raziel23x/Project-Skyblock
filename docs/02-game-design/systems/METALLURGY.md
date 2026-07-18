# Metallurgy System

## Purpose

Metallurgy governs obtaining, refining, alloying, shaping, and recycling metals. It replaces arbitrary skyblock ore multiplication with understandable material recovery and industrial development.

## Core Design Rules

- Metals originate from mineral, biological, atmospheric, trade, dimensional, or recycled sources.
- Ore processing should improve recovery, not create matter.
- Alloy properties should justify their use.
- Vanilla furnace smelting remains useful.

## Progression

### Native and Recovered Metals

Small amounts come from bootstrap deposits, salvage, trade, mob drops, or dimensional materials.

### Primitive Smelting

Charcoal furnaces, bloom-like products, casting, hammering, and basic purification.

### Controlled Metallurgy

Temperature control, fluxes, alloying, molds, and repeatable quality.

### Industrial Metallurgy

Bulk furnaces, electrorefining, gas reactions, rolling, extrusion, and recycling.

### Advanced Materials

High-performance alloys, composites, dimensional metals, and specialty treatments.

## Inputs

- Mineral concentrates
- Scrap
- Metal-bearing biological or chemical products
- Fluxes
- Carbon
- Fuel or electrical energy
- Alloying elements
- Refractory materials

## Outputs

- Ingots
- Nuggets
- Plates
- Rods
- Wire
- Cast parts
- Powders
- Slag
- Recovered secondary metals

## Vanilla Integration

- Furnace and blast furnace recipes remain available.
- Iron farms remain valid.
- Piglin and villager trade remain useful.
- Project metallurgy offers broader metal access, predictable alloys, recycling, and industrial scale.
- Existing modded ingots should integrate through tags.

## Automation

Automation advances from hand casting and hammering to controlled furnaces, continuous casting, rolling, and automated quality handling.

## Restrictions

- No infinite metal from crushing a single block repeatedly.
- No alloy should exist only as an arbitrary machine tier.
- Recycling should recover less than perfect amounts unless advanced systems justify near-closed loops.
- Metallurgy should not require memorizing exact real-world temperatures.

## Open Questions

- What is the canonical bootstrap source for each vanilla metal?
- How should iron farms compare with industrial iron production?
- Is metal quality represented explicitly?
- Which processing forms are essential versus unnecessary clutter?

---

## Feature Review Checklist

Before implementation, confirm that the proposed feature:

- complements vanilla Minecraft rather than disabling it;
- has an understandable cause and effect;
- introduces knowledge before automation;
- supports the journey from survival to civilization;
- avoids unnecessary grind and arbitrary recipe chains;
- can be expressed through data where practical;
- has a clear place in progression;
- defines failure, recovery, and renewable paths;
- does not accidentally invalidate several other systems.

## Status

Design specification. Exact values, recipes, machine costs, and timings remain subject to playtesting.
