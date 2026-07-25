#!/usr/bin/env python3
"""Offline resource and packaging checks for Project Skyblock."""

from __future__ import annotations

import json
import re
import struct
import sys
from pathlib import Path
from urllib.parse import unquote

ROOT = Path(__file__).resolve().parents[1]
RESOURCES = ROOT / "src" / "main" / "resources"
ASSETS = RESOURCES / "assets" / "projectskyblock"
JAVA_ROOT = ROOT / "src" / "main" / "java"
SIMULATION_ROOT = JAVA_ROOT / "raziel23x" / "projectskyblock" / "simulation"

errors: list[str] = []


def fail(message: str) -> None:
    errors.append(message)


json_files = sorted(RESOURCES.rglob("*.json"))
parsed: dict[Path, object] = {}
for path in json_files:
    try:
        parsed[path] = json.loads(path.read_text(encoding="utf-8"))
    except Exception as exc:  # noqa: BLE001 - validator should report every parser failure
        fail(f"Invalid JSON: {path.relative_to(ROOT)}: {exc}")

for archive in RESOURCES.rglob("*.zip"):
    fail(f"Archive packaged inside runtime resources: {archive.relative_to(ROOT)}")

# Curios 1.21.x compatibility is data-driven and must remain complete while Curios stays optional.
curios_slot_path = RESOURCES / "data/projectskyblock/curios/slots/repair_gem.json"
curios_entities_path = RESOURCES / "data/projectskyblock/curios/entities/repair_gem.json"
curios_tag_path = RESOURCES / "data/curios/tags/item/repair_gem.json"
curios_icon_path = RESOURCES / "assets/projectskyblock/textures/slot/repair_gem.png"
curios_lang_path = RESOURCES / "assets/projectskyblock/lang/en_us.json"

for required_curios_resource in (
    curios_slot_path,
    curios_entities_path,
    curios_tag_path,
    curios_icon_path,
    curios_lang_path,
):
    if not required_curios_resource.exists():
        fail(
            "Missing Repair Gem Curios resource: "
            + str(required_curios_resource.relative_to(ROOT))
        )

slot_data = parsed.get(curios_slot_path)
if isinstance(slot_data, dict):
    expected_slot_fields = {
        "size": 1,
        "operation": "SET",
        "icon": "projectskyblock:slot/repair_gem",
        "add_cosmetic": False,
        "use_native_gui": True,
        "render_toggle": False,
        "drop_rule": "DEFAULT",
        "validators": ["curios:tag"],
    }
    for field, expected in expected_slot_fields.items():
        if slot_data.get(field) != expected:
            fail(
                f"Repair Gem Curios slot has invalid {field}: "
                f"{slot_data.get(field)!r}"
            )
elif curios_slot_path.exists():
    fail("Repair Gem Curios slot data is not a JSON object")

entities_data = parsed.get(curios_entities_path)
if isinstance(entities_data, dict):
    if entities_data.get("entities") != ["minecraft:player"]:
        fail("Repair Gem Curios slot must be assigned only to minecraft:player")
    if entities_data.get("slots") != ["repair_gem"]:
        fail("Repair Gem Curios entity assignment must contain repair_gem")
elif curios_entities_path.exists():
    fail("Repair Gem Curios entity data is not a JSON object")

tag_data = parsed.get(curios_tag_path)
if isinstance(tag_data, dict):
    if tag_data.get("replace") is not False:
        fail("Repair Gem Curios item tag must merge instead of replace")
    if tag_data.get("values") != ["projectskyblock:repair_gem"]:
        fail("Repair Gem Curios item tag contains unexpected values")
elif curios_tag_path.exists():
    fail("Repair Gem Curios item tag is not a JSON object")

lang_data = parsed.get(curios_lang_path)
if isinstance(lang_data, dict):
    if lang_data.get("curios.identifier.repair_gem") != "Repair Gem":
        fail("Repair Gem Curios slot localization is missing or incorrect")
elif curios_lang_path.exists():
    fail("Project Skyblock en_us language data is not a JSON object")

# The greenfield simulation engine must remain host-independent. Persistence formats are
# quarantined at explicit platform adapters and may not leak into runtime code.
for source in sorted(SIMULATION_ROOT.rglob("*.java")):
    text = source.read_text(encoding="utf-8")
    for forbidden in ("net.minecraft.", "net.neoforged.", "com.mojang."):
        if f"import {forbidden}" in text:
            fail(f"Platform import leaked into simulation engine: {source.relative_to(ROOT)}")
    for forbidden_type in ("CompoundTag", "ListTag", "NbtOps", "Tag.TAG_"):
        if forbidden_type in text:
            fail(f"Persistence type leaked into simulation engine: {source.relative_to(ROOT)} ({forbidden_type})")

