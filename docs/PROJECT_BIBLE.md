# Project Skyblock Project Bible

## Purpose

Project Skyblock is a long-term Minecraft engineering, automation, chemistry, and progression framework.

The project must remain organized, readable, maintainable, data-driven where practical, and approachable for both contributors and addon developers.

Every system should have a clear purpose. Every file should have an obvious home. Every public hook should be documented.

---



# Gameplay Design Principles

Project Skyblock is a long-term Minecraft progression framework focused on building a thriving civilization from an empty world through biology, chemistry, engineering, automation, and scientific discovery while complementing—never replacing—vanilla Minecraft.

## 1. Complement Vanilla, Don't Override It
Project Skyblock extends Minecraft rather than replacing it. Vanilla mechanics remain functional whenever practical. Where skyblock removes normal terrain progression, Project Skyblock supplies an equivalent progression that preserves the spirit of vanilla.

## 2. Build a World From Nothing
Players begin with almost nothing. Every expansion should feel earned.

## 3. Mechanics Must Make Sense
Avoid arbitrary "skyblock magic." Systems should have understandable internal logic.

## 4. Discovery Before Automation
Players learn a process before they automate it.

## 5. Renewable Through Knowledge
Resources become renewable through scientific and technological progression.

## 6. Respect Player Creativity
Vanilla farms and contraptions remain valid. Project Skyblock offers richer alternatives instead of disabling vanilla.

## 7. Every Dimension Has a Purpose
Overworld, Nether, and End each have unique roles and progression.

## 8. Preserve the Spirit of Minecraft
Favor solutions that feel like natural extensions of Minecraft.


## Core Principles

1. **Organization comes first.**
2. **Consistency is preferred over cleverness.**
3. **One class, package, file, or component should have one clear responsibility.**
4. **Code should be written for the next person who must read it.**
5. **Systems should be data-driven whenever practical.**
6. **Public extension points must be deliberate, stable, and documented.**
7. **Internal implementation details must not become accidental APIs.**
8. **Every machine and asset must belong to one coherent product family.**
9. **Documentation must explain intent, lifecycle, assumptions, and extension points.**
10. **Complexity must solve a real problem, not exist for its own sake.**

> Code is written once but read hundreds of times. Optimize for the reader first.

---

## Project Organization

Project Skyblock should be structured so a new contributor can quickly determine:

- what each subsystem does;
- where that subsystem begins;
- which classes own which responsibilities;
- what is safe to extend;
- what is internal;
- where data files belong;
- and how systems communicate.

Catch-all packages and ambiguous class names should be avoided.

Names such as `Helper`, `Manager2`, `Thing`, `Stuff`, or unrelated collections inside `Utils` do not communicate ownership or responsibility.

Preferred names describe the exact role of the class or file, such as:

- `MachineEnergyStorage`
- `CrusherRecipeLookup`
- `EnergyCableNetwork`
- `MachineMenuData`
- `ChemicalReactionRegistry`

---

## Public API Philosophy

Third-party addons should not need to inspect or copy internal implementation code.

Stable extension points belong in a dedicated public `api` package.

Internal implementation code must remain separate from the public API.

Public API documentation should explain:

- purpose;
- valid use cases;
- lifecycle;
- logical side or thread expectations;
- accepted and returned values;
- compatibility guarantees;
- and examples of intended integration.

Breaking API changes require a major API version increase.

Compatible additions require a minor API version increase.

Deprecated hooks should remain documented for at least one release cycle before removal.

---

## Code Documentation Standard

Important classes should begin with documentation that explains:

- the class purpose;
- its responsibilities;
- its lifecycle;
- the systems it communicates with;
- its extension points;
- and any assumptions contributors must preserve.

Comments should explain **why**, not narrate obvious syntax.

Bad:

```java
// Add one to the counter.
counter++;
```

Useful:

```java
// Advance only after the server confirms the recipe is still valid.
// This prevents client prediction from consuming items twice.
counter++;
```

---

## Commit Standard

Commits should clearly state their type, scope, and purpose.

Preferred format:

```text
type(scope): concise description
```

Examples:

```text
feat(machine): add shared powered machine framework
fix(network): prevent duplicate energy routing
refactor(recipe): separate lookup from recipe execution
docs(api): document addon machine hooks
assets(machine): introduce universal FE port
data(crusher): move outputs to datapack recipes
```

For substantial commits, the body should explain:

- **Purpose**
- **Affected systems**
- **API impact**
- **Migration notes**, when applicable

Commits should remain focused. Unrelated changes should be committed separately.

---

## Industrial Design Philosophy

Every machine, block, texture, model, animation, sound, and interface should look and behave as though it belongs to the same engineered product line.

The fictional manufacturer is:

**Project Skyblock Industries — PSI**

Nothing should appear randomly assembled from unrelated styles.

Visual components should communicate purpose:

- vents indicate cooling;
- pipes indicate fluid handling;
- cables indicate electricity;
- gears indicate mechanical motion;
- gauges indicate machine status;
- windows indicate inspection;
- exhausts indicate heat;
- reinforced housings indicate pressure or danger.

Decoration should support function rather than obscure it.

---

## Shared Machine Standards

Powered machines should use shared, recognizable components:

- Universal FE Port
- standard mounting height
- standard cable termination
- standard frame proportions
- standard feet
- standard corner hardware
- standard panel seams
- standard vent language
- standard maintenance access
- standard GUI layout

Machines may retain unique silhouettes and functional details, but common interfaces must remain consistent.

Default automation convention:

- **Rear:** FE connection
- **Top:** item input
- **Bottom:** item output
- **Sides:** fluids, upgrades, or machine-specific connections

Exceptions must be intentional and documented.

---

## GUI Standards

Machine interfaces should use consistent placement for:

- machine title;
- player inventory;
- energy storage;
- progress display;
- input slots;
- output slots;
- fluid tanks;
- status messages;
- configuration controls.

Once a player understands one Project Skyblock machine, the next machine should feel familiar.

---

## Data-Driven Design

Recipes, reactions, material definitions, and progression rules should be data-driven whenever this improves extensibility and maintainability.

Data formats must be:

- documented;
- validated;
- version-aware where necessary;
- understandable by pack developers;
- and compatible with datapack overrides when practical.

Hardcoded behavior should be reserved for logic that cannot reasonably or safely be represented as data.

---

## Decision Filter

When multiple implementation choices are valid, prefer the one that is:

1. easier to understand;
2. easier to maintain;
3. easier to extend;
4. easier to document;
5. easier to test;
6. more consistent with existing standards;
7. less likely to create accidental coupling.

The technically cleverest solution is not automatically the best solution.

---

## Contributor Expectation

A contributor should be able to open the repository and quickly discover:

- the project vision;
- the architecture;
- the relevant subsystem;
- the extension point;
- the coding standard;
- the expected tests;
- and the correct commit style.

Contributors should not need psychic powers, archaeological equipment, or a wall covered in red string.

---

## Long-Term Vision

Project Skyblock should mature into a professional, extensible Minecraft platform rather than a collection of disconnected features.

Every contribution should strengthen:

- organization;
- readability;
- consistency;
- maintainability;
- documentation;
- extensibility;
- and player understanding.

When uncertain, choose the solution that makes the project easier for the next developer, pack author, addon creator, and player.
