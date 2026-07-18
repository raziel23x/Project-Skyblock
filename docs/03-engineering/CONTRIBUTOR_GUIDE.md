# CONTRIBUTOR_GUIDE.md

# Contributor Guide

This document explains how Project Skyblock should be extended.

## Project Layers

1. Constitution
2. Civilization Blueprint
3. Gameplay Philosophy
4. System Documentation
5. Resource Bible
6. Technical Architecture
7. Code

Lower layers must not contradict higher layers.

---

# Design Checklist

Before implementing anything:

## Purpose

Why should this exist?

## Story

How does this help build a civilization?

## Vanilla

Does it complement rather than replace vanilla?

## Progression

Where does it fit?

- Survival
- Settlement
- Industry
- Mastery

## Automation

Can the player perform it manually first?

## Data

Can this be defined through JSON, tags, or datapacks instead of Java?

---

# Coding Guidelines

- Favor composition over special cases.
- Prefer registries and data definitions.
- Keep APIs small and stable.
- Avoid hardcoded item references.
- Build around tags where practical.
- Document public APIs.

---

# Documentation Requirements

Every new gameplay system should include:

- Purpose
- Player experience
- Inputs
- Outputs
- Progression
- Automation
- Vanilla integration
- Design restrictions

Documentation is considered part of the implementation.

---

# Review Questions

A reviewer should be able to answer YES to these:

- Does it fit the Constitution?
- Does it strengthen the civilization vision?
- Is it understandable?
- Is it maintainable?
- Is it extensible?
- Is it documented?

If not, revise before merging.

---

# Long-Term Goal

Project Skyblock should remain understandable years from now, even as contributors change.

Consistency is more valuable than feature count.