# Milestone 19A inventory mutation and item-boundary contracts must remain explicit.
m19a_required_sources = {
    "atomic inventory commit result": JAVA_ROOT / "raziel23x/projectskyblock/simulation/machine/component/MachineInventoryCommitResult.java",
    "inventory transaction": JAVA_ROOT / "raziel23x/projectskyblock/simulation/machine/component/MachineInventoryTransaction.java",
    "inventory view kind": JAVA_ROOT / "raziel23x/projectskyblock/simulation/machine/component/MachineInventoryViewKind.java",
    "inventory view": JAVA_ROOT / "raziel23x/projectskyblock/simulation/machine/component/MachineInventoryView.java",
    "Minecraft item boundary failure": JAVA_ROOT / "raziel23x/projectskyblock/platform/neoforge/inventory/ItemStackBoundaryException.java",
    "Minecraft item boundary codec": JAVA_ROOT / "raziel23x/projectskyblock/platform/neoforge/inventory/MinecraftItemStackCodec.java",
    "NeoForge item handler adapter": JAVA_ROOT / "raziel23x/projectskyblock/platform/neoforge/machine/capability/EngineItemHandlerAdapter.java",
    "item handler adapter diagnostics": JAVA_ROOT / "raziel23x/projectskyblock/platform/neoforge/machine/capability/EngineItemHandlerAdapterDiagnostics.java",
}
for description, source in m19a_required_sources.items():
    if not source.exists():
        fail(f"Missing Milestone 19A {description}: {source.relative_to(ROOT)}")

inventory_component_source = (
    JAVA_ROOT
    / "raziel23x/projectskyblock/simulation/machine/component/MachineInventoryComponent.java"
)
if inventory_component_source.exists():
    inventory_component_text = inventory_component_source.read_text(encoding="utf-8")
    required_inventory_contracts = {
        "transaction opening": "public synchronized MachineInventoryTransaction beginTransaction()",
        "optimistic version check": "transaction.baseVersion() != stateVersion",
        "stale candidate rejection": "throw new ConcurrentModificationException",
        "precomputed commit counters": "long committedStateVersion = Math.addExact(stateVersion, 1L)",
        "single atomic candidate publication": "System.arraycopy(candidate, 0, stacks, 0, stacks.length)",
        "transaction commit diagnostics": "transactionCommitCount",
        "transaction conflict diagnostics": "transactionConflictCount",
    }
    for description, snippet in required_inventory_contracts.items():
        if snippet not in inventory_component_text:
            fail(f"Missing Milestone 19A inventory {description} contract")

transaction_source = m19a_required_sources["inventory transaction"]
if transaction_source.exists():
    transaction_text = transaction_source.read_text(encoding="utf-8")
    for description, snippet in {
        "isolated source snapshot": "this.originalStacks = sourceStacks.clone()",
        "isolated working snapshot": "this.workingStacks = sourceStacks.clone()",
        "explicit rollback": "public void rollback()",
        "owner-controlled commit": "return owner.commitTransaction(this)",
    }.items():
        if snippet not in transaction_text:
            fail(f"Missing Milestone 19A transaction {description} contract")

view_source = m19a_required_sources["inventory view"]
if view_source.exists():
    view_text = view_source.read_text(encoding="utf-8")
    for description, snippet in {
        "automation view": "MachineInventoryViewKind.AUTOMATION",
        "menu view": "MachineInventoryViewKind.MENU",
        "player extraction distinction": "transaction.consume(mappedSlot, requestedQuantity)",
        "automation extraction distinction": "transaction.extract(mappedSlot, requestedQuantity)",
        "cross-owner transaction rejection": "transaction.belongsTo(inventory)",
    }.items():
        if snippet not in view_text:
            fail(f"Missing Milestone 19A inventory-view {description} contract")

item_codec_source = m19a_required_sources["Minecraft item boundary codec"]
if item_codec_source.exists():
    item_codec_text = item_codec_source.read_text(encoding="utf-8")
    required_codec_contracts = {
        "typed persistent data-component codec": "DataComponentPatch.CODEC",
        "stable codec identifier": "projectskyblock:minecraft_data_components_json_v1",
        "canonical component ordering": "entries.sort(Comparator.comparing",
        "canonical JSON object ordering": ".sorted(Map.Entry.comparingByKey())",
        "registered component identifier validation": "BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component)",
        "registry-aware persistence operations": "RegistryOps.create(",
        "typed JSON operations": "JsonOps.INSTANCE",
        "bounded payload validation": "SimulationItemState.MAX_PAYLOAD_BYTES",
        "strict UTF-8 decoding": "CodingErrorAction.REPORT",
        "bounded JSON nesting": "MAX_JSON_DEPTH = 64",
        "canonical payload rejection": "data-component payload is not in canonical form",
        "lossless persistence verification": "data-component patch is not losslessly persistent",
        "transient component rejection": "transient data component cannot cross the persistent engine boundary",
    }
    for description, snippet in required_codec_contracts.items():
        if snippet not in item_codec_text:
            fail(f"Missing Milestone 19A item-codec {description} contract")
    for forbidden_codec_surface in (
        "import net.minecraft.nbt.",
        "CompoundTag",
        "ListTag",
        "NbtOps",
        "TagParser",
        "SnbtPrinterTagVisitor",
        "DataComponentPatch.STREAM_CODEC",
        "RegistryFriendlyByteBuf",
        "ConnectionType",
        "io.netty.buffer",
    ):
        if forbidden_codec_surface in item_codec_text:
            fail(
                "NBT/SNBT leaked into the Milestone 19A item boundary codec: "
                + forbidden_codec_surface
            )

