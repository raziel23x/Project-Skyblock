# Engine Baseline and Validation

> Status: **Approved engineering record**
>
> Purpose: preserve what the engine was expected to provide at the end of the Engine Era, then
> compare that expectation with what Project Skyblock and later projects actually required.

## 1. What the Baseline Is

At the end of the Engine Era, create a versioned snapshot of the Minecraft-independent simulation
backend, its platform adapters, its tests, and its documented contracts.

The baseline records:

- implemented simulation systems and reusable machine components;
- ownership boundaries between simulation, platform, persistence, networking, and presentation;
- public and internal extension points;
- known limitations and intentionally deferred systems;
- performance assumptions and available measurements;
- the expected needs of Project Skyblock's Game Era;
- capabilities believed useful to future projects.

The baseline is evidence. It is not a promise that the design is perfect.

## 2. What the Baseline Is Not

The baseline is **not an engine freeze**.

After it is captured:

- defects may be corrected;
- reusable capabilities may be added when real projects prove a need;
- APIs may evolve through documented, versioned decisions;
- Project Skyblock-specific gameplay may continue to expand;
- platform adapters may change as Minecraft and NeoForge change.

A capability is not removed merely because Project Skyblock has not used it yet. The simulation may
serve later mods with different requirements. Removal requires an architectural reason such as an
invalid model, duplicated responsibility, harmful maintenance burden, or replacement by a better
contract. Lack of immediate use is not sufficient.

## 3. Change Classification

Every meaningful post-baseline change should be classified as one of the following:

1. **Baseline capability used as designed** — gameplay consumed the existing engine contract without
   changing it.
2. **Reusable engine extension** — a real project demonstrated a generally useful capability that
   belongs in the shared simulation.
3. **Project-specific gameplay** — behavior belongs to Project Skyblock and does not change the
   reusable engine contract.
4. **Platform-adapter change** — Minecraft, NeoForge, persistence, networking, rendering, or
   capability integration changed while simulation ownership remained intact.
5. **Architectural correction** — an original assumption or boundary was wrong and required a
   documented correction.
6. **Deferred capability activated** — a previously planned area, such as fluids, gases, chemistry,
   pressure, or multiblock composition, became justified by a real consumer.

## 4. Validation Journal

During the Game Era, record changes that affect the engine in a validation journal. Each entry must
include:

- date and project;
- original baseline assumption;
- observed requirement;
- change classification;
- why the responsibility belongs in that layer;
- affected contracts and tests;
- whether the change benefits future projects;
- documentation and migration work performed.

## 5. Final Validation Report

After Project Skyblock reaches its intended production state, compare the final engine with the
baseline and answer:

- Which predictions were correct?
- Which capabilities were used without modification?
- Which reusable systems had to be added?
- Which platform assumptions changed?
- Which project-specific systems correctly remained outside the engine?
- Which abstractions were premature, insufficient, or exactly right?
- What should be retained for future mods even if Project Skyblock did not use it?
- Is standalone library extraction now justified?

The report exists to improve future engineering decisions, not to grade the project or search for
features to delete.

## 6. Relationship to Library Extraction

The baseline and validation report provide the evidence needed to decide whether the simulation
should become a standalone library mod shared by Project Skyblock and future projects.

Extraction is justified when multiple real projects need the backend, the core remains free of
Project Skyblock-specific dependencies, tests are independent of Minecraft where practical, APIs
have demonstrated stability, and long-term versioned maintenance is accepted.

Until then, the repository must preserve extraction-ready boundaries without pretending that an
unproven internal package is already a stable public library.
