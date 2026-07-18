# Chemistry System

## Purpose

Chemistry provides controlled transformation of matter through separation, reaction, purification, and synthesis. It converts early alchemical understanding into measurable industrial processes.

## Core Design Rules

- Matter has traceable inputs and outputs.
- Conservation should be respected at the abstraction level used by gameplay.
- Better equipment improves purity, efficiency, safety, and scale.
- Chemistry should create networks of useful substances, not hundreds of dead-end intermediates.

## Major Process Families

- Crushing and grinding
- Dissolution
- Filtration
- Distillation
- Evaporation
- Crystallization
- Precipitation
- Electrolysis
- Roasting
- Reduction
- Oxidation
- Fermentation
- Polymerization where appropriate

## Progression

### Primitive Chemistry

Ash, lime, charcoal, brine, fermentation, soap-like cleaners, pigments, and simple salts.

### Laboratory Chemistry

Glassware, measured fluids, controlled heat, filters, catalysts, and purity.

### Industrial Chemistry

Continuous processing, pressure, electrochemistry, bulk fluids, waste treatment, and recycling.

### Advanced Chemistry

High-purity materials, engineered catalysts, advanced fuels, polymers, and dimensional compounds.

## Data Model Requirements

Chemical substances should be data-driven where practical and define:

- Identifier
- Form
- Tags or traits
- Physical state
- Hazard class if used
- Valid processes
- Major uses
- Recycling or disposal paths

## Vanilla Integration

Vanilla items should participate through tags and mappings rather than being replaced. Examples include charcoal, blaze powder, redstone, glowstone, gunpowder, clay, glass, dyes, and potion ingredients.

## Waste and Byproducts

Byproducts should create choices:

- Reuse
- Refine
- Neutralize
- Store
- Dispose

Waste systems should add planning without becoming punishment.

## Automation

Chemistry automation includes fluid handling, dosing, heat control, pressure control, separation, and quality monitoring.

## Restrictions

- No unexplained one-step conversion of common matter into unrelated rare matter.
- Avoid requiring real-world chemistry knowledge to play.
- Avoid hazardous mechanics that are merely random explosions.
- Do not make every recipe demand a unique vessel.

## Open Questions

- How detailed should stoichiometry be?
- Should purity be a numeric value, tier, or process state?
- Which chemicals are explicit fluids versus abstract reagents?
- How are modded chemical systems integrated without duplication?

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