item_adapter_source = m19a_required_sources["NeoForge item handler adapter"]
if item_adapter_source.exists():
    item_adapter_text = item_adapter_source.read_text(encoding="utf-8")
    required_adapter_contracts = {
        "backend transaction use": "MachineInventoryTransaction transaction",
        "simulation without commit": "if (!simulate && transaction.changed())",
        "decode before extraction commit": "decoded = codec.decode(extracted)",
        "fail-closed stale commit": "catch (ConcurrentModificationException exception)",
        "restricted inventory view": "private final MachineInventoryView view",
    }
    for description, snippet in required_adapter_contracts.items():
        if snippet not in item_adapter_text:
            fail(f"Missing Milestone 19A item-adapter {description} contract")
    for forbidden_adapter_state in ("ItemStackHandler", "NonNullList<ItemStack>"):
        if forbidden_adapter_state in item_adapter_text:
            fail(
                "NeoForge item adapter duplicates authoritative inventory state: "
                + forbidden_adapter_state
            )

m19a_required_tests = (
    ROOT / "src/test/java/raziel23x/projectskyblock/simulation/machine/component/MachineInventoryTransactionTest.java",
    ROOT / "src/test/java/raziel23x/projectskyblock/simulation/machine/component/MachineInventoryViewTest.java",
    ROOT / "src/test/java/raziel23x/projectskyblock/platform/neoforge/inventory/MinecraftItemStackCodecTest.java",
    ROOT / "src/test/java/raziel23x/projectskyblock/platform/neoforge/machine/EngineItemHandlerAdapterTest.java",
)
for test_source in m19a_required_tests:
    if not test_source.exists():
        fail(f"Missing Milestone 19A regression test: {test_source.relative_to(ROOT)}")

item_codec_test = m19a_required_tests[2]
if item_codec_test.exists():
    item_codec_test_text = item_codec_test.read_text(encoding="utf-8")
    for description, snippet in {
        "component round trip": "componentBearingStackRoundTripsExactly",
        "canonical mutation-order identity": "canonicalEncodingDoesNotDependOnComponentMutationOrder",
        "non-canonical payload rejection": "nonCanonical",
        "transient component rejection": "transientComponentsFailClosedInsteadOfDisappearing",
    }.items():
        if snippet not in item_codec_test_text:
            fail(f"Missing Milestone 19A item-codec test for {description}")

m19a_milestone_doc = (
    ROOT
    / "docs/03-engineering/milestones/BACKEND_MILESTONE_19A_INVENTORY_TRANSACTIONS_AND_ITEM_ADAPTER.md"
)
if not m19a_milestone_doc.exists():
    fail("Missing Milestone 19A engineering record")

# Milestone 19B1 combustion state and schema-3 persistence must remain explicit.
m19b1_required_sources = {
    "combustion component": JAVA_ROOT / "raziel23x/projectskyblock/simulation/machine/component/MachineCombustionComponent.java",
    "combustion diagnostics": JAVA_ROOT / "raziel23x/projectskyblock/simulation/machine/component/MachineCombustionDiagnostics.java",
    "combustion snapshot": JAVA_ROOT / "raziel23x/projectskyblock/simulation/machine/persistence/MachineCombustionSnapshot.java",
}
for description, source in m19b1_required_sources.items():
    if not source.exists():
        fail(f"Missing Milestone 19B1 {description}: {source.relative_to(ROOT)}")

combustion_source = m19b1_required_sources["combustion component"]
if combustion_source.exists():
    combustion_text = combustion_source.read_text(encoding="utf-8")
    for description, snippet in {
        "positive ignition": "if (burnUnits <= 0L)",
        "repeat ignition rejection": "combustion reservoir is already burning",
        "bounded consumption": "Math.min(requestedUnits, remainingBurnUnits)",
        "restore validation": "remainingBurnUnits > totalBurnUnits",
        "persistence/client dirty boundary": "DirtyFlag.PERSISTENCE, DirtyFlag.CLIENT_SYNC",
    }.items():
        if snippet not in combustion_text:
            fail(f"Missing Milestone 19B1 combustion {description} contract")
    if "DirtyFlag.SCHEDULER" in combustion_text:
        fail("Combustion progress must not issue redundant scheduler dirty state")

runtime_snapshot_source = (
    JAVA_ROOT
    / "raziel23x/projectskyblock/simulation/machine/persistence/MachineRuntimeSnapshot.java"
)
if runtime_snapshot_source.exists():
    snapshot_text = runtime_snapshot_source.read_text(encoding="utf-8")
    if "CURRENT_SCHEMA_VERSION = 3" not in snapshot_text:
        fail("Milestone 19B1 machine snapshot schema must be version 3")
    if "MachineCombustionSnapshot combustion" not in snapshot_text:
        fail("Milestone 19B1 combustion state is missing from machine snapshots")

nbt_codec_source = (
    JAVA_ROOT
    / "raziel23x/projectskyblock/platform/neoforge/machine/MachineRuntimeNbtCodec.java"
)
if nbt_codec_source.exists():
    nbt_codec_text = nbt_codec_source.read_text(encoding="utf-8")
    for description, snippet in {
        "schema-3 combustion write": 'root.put("Combustion", combustionTag)',
        "older-schema migration": "combustion = MachineCombustionSnapshot.EMPTY",
        "remaining burn read": 'combustionTag.getLong("RemainingBurnUnits")',
        "total burn read": 'combustionTag.getLong("TotalBurnUnits")',
    }.items():
        if snippet not in nbt_codec_text:
            fail(f"Missing Milestone 19B1 NBT {description} contract")

