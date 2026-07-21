# Localization Standard

> Status: **Required engineering and content standard**

## Principle

All player-facing text is localized from its first implementation. English is the initial language,
not a license to hard-code English strings into Java, menus, screens, packets, commands, errors,
advancements, tooltips, research entries, or machine diagnostics.

## Requirements

- Use stable translation keys for every player-facing string.
- Keep authoritative rules and state language-neutral.
- Send identifiers, structured values, and translation arguments across the network rather than
  preformatted English sentences.
- Keep logs and developer-only diagnostics distinct from player-facing messages.
- Prefer reusable message patterns with typed arguments over many nearly identical strings.
- Never use translated display text as a registry identifier, persistence key, comparison value, or
  gameplay input.
- Datapack, KubeJS, and addon extension points must be able to provide their own localization keys.
- Missing translations must degrade visibly and safely without affecting simulation behavior.

## Naming

Translation keys should follow the owning namespace and content category, for example:

```text
block.projectskyblock.example_machine
item.projectskyblock.example_part
menu.projectskyblock.example_machine
message.projectskyblock.machine.blocked_output
tooltip.projectskyblock.example_part.capacity
research.projectskyblock.example_topic.title
research.projectskyblock.example_topic.description
```

Keys are contracts. Rename them deliberately and document compatibility impact where external data or
addons may reference them.

## Architecture Boundary

Localization belongs to presentation and integration layers. The simulation may report typed status
codes and values such as `BLOCKED_OUTPUT`, required temperature, or missing module role. Minecraft
adapters convert those values into translatable components for players.

The backend must not depend on Minecraft text classes merely to describe simulation state.

## Review Checklist

Before completing a feature, confirm:

- no player-visible string is hard-coded;
- translation keys are stable and consistently named;
- dynamic values are passed as arguments;
- server and client agree on structured status meaning;
- text is not used as authoritative state;
- language files and generated assets are validated;
- future translators have enough context to understand ambiguous terms.
