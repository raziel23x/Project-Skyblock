# Architecture Review Checklist

> Status: **Approved project workflow**
>
> Use this checklist before beginning a new backend system or materially redesigning an existing
> one.

---

## 1. Problem

- What exact problem is being solved?
- Is it required by an approved milestone?
- Is there a current consumer?
- Is the problem supported by profiling, testing, gameplay design, or platform requirements?
- Are we solving a real problem rather than a hypothetical future problem?

If the problem cannot be stated in one or two clear paragraphs, stop and define it first.

---

## 2. Existing Architecture

- Does an existing system already own this responsibility?
- Can an existing contract be extended without weakening its purpose?
- Would adding this behavior create duplicate sources of truth?
- Does this belong in backend logic, platform integration, persistence, networking, or presentation?
- Are we bypassing an existing abstraction because direct Minecraft code appears faster to write?

Reuse is preferred only when responsibilities genuinely match.

Do not force unrelated systems into one abstraction merely to reduce class count.

---

## 3. Constitution and Engineering Standards

Confirm that the proposal:

- supports Project Skyblock's scientific and engineering identity,
- remains backend-first,
- uses typed state,
- avoids raw NBT in gameplay code,
- remains server-authoritative,
- permits idle sleeping,
- uses bounded recurring work,
- avoids unnecessary world scans,
- has explicit cache invalidation,
- uses change-driven synchronization,
- can be tested automatically,
- keeps player-facing text localized and out of simulation authority.

Any exception must be written down and justified before implementation.

---

## 4. Ownership

For every mutable value, identify:

- authoritative owner,
- allowed mutators,
- readers,
- persistence owner,
- synchronization owner,
- invalidation conditions.

Reject the design when two systems can independently mutate the same authoritative value.

---

## 5. Lifecycle

Define behavior for:

- creation,
- placement,
- load,
- external changes,
- scheduled work,
- blocking,
- unblocking,
- chunk unload,
- chunk reload,
- removal,
- migration,
- invalid or corrupted state.

A system without unload and reload behavior is not ready for implementation.

---

## 6. Performance

Answer:

- What wakes this system?
- What lets it sleep?
- What work can repeat?
- What is the upper bound per execution?
- What is the upper bound per tick?
- What is cached?
- What invalidates each cache?
- What happens with 1,000 idle instances?
- What happens with 1,000 active instances?
- What happens when every output is blocked?
- What diagnostics prove its cost?

Avoid the phrase "should be fine."

Measure it or define how it will be measured.

---

## 7. Persistence

Confirm:

- internal state is typed,
- platform serialization is isolated,
- data has defaults,
- invalid values are handled,
- migrations are versioned,
- removed registry entries are survivable,
- persistence does not run every tick,
- gameplay packages do not import NBT types.

---

## 8. Networking

Confirm:

- the server remains authoritative,
- every client action is validated,
- state is sent only when changed,
- packet contents are minimal,
- rapidly changing display values use sensible thresholds,
- unnecessary hidden state is not synchronized,
- animation can derive from compact server state.

---

## 9. Testing

Before implementation, list:

- unit tests,
- GameTests,
- failure cases,
- chunk lifecycle tests,
- performance scenario,
- generated stress layout,
- measurable completion criteria.

If the design cannot be tested without manually watching a machine, it needs a better interface.

---

## 10. Public API and Extensibility

Decide whether the feature needs:

- no public extension point,
- datapack support,
- KubeJS support,
- tags,
- NeoForge events,
- Java API,
- compatibility adapter.

Do not expose internals merely because an addon might someday need them.

Expose the smallest stable contract with a current use case.

---

## 11. Hyperfocus Guardrail

Before expanding the task, ask:

- Does this change unblock the current milestone?
- Is there a failing test?
- Is there profiling evidence?
- Is there a concrete compatibility problem?
- Is the approved design insufficient for the next implementation?
- Am I polishing something that already satisfies its completion criteria?

If every answer is no, freeze the current work and move to the next milestone.

### Stop statement

Use this exact conclusion when appropriate:

> This satisfies the approved milestone. Further refinement is deferred until a test, profiler,
> integration, or reference implementation demonstrates a concrete need.

---

## 12. Version and Dependency Changes

For a Minecraft, NeoForge, mapping, Gradle, or library update, record:

- the exact current baseline;
- the exact proposed version;
- the concrete feature, fix, compatibility requirement, security improvement, or measured benefit gained;
- adapter or migration work required;
- rollback plan and validation tests.

A newer version number alone is not an engineering reason to update.

## 13. Review Outcome

Every architecture review ends with one outcome:

### APPROVED

The design is sufficiently defined for the current milestone.

### REVISE

Specific unresolved issues must be corrected before implementation.

### DEFER

The idea may be useful later but has no current consumer or milestone requirement.

### REJECT

The proposal duplicates responsibility, violates project standards, or creates unjustified
complexity.

Record the outcome and the reasons.

---

## 13. Required Review Summary

Use this template:

```text
Feature:
Milestone:
Problem:
Authoritative owner:
Wake conditions:
Sleep conditions:
Maximum recurring work:
Persistence boundary:
Networking boundary:
Required tests:
Public extension point:
Known open decisions:
Review outcome:
Reason:
```

The summary should fit on one page.

A proposal that requires a novel to explain before any code exists is probably trying to solve too
many problems at once.