m19b1_test = (
    ROOT
    / "src/test/java/raziel23x/projectskyblock/simulation/machine/component/MachineCombustionComponentTest.java"
)
if not m19b1_test.exists():
    fail("Missing Milestone 19B1 combustion regression test")

m19b1_milestone_doc = (
    ROOT
    / "docs/03-engineering/milestones/BACKEND_MILESTONE_19B1_COMBUSTION_STATE_AND_PERSISTENCE.md"
)
if not m19b1_milestone_doc.exists():
    fail("Missing Milestone 19B1 engineering record")

# Milestone 19B2 must keep the Material Crusher as an engine-owned, ticker-free stress fixture.
m19b2_required_sources = {
    "crusher logic": JAVA_ROOT / "raziel23x/projectskyblock/simulation/machine/logic/crusher/MaterialCrusherLogic.java",
    "crusher legacy migration": JAVA_ROOT / "raziel23x/projectskyblock/simulation/machine/logic/crusher/MaterialCrusherLegacySnapshotMigration.java",
    "recipe port": JAVA_ROOT / "raziel23x/projectskyblock/simulation/machine/logic/crusher/MaterialCrusherRecipePort.java",
    "fuel port": JAVA_ROOT / "raziel23x/projectskyblock/simulation/machine/logic/crusher/MaterialCrusherFuelPort.java",
    "Minecraft recipe adapter": JAVA_ROOT / "raziel23x/projectskyblock/platform/neoforge/machine/crusher/MinecraftMaterialCrusherRecipePort.java",
    "Minecraft fuel adapter": JAVA_ROOT / "raziel23x/projectskyblock/platform/neoforge/machine/crusher/MinecraftMaterialCrusherFuelPort.java",
    "crusher item adapter": JAVA_ROOT / "raziel23x/projectskyblock/platform/neoforge/machine/crusher/MaterialCrusherItemHandler.java",
    "crusher FE gate": JAVA_ROOT / "raziel23x/projectskyblock/platform/neoforge/machine/crusher/MaterialCrusherEnergyStorage.java",
}
for description, source in m19b2_required_sources.items():
    if not source.exists():
        fail(f"Missing Milestone 19B2 {description}: {source.relative_to(ROOT)}")

crusher_logic_source = m19b2_required_sources["crusher logic"]
if crusher_logic_source.exists():
    crusher_logic_text = crusher_logic_source.read_text(encoding="utf-8")
    for description, snippet in {
        "maximum-output admission": "canAcceptMaximum(inventory, recipe.maximumResult())",
        "atomic completion transaction": "MachineInventoryTransaction transaction = inventory.beginTransaction()",
        "input consumption": "transaction.consume(INPUT_SLOT, 1L)",
        "primary output insertion": "transaction.store(OUTPUT_SLOT, result.primary())",
        "byproduct output insertion": "transaction.store(BYPRODUCT_SLOT, result.byproduct())",
        "hybrid source policy": "consumePower(components, settings)",
        "combustion ownership": "components.combustion()",
        "stable output blocking": "SimulationResult.blocked(BLOCKED_OUTPUT)",
        "legacy process adoption": "processing.processId().equals(LEGACY_PROCESS_ID)",
    }.items():
        if snippet not in crusher_logic_text:
            fail(f"Missing Milestone 19B2 crusher {description} contract")

crusher_block_entity = (
    JAVA_ROOT / "raziel23x/projectskyblock/blockentity/MaterialCrusherBlockEntity.java"
)
if not crusher_block_entity.exists():
    fail("Missing Material Crusher block entity")
else:
    crusher_block_entity_text = crusher_block_entity.read_text(encoding="utf-8")
    required_shell_contracts = {
        "engine block-entity bridge": "extends EngineMachineBlockEntity",
        "composed machine runtime": "new MachineRuntime(",
        "legacy snapshot hook": "readLegacyMachineSnapshot(",
        "legacy normalization helper": "MaterialCrusherLegacySnapshotMigration.migrate(",
        "engine item views": "EngineItemHandlerAdapter",
        "top input view": "Material Crusher top input",
        "side fuel view": "Material Crusher side fuel",
        "bottom output view": "Material Crusher bottom output",
        "engine energy adapter": "EngineEnergyStorageAdapter",
    }
    for description, snippet in required_shell_contracts.items():
        if snippet not in crusher_block_entity_text:
            fail(f"Missing Milestone 19B2 platform-shell {description} contract")
    for forbidden_state in (
        "new CrusherInventory(",
        "new CrusherEnergyStorage(",
        "private int progress;",
        "private int burnTimeRemaining;",
        "private int burnTimeTotal;",
        "serverTick(",
    ):
        if forbidden_state in crusher_block_entity_text:
            fail("Material Crusher still owns legacy authority or ticking: " + forbidden_state)

crusher_block_source = JAVA_ROOT / "raziel23x/projectskyblock/block/MaterialCrusherBlock.java"
if crusher_block_source.exists():
    crusher_block_text = crusher_block_source.read_text(encoding="utf-8")
    if "getTicker(" in crusher_block_text or "MaterialCrusherBlockEntity::serverTick" in crusher_block_text:
        fail("Material Crusher still registers an independent block-entity ticker")

