# Integration Contract

Every compatibility module should follow these rules.

1. Never require another gameplay mod for Project Skyblock core runtime.
2. Detect integrations dynamically and keep optional classes outside unconditional load paths.
3. Use the stable Project Skyblock public API when it exists; never reach into engine internals.
4. Treat Minecraft, NeoForge capabilities, KubeJS, Jade, The One Probe, JEI, CraftTweaker, Curios,
   addons, and third-party mods as adapters or consumers—not owners of authoritative state.
5. Use common tags and documented data contracts before hard-coded item identities.
6. Keep integrations modular and test them both present and absent.
7. Preserve normal Project Skyblock behavior when no integrations are installed.
8. Document every compatibility layer, its version matrix, and its failure behavior.
9. Avoid duplicate mechanics when another mod already solves the presentation or compatibility
   problem without changing Project Skyblock's simulation ownership.
10. Fail closed at publication or mutation boundaries; never expose partial integration state.

KubeJS is one important consumer of the future public API, not the API's owner and not a required
core dependency.


## Curios Repair Gem Boundary

- Curios remains an optional runtime integration.
- Project Skyblock registers the dedicated `repair_gem` slot, player assignment,
  item acceptance, slot icon, and localization through Curios 1.21.x datapack resources.
- The server-side adapter uses guarded reflection only to inspect equipped stacks.
- The Repair Gem's authoritative repair behavior remains Project Skyblock-owned and
  continues to work from the ordinary inventory when Curios is absent.
