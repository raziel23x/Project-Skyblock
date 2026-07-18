# Machines System

## Purpose

Machines turn learned processes into repeatable infrastructure. They should feel like tools assembled for understandable tasks, not black boxes that accept arbitrary ingredients.

## Core Design Rules

- Every machine has a clear physical or magical function.
- One machine may support a family of related processes.
- Machines should expose meaningful inputs, outputs, and operating requirements.
- Multiblocks are used when scale or structure adds gameplay, not simply to look impressive.

## Machine Families

- Cutting and shaping
- Crushing and grinding
- Mixing
- Heating and cooling
- Pressing
- Pumping
- Separating
- Culturing
- Chemical reaction
- Smelting and casting
- Environmental control
- Dimensional stabilization

## Progression

### Tools and Workstations

Manual tools, benches, primitive furnaces, containers, and simple apparatus.

### Mechanical Machines

Powered cutters, mills, pumps, mixers, and presses.

### Controlled Machines

Measured temperature, pressure, speed, fluid flow, and timed operation.

### Industrial Systems

Continuous processing, parallel operation, logistics integration, and recovery loops.

## User Experience

Players should be able to infer:

- what the machine does;
- why the recipe belongs there;
- what resource or energy it consumes;
- why an upgraded machine performs better.

## Vanilla Integration

Crafting tables, furnaces, blast furnaces, smokers, stonecutters, grindstones, brewing stands, composters, cauldrons, anvils, and smithing tables remain useful.

Project machines may extend these workflows without erasing them.

## Data Requirements

Recipes should be data-driven and define:

- Inputs
- Outputs
- Process type
- Duration
- Energy or environmental requirements
- Optional catalysts
- Byproducts
- Unlock or research conditions

## Restrictions

- Avoid one-block machines for every individual recipe.
- Avoid machine tiers distinguished only by speed.
- Avoid opaque recipe failure.
- Avoid mandatory GUI complexity where world interaction would be clearer.

## Open Questions

- Which early processes should be in-world rather than GUI-based?
- Which machines require multiblocks?
- How are upgrades represented?
- What common machine interface standards are necessary?

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