for retired_source in (
    JAVA_ROOT / "raziel23x/projectskyblock/machine/crusher/CrusherEnergyStorage.java",
    JAVA_ROOT / "raziel23x/projectskyblock/machine/crusher/CrusherInventory.java",
    JAVA_ROOT / "raziel23x/projectskyblock/machine/crusher/CrusherPowerSource.java",
    JAVA_ROOT / "raziel23x/projectskyblock/machine/crusher/CrusherSidedItemHandler.java",
):
    if retired_source.exists():
        fail(f"Retired split-authority crusher source remains active: {retired_source.relative_to(ROOT)}")

level_manager_source = (
    JAVA_ROOT / "raziel23x/projectskyblock/platform/neoforge/machine/EngineMachineLevelManager.java"
)
project_entry_source = JAVA_ROOT / "raziel23x/projectskyblock/ProjectSkyblock.java"
if level_manager_source.exists():
    level_manager_text = level_manager_source.read_text(encoding="utf-8")
    if "public static void requestAllWork(MinecraftServer server)" not in level_manager_text:
        fail("Milestone 19B2 datapack-reload wake entry point is missing")
if project_entry_source.exists():
    project_entry_text = project_entry_source.read_text(encoding="utf-8")
    for snippet in (
        "ProjectSkyblock::onDatapackSync",
        "event.getPlayer() == null",
        "EngineMachineLevelManager.requestAllWork",
    ):
        if snippet not in project_entry_text:
            fail("Milestone 19B2 datapack-reload reevaluation contract is missing: " + snippet)

m19b2_required_tests = (
    ROOT / "src/test/java/raziel23x/projectskyblock/simulation/machine/logic/crusher/MaterialCrusherLogicTest.java",
    ROOT / "src/test/java/raziel23x/projectskyblock/simulation/machine/logic/crusher/MaterialCrusherLegacySnapshotMigrationTest.java",
    ROOT / "src/test/java/raziel23x/projectskyblock/platform/neoforge/machine/crusher/MaterialCrusherItemHandlerTest.java",
    ROOT / "src/test/java/raziel23x/projectskyblock/platform/neoforge/machine/crusher/MaterialCrusherEnergyStorageTest.java",
)
for test_source in m19b2_required_tests:
    if not test_source.exists():
        fail(f"Missing Milestone 19B2 regression test: {test_source.relative_to(ROOT)}")

m19b2_milestone_doc = (
    ROOT
    / "docs/03-engineering/milestones/BACKEND_MILESTONE_19B2_MATERIAL_CRUSHER_ENGINE_MIGRATION.md"
)
if not m19b2_milestone_doc.exists():
    fail("Missing Milestone 19B2 engineering record")

# Milestone 20A typed transport topology, reservations, fairness, and sleeping must remain explicit.
m20a_required_sources = {
    "transport channel id": JAVA_ROOT / "raziel23x/projectskyblock/simulation/transport/TransportChannelId.java",
    "transport node id": JAVA_ROOT / "raziel23x/projectskyblock/simulation/transport/TransportNodeId.java",
    "transport profile": JAVA_ROOT / "raziel23x/projectskyblock/simulation/transport/TransportProfile.java",
    "transport connection": JAVA_ROOT / "raziel23x/projectskyblock/simulation/transport/TransportConnection.java",
    "typed transport topology": JAVA_ROOT / "raziel23x/projectskyblock/simulation/transport/TransportTopology.java",
    "typed transport route": JAVA_ROOT / "raziel23x/projectskyblock/simulation/transport/TransportRoute.java",
    "shared-edge reservations": JAVA_ROOT / "raziel23x/projectskyblock/simulation/transport/TransportStepReservations.java",
    "dispatch planner": JAVA_ROOT / "raziel23x/projectskyblock/simulation/transport/TransportDispatchPlanner.java",
    "dispatch plan": JAVA_ROOT / "raziel23x/projectskyblock/simulation/transport/TransportDispatchPlan.java",
    "scheduled transport participant": JAVA_ROOT / "raziel23x/projectskyblock/simulation/transport/TransportNetworkParticipant.java",
    "transport runtime state": JAVA_ROOT / "raziel23x/projectskyblock/simulation/transport/TransportNetworkRuntimeState.java",
    "per-channel step result": JAVA_ROOT / "raziel23x/projectskyblock/simulation/transport/TransportChannelStepResult.java",
    "multi-channel step result": JAVA_ROOT / "raziel23x/projectskyblock/simulation/transport/TransportNetworkStepResult.java",
}
for description, source in m20a_required_sources.items():
    if not source.exists():
        fail(f"Missing Milestone 20A {description}: {source.relative_to(ROOT)}")

transport_profile_source = m20a_required_sources["transport profile"]
if transport_profile_source.exists():
    transport_profile_text = transport_profile_source.read_text(encoding="utf-8")
    for description, snippet in {
        "arbitrary typed channel map": "NavigableMap<TransportChannelId, TransportChannelProfile>",
        "independent native-unit lookup": "maximumUnitsPerStep(TransportChannelId channelId)",
        "duplicate-channel rejection": "transport profile contains duplicate channel",
    }.items():
        if snippet not in transport_profile_text:
            fail(f"Missing Milestone 20A transport-profile {description} contract")

