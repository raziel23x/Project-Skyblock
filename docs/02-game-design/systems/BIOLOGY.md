# Biology System

## Purpose

Biology is the foundation of early survival and long-term renewable life. It governs decomposition, microorganisms, organic growth, breeding, biological samples, and the conversion of dead material into living systems.

Biology should make the player feel that life is something they cultivate, protect, study, and eventually engineer.

## Core Design Rules

- Living materials come from living or once-living sources.
- Dirt, compost, nutrients, and biomass should be connected.
- The first specimen may be scarce; knowledge makes later specimens renewable.
- Biological systems should interact with vanilla crops, animals, bees, fungi, and trees.
- Biology must not become a reskinned ore-processing system.

## Player Journey

### Survival

The player learns to preserve saplings, collect plant matter, and create primitive compost.

### Discovery

The player identifies decay, moisture, temperature, and nutrient balance as useful variables.

### Establishment

Compost, soil improvement, fungi, and simple breeding support reliable food production.

### Industry

Controlled cultures, nutrient media, tissue propagation, and biological processing create scalable renewable inputs.

### Civilization

Advanced genetics, ecosystem management, and closed-loop biological production support the entire settlement.

## Primary Inputs

- Leaves
- Saplings
- Seeds
- Food waste
- Rotten flesh
- Bones
- Fungi
- Water
- Manure or equivalent animal waste
- Biological samples
- Heat and light where appropriate

## Primary Outputs

- Compost
- Humus
- Nutrient material
- Biomass
- Culture samples
- Fertilizers
- Soil amendments
- Biological catalysts
- Renewable plant and animal specimens

## Manual Processes

- Layering organic matter for compost
- Drying or soaking plant matter
- Separating seeds from produce
- Collecting samples from plants, animals, and fungi
- Combining carbon-rich and nitrogen-rich materials
- Maintaining moisture and airflow

## Automation Path

1. Manual compost pile or container
2. Managed composter with measurable conditions
3. Mechanical shredder and mixer
4. Controlled bioreactor
5. Automated nutrient recovery and culture propagation

Automation should improve throughput, consistency, and recovery. It should not unlock outputs the player has never produced manually.

## Vanilla Integration

Vanilla composters remain useful. Project systems may provide more control, more outputs, or higher efficiency, but should not make the vanilla composter worthless.

Vanilla breeding remains valid. Advanced breeding and genetics provide predictability, specialization, and scale.

Bone meal remains useful. Project fertilizers broaden the system rather than replacing bone meal.

## Dependencies

Biology supports:

- Agriculture
- Forestry
- Ecology
- Chemistry
- Food production
- Waste recycling
- Early renewable materials

## Restrictions

- No instant conversion of arbitrary organic matter into rare materials.
- No biological system should create matter without a defined feedstock.
- Genetic systems should improve traits, not create unrelated resources from nothing.
- Early biology should remain understandable without requiring advanced laboratory equipment.

## Open Questions

- Which biological variables are simulated and which are abstracted?
- Will microorganisms exist as items, fluids, data, or hidden process states?
- How much genetic depth is appropriate before the system becomes spreadsheet-heavy?
- Should animal waste be explicit or represented through bedding and compost inputs?

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
