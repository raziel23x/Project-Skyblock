# Energy System

## Purpose

Energy represents the ability to perform work and sustain automation. It should progress from direct human effort and heat to mechanical power, electricity, and advanced energy systems.

## Core Design Rules

- Early machines should work through understandable power sources.
- Energy progression should unlock scale and control, not merely larger numbers.
- Heat, mechanical power, and electricity should remain meaningfully distinct where practical.
- Compatibility with common mod energy systems should be optional and isolated.

## Progression

### Human and Environmental Power

- Manual tools
- Gravity
- Water
- Wind
- Fire and heat

### Mechanical Power

- Shafts
- Rotational machinery
- Flywheels
- Pumps
- Mechanical transmission

### Electrical Power

- Generation
- Storage
- Distribution
- Motors
- Control systems

### Industrial Energy

- High-temperature processes
- Large generators
- Grid management
- Energy recovery

### Advanced Energy

- Dimensional energy
- High-density storage
- Specialized endgame systems

## Energy Uses

- Motion
- Heating
- Cooling
- Pumping
- Separation
- Lighting
- Environmental control
- Computation and research
- Dimensional access

## Vanilla Integration

Redstone is a control and signaling system, not automatically a universal power source. Furnaces remain valid heat-processing devices. Lava, fuel items, daylight, flowing water, and other vanilla mechanics may support energy systems where appropriate.

## Automation

Energy automation includes regulation, shutdown, load balancing, and storage management.

## Restrictions

- Avoid meaningless tier inflation.
- Avoid a universal generator that makes every other source irrelevant.
- Avoid hidden losses players cannot understand.
- Power failure should pause or degrade systems rather than always destroy them.

## Open Questions

- Will the project define its own electrical unit or use NeoForge energy?
- How strongly should heat be simulated?
- Should mechanical power integrate directly with Create when installed?
- What are acceptable conversion losses between power forms?

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