transport_topology_source = m20a_required_sources["typed transport topology"]
if transport_topology_source.exists():
    transport_topology_text = transport_topology_source.read_text(encoding="utf-8")
    for description, snippet in {
        "channel-filtered route discovery": "if (!connection.supports(channelId))",
        "stable node ordering": "new TreeMap<>()",
        "conflicting connection rejection": "transport connection already exists with a different profile",
        "channel-filtered components": "connectedComponentsForChannel",
    }.items():
        if snippet not in transport_topology_text:
            fail(f"Missing Milestone 20A topology {description} contract")

transport_reservations_source = m20a_required_sources["shared-edge reservations"]
if transport_reservations_source.exists():
    transport_reservations_text = transport_reservations_source.read_text(encoding="utf-8")
    for description, snippet in {
        "captured topology revision": "topologyRevision = topology.revision()",
        "stale topology rejection": "topology.revision() != topologyRevision",
        "all-route validation before mutation": "validateRoute(route);",
        "checked shared-edge decrement": "Math.subtractExact(remaining, reserved)",
    }.items():
        if snippet not in transport_reservations_text:
            fail(f"Missing Milestone 20A reservation {description} contract")

transport_planner_source = m20a_required_sources["dispatch planner"]
if transport_planner_source.exists():
    transport_planner_text = transport_planner_source.read_text(encoding="utf-8")
    for description, snippet in {
        "stable request sorting": "stableRequests.sort(Comparator.comparing(TransportDispatchRequest::id))",
        "rotating fairness": "fairnessSequence % stableRequests.size()",
        "shared-edge reservation use": "new TransportStepReservations(topology, channelId)",
        "unroutable accounting": "TransportDispatchStatus.UNROUTABLE",
        "capacity deferral accounting": "TransportDispatchStatus.CAPACITY_DEFERRED",
    }.items():
        if snippet not in transport_planner_text:
            fail(f"Missing Milestone 20A dispatch {description} contract")

transport_participant_source = m20a_required_sources["scheduled transport participant"]
if transport_participant_source.exists():
    transport_participant_text = transport_participant_source.read_text(encoding="utf-8")
    for description, snippet in {
        "bounded execution": "budget.tryConsume(EXECUTION_WORK_UNITS)",
        "progress continuation": "if (result.madeProgress())",
        "stable stall confirmation": "state.confirmsStableStall(result.stateFingerprint())",
        "event-driven sleeping": "return SimulationResult.sleep()",
    }.items():
        if snippet not in transport_participant_text:
            fail(f"Missing Milestone 20A scheduler {description} contract")

transport_step_result_source = m20a_required_sources["multi-channel step result"]
if transport_step_result_source.exists():
    transport_step_result_text = transport_step_result_source.read_text(encoding="utf-8")
    if "Native units are never summed across channels" not in transport_step_result_text:
        fail("Milestone 20A diagnostics must preserve native units per channel")
    for forbidden_aggregate in ("totalRequestedUnits", "totalCommittedUnits", "totalTransportUnits"):
        if forbidden_aggregate in transport_step_result_text:
            fail("Milestone 20A transport diagnostics aggregate incompatible channel units")

m20a_required_tests = (
    ROOT / "src/test/java/raziel23x/projectskyblock/simulation/transport/TransportTopologyTest.java",
    ROOT / "src/test/java/raziel23x/projectskyblock/simulation/transport/TransportStepReservationsTest.java",
    ROOT / "src/test/java/raziel23x/projectskyblock/simulation/transport/TransportDispatchPlannerTest.java",
    ROOT / "src/test/java/raziel23x/projectskyblock/simulation/transport/TransportNetworkParticipantTest.java",
)
for test_source in m20a_required_tests:
    if not test_source.exists():
        fail(f"Missing Milestone 20A regression test: {test_source.relative_to(ROOT)}")

m20a_milestone_doc = (
    ROOT
    / "docs/03-engineering/milestones/BACKEND_MILESTONE_20A_TYPED_TRANSPORT_NETWORK_CORE.md"
)
if not m20a_milestone_doc.exists():
    fail("Missing Milestone 20A engineering record")

# Keep the unavoidable Minecraft NBT surface explicit. Legacy prototype block entities are
# temporary stress fixtures; new NBT-bearing gameplay files must not appear unnoticed.
allowed_nbt_sources = {
    Path("src/main/java/raziel23x/projectskyblock/blockentity/MaterialCrusherBlockEntity.java"),
    Path("src/main/java/raziel23x/projectskyblock/blockentity/ResourceGeneratorBlockEntity.java"),
    Path("src/main/java/raziel23x/projectskyblock/blockentity/ThermalGeneratorBlockEntity.java"),
    Path("src/main/java/raziel23x/projectskyblock/platform/neoforge/machine/EngineMachineBlockEntity.java"),
    Path("src/main/java/raziel23x/projectskyblock/platform/neoforge/machine/MachineRuntimeNbtCodec.java"),
}
for source in sorted(JAVA_ROOT.rglob("*.java")):
    text = source.read_text(encoding="utf-8")
    if "import net.minecraft.nbt." not in text:
        continue
    relative = source.relative_to(ROOT)
    if relative not in allowed_nbt_sources:
        fail(f"Unexpected NBT-bearing source outside quarantine: {relative}")

# Project Skyblock custom data types use a mod-specific root so unrelated mods cannot collide
# with a generic `materials/` or `processing_routes/` folder during a fail-closed reload.
for legacy_directory in (
    RESOURCES / "data" / "projectskyblock" / "materials",
    RESOURCES / "data" / "projectskyblock" / "processing_routes",
):
    if legacy_directory.exists():
        fail(f"Legacy generic custom-data directory remains: {legacy_directory.relative_to(ROOT)}")

wrapper_properties = ROOT / "gradle" / "wrapper" / "gradle-wrapper.properties"
if not wrapper_properties.exists():
    fail("Missing Gradle wrapper properties")
elif "distributionSha256Sum=" not in wrapper_properties.read_text(encoding="utf-8"):
    fail("Gradle wrapper distribution checksum is not pinned")

gradlew = ROOT / "gradlew"
if not gradlew.exists():
    fail("Missing Gradle wrapper script")
elif not gradlew.stat().st_mode & 0o111:
    fail("gradlew is not executable")


build_gradle = ROOT / "build.gradle"
if not build_gradle.exists():
    fail("Missing build.gradle")
else:
    build_text = build_gradle.read_text(encoding="utf-8")
    required_build_contracts = {
        "standalone run directory": "gameDirectory = project.file('run-standalone')",
        "integration run directory": "gameDirectory = project.file('run-integration')",
        "integration-only helper classpath": "integrationHelperRuntime files(fileTree(",
        "typed release artifact isolation task": "abstract class VerifyReleaseArtifactTask extends DefaultTask",
        "declared release archive input": "abstract RegularFileProperty getReleaseJar()",
        "release artifact isolation registration": "tasks.register('verifyReleaseArtifact', VerifyReleaseArtifactTask)",
        "provider-backed release archive": "releaseJar.set(releaseJarTask.flatMap { it.archiveFile })",
        "native integration validation task": "abstract class ValidateIntegrationEnvironmentTask extends DefaultTask",
        "native integration validation registration": "'validateIntegrationEnvironment',\n        ValidateIntegrationEnvironmentTask",
        "integration SHA-512 verification": "MessageDigest.getInstance('SHA-512')",
        "dedicated integration source set": "integrationRun {",
        "isolated integration helper configuration": "integrationHelperRuntime {",
        "integration runtime classpath wiring": "sourceSets.integrationRun.runtimeClasspath += configurations.integrationHelperRuntime",
        "standalone source set binding": "sourceSet = sourceSets.main",
        "integration source set binding": "sourceSet = sourceSets.integrationRun",
        "integration launch validation gate": "dependsOn validateIntegrationEnvironmentTask",
    }
    for description, snippet in required_build_contracts.items():
        if snippet not in build_text:
            fail(f"Missing {description} contract in build.gradle")
    if "gameDirectory = project.file('run-clean')" in build_text:
        fail("Legacy shared run-clean client directory remains in build.gradle")
    for forbidden_execution_access in (
        "tasks.named('jar').get().archiveFile",
        "zipTree(releaseJar)",
        "commandLine 'python', 'tools/validate_integration_environment.py'",
        "eachWithIndex",
        "integrationClientAdditionalRuntimeClasspath",
        "runtimeClasspath.extendsFrom localRuntime",
    ):
        if forbidden_execution_access in build_text:
            fail(
                "Configuration-cache-unsafe release verification remains in build.gradle: "
                + forbidden_execution_access
            )

integration_manifest = ROOT / "dev" / "integration-mods.json"
if not integration_manifest.exists():
    fail("Missing tracked integration-mod manifest")
else:
    try:
        integration_data = json.loads(integration_manifest.read_text(encoding="utf-8"))
    except Exception as exc:  # noqa: BLE001 - validator should report parser failures
        fail(f"Invalid integration-mod manifest: {exc}")
    else:
        if not isinstance(integration_data, dict) or integration_data.get("schema") != 1:
            fail("Unsupported integration-mod manifest schema")
        if integration_data.get("minecraft_version") != "1.21.1":
            fail("Integration-mod manifest targets the wrong Minecraft version")
        if integration_data.get("loader") != "neoforge":
            fail("Integration-mod manifest targets the wrong loader")
        projects = integration_data.get("projects")
        if not isinstance(projects, list) or not projects:
            fail("Integration-mod manifest contains no projects")
        else:
            slugs: list[str] = []
            for entry in projects:
                if (
                    not isinstance(entry, dict)
                    or not isinstance(entry.get("slug"), str)
                    or not isinstance(entry.get("role"), str)
                    or not isinstance(entry.get("allow_prerelease"), bool)
                ):
                    fail("Integration-mod manifest contains an invalid project entry")
                    continue
                slugs.append(entry["slug"])
            duplicates = sorted({slug for slug in slugs if slugs.count(slug) > 1})
            if duplicates:
                fail("Duplicate integration-mod projects: " + ", ".join(duplicates))

for required_tool in (
    ROOT / "tools" / "setup_integration_mods.ps1",
    ROOT / "tools" / "restore_local_validation_state.ps1",
):
    if not required_tool.exists():
        fail(f"Missing developer validation tool: {required_tool.relative_to(ROOT)}")

integration_setup = ROOT / "tools" / "setup_integration_mods.ps1"
if integration_setup.exists():
    integration_setup_text = integration_setup.read_text(encoding="utf-8")
    if "function ConvertTo-FlatObjectArray" not in integration_setup_text:
        fail("Integration setup must normalize Windows PowerShell REST array responses")
    if "function Get-DatePublishedSortKey" not in integration_setup_text:
        fail("Integration setup must validate Modrinth publication dates")
    if "Sort-Object { [DateTimeOffset]$_.date_published }" in integration_setup_text:
        fail("Integration setup contains the unsafe PowerShell 5.1 date-sort pattern")

ignore_file = ROOT / ".gitignore"
if not ignore_file.exists():
    fail("Missing .gitignore")
else:
    ignore_text = ignore_file.read_text(encoding="utf-8")
    for required_ignore in (
        "dev/mods/**/*.jar",
        "dev/integration-mods.lock.json",
        "run-standalone/",
        "run-integration/",
    ):
        if required_ignore not in ignore_text:
            fail(f"Missing development isolation rule in .gitignore: {required_ignore}")


def check_model_ref(reference: str, source: Path) -> None:
    if reference.startswith("minecraft:") or reference.startswith("builtin/"):
        return
    namespace, _, model = reference.partition(":")
    if not model:
        model = namespace
        namespace = "minecraft"
    if namespace != "projectskyblock":
        return
    target = RESOURCES / "assets" / namespace / "models" / f"{model}.json"
    if not target.exists():
        fail(f"Missing model {reference} referenced by {source.relative_to(ROOT)}")


for path, data in parsed.items():
    if not isinstance(data, dict):
        continue
    relative = path.relative_to(ASSETS) if ASSETS in path.parents else None
    if relative and relative.parts and relative.parts[0] == "blockstates":
        queue = [data]
        while queue:
            value = queue.pop()
            if isinstance(value, dict):
                if isinstance(value.get("model"), str):
                    check_model_ref(value["model"], path)
                queue.extend(value.values())
            elif isinstance(value, list):
                queue.extend(value)
    if relative and relative.parts and relative.parts[0] == "models":
        parent = data.get("parent")
        if isinstance(parent, str):
            check_model_ref(parent, path)
        textures = data.get("textures", {})
        if isinstance(textures, dict):
            for texture in textures.values():
                if not isinstance(texture, str) or texture.startswith("#") or texture.startswith("minecraft:"):
                    continue
                namespace, _, name = texture.partition(":")
                if namespace == "projectskyblock":
                    target = RESOURCES / "assets" / namespace / "textures" / f"{name}.png"
                    if not target.exists():
                        fail(f"Missing texture {texture} referenced by {path.relative_to(ROOT)}")

for png in sorted(RESOURCES.rglob("*.png")):
    raw = png.read_bytes()
    if len(raw) < 24 or raw[:8] != b"\x89PNG\r\n\x1a\n":
        fail(f"Invalid PNG signature: {png.relative_to(ROOT)}")
        continue
    width, height = struct.unpack(">II", raw[16:24])
    if png == curios_icon_path and (width, height) != (16, 16):
        fail(
            "Repair Gem Curios slot icon must be 16x16: "
            f"{png.relative_to(ROOT)} ({width}x{height})"
        )
    meta = png.with_suffix(png.suffix + ".mcmeta")
    if meta.exists() and height % width != 0:
        fail(f"Animated texture height is not a multiple of width: {png.relative_to(ROOT)} ({width}x{height})")


# Documentation manifest and local-link integrity.
docs_root = ROOT / "docs"
manifest = docs_root / "MANIFEST.md"
doc_files = sorted(path.relative_to(docs_root).as_posix() for path in docs_root.rglob("*.md"))
if not manifest.exists():
    fail("Missing docs/MANIFEST.md")
else:
    manifest_text = manifest.read_text(encoding="utf-8")
    count_match = re.search(r"^Markdown files: (\d+)$", manifest_text, re.MULTILINE)
    if not count_match or int(count_match.group(1)) != len(doc_files):
        fail(f"Documentation manifest count does not match {len(doc_files)} Markdown files")
    listed = re.findall(r"^- `([^`]+\.md)`$", manifest_text, re.MULTILINE)
    if set(listed) != set(doc_files):
        missing = sorted(set(doc_files) - set(listed))
        stale = sorted(set(listed) - set(doc_files))
        if missing:
            fail("Documentation manifest missing: " + ", ".join(missing))
        if stale:
            fail("Documentation manifest contains stale entries: " + ", ".join(stale))

markdown_files = sorted(ROOT.rglob("*.md"))
link_pattern = re.compile(r"\[[^\]]*]\(([^)]+)\)")
for markdown in markdown_files:
    text = markdown.read_text(encoding="utf-8")
    for raw_target in link_pattern.findall(text):
        target = raw_target.strip().split(maxsplit=1)[0].strip("<>")
        if not target or target.startswith(("#", "http://", "https://", "mailto:")):
            continue
        path_part = unquote(target.split("#", 1)[0])
        if not path_part:
            continue
        resolved = (markdown.parent / path_part).resolve()
        try:
            resolved.relative_to(ROOT.resolve())
        except ValueError:
            fail(f"Markdown link escapes repository: {markdown.relative_to(ROOT)} -> {target}")
            continue
        if not resolved.exists():
            fail(f"Broken Markdown link: {markdown.relative_to(ROOT)} -> {target}")

if errors:
    print("Validation failed:")
    for error in errors:
        print(f" - {error}")
    sys.exit(1)

print(f"Validation passed: {len(json_files)} JSON files and {len(list(RESOURCES.rglob('*.png')))} PNG files checked.")
