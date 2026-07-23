package raziel23x.projectskyblock.platform.neoforge.machine;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemKey;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemState;
import raziel23x.projectskyblock.simulation.machine.component.MachineProcessingStatus;
import raziel23x.projectskyblock.simulation.machine.persistence.MachineProcessingSnapshot;
import raziel23x.projectskyblock.simulation.machine.persistence.MachineRuntimeSnapshot;

/** NeoForge/Minecraft adapter for the backend-owned machine persistence contract. */
public final class MachineRuntimeNbtCodec {
    public static final String ROOT_KEY = "EngineRuntime";

    private MachineRuntimeNbtCodec() {
    }

    public static void write(CompoundTag parent, MachineRuntimeSnapshot snapshot) {
        CompoundTag root = new CompoundTag();
        root.putInt("Schema", snapshot.schemaVersion());
        root.putLong("Energy", snapshot.storedEnergy());
        root.putLong("ThermalEnergy", snapshot.thermalEnergyMicroJoules());

        ListTag inventory = new ListTag();
        for (SimulationItemStack stack : snapshot.inventory()) {
            CompoundTag encoded = new CompoundTag();
            if (!stack.isEmpty()) {
                encoded.putString("Item", stack.item().serializedName());
                encoded.putLong("Quantity", stack.quantity());
                encoded.putLong("Maximum", stack.maximumStackSize());
                if (stack.item().hasState()) {
                    encoded.putString("StateCodec", stack.item().state().codecId());
                    encoded.putByteArray("StatePayload", stack.item().state().payload());
                }
            }
            inventory.add(encoded);
        }
        root.put("Inventory", inventory);

        MachineProcessingSnapshot processing = snapshot.processing();
        CompoundTag processTag = new CompoundTag();
        processTag.putString("Status", processing.status().name());
        processTag.putString("ProcessId", processing.processId());
        processTag.putLong("CompletedUnits", processing.completedUnits());
        processTag.putLong("RequiredUnits", processing.requiredUnits());
        processTag.putString("BlockedReason", processing.blockedReason());
        processTag.putLong("CompletedProcesses", processing.completedProcesses());
        root.put("Processing", processTag);
        parent.put(ROOT_KEY, root);
    }

    public static MachineRuntimeSnapshot read(CompoundTag parent) {
        if (!parent.contains(ROOT_KEY, Tag.TAG_COMPOUND)) {
            throw new IllegalArgumentException("machine runtime tag is missing");
        }
        CompoundTag root = parent.getCompound(ROOT_KEY);
        requireTag(root, "Schema", Tag.TAG_INT);
        requireTag(root, "Energy", Tag.TAG_LONG);
        requireTag(root, "ThermalEnergy", Tag.TAG_LONG);
        requireTag(root, "Inventory", Tag.TAG_LIST);
        requireTag(root, "Processing", Tag.TAG_COMPOUND);
        int schema = root.getInt("Schema");
        if (schema <= 0 || schema > MachineRuntimeSnapshot.CURRENT_SCHEMA_VERSION) {
            throw new IllegalArgumentException("unsupported machine snapshot schema: " + schema);
        }

        ListTag encodedInventory = root.getList("Inventory", Tag.TAG_COMPOUND);
        List<SimulationItemStack> inventory = new ArrayList<>(encodedInventory.size());
        for (int index = 0; index < encodedInventory.size(); index++) {
            CompoundTag encoded = encodedInventory.getCompound(index);
            if (!encoded.contains("Item", Tag.TAG_STRING)) {
                if (!encoded.isEmpty()) {
                    throw new IllegalArgumentException("empty inventory slot contains unexpected data at index " + index);
                }
                inventory.add(SimulationItemStack.empty());
                continue;
            }
            requireTag(encoded, "Quantity", Tag.TAG_LONG);
            requireTag(encoded, "Maximum", Tag.TAG_LONG);
            SimulationItemKey item = SimulationItemKey.parse(encoded.getString("Item"));
            boolean hasStateCodec = encoded.contains("StateCodec", Tag.TAG_STRING);
            boolean hasStatePayload = encoded.contains("StatePayload", Tag.TAG_BYTE_ARRAY);
            if (hasStateCodec != hasStatePayload) {
                throw new IllegalArgumentException("stateful item must contain both codec and payload");
            }
            if (schema < 2 && hasStateCodec) {
                throw new IllegalArgumentException("schema 1 item unexpectedly contains opaque state");
            }
            if (schema >= 2 && hasStateCodec) {
                item = item.withState(SimulationItemState.opaque(
                        encoded.getString("StateCodec"),
                        encoded.getByteArray("StatePayload")));
            }
            inventory.add(SimulationItemStack.of(
                    item,
                    encoded.getLong("Quantity"),
                    encoded.getLong("Maximum")));
        }

        CompoundTag processTag = root.getCompound("Processing");
        requireTag(processTag, "Status", Tag.TAG_STRING);
        requireTag(processTag, "ProcessId", Tag.TAG_STRING);
        requireTag(processTag, "CompletedUnits", Tag.TAG_LONG);
        requireTag(processTag, "RequiredUnits", Tag.TAG_LONG);
        requireTag(processTag, "BlockedReason", Tag.TAG_STRING);
        requireTag(processTag, "CompletedProcesses", Tag.TAG_LONG);
        MachineProcessingStatus status;
        try {
            status = MachineProcessingStatus.valueOf(processTag.getString("Status"));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("unknown processing status", exception);
        }
        MachineProcessingSnapshot processing = new MachineProcessingSnapshot(
                status,
                processTag.getString("ProcessId"),
                processTag.getLong("CompletedUnits"),
                processTag.getLong("RequiredUnits"),
                processTag.getString("BlockedReason"),
                processTag.getLong("CompletedProcesses"));
        return new MachineRuntimeSnapshot(
                MachineRuntimeSnapshot.CURRENT_SCHEMA_VERSION,
                root.getLong("Energy"),
                root.getLong("ThermalEnergy"),
                inventory,
                processing);
    }

    private static void requireTag(CompoundTag tag, String key, int expectedType) {
        if (!tag.contains(key, expectedType)) {
            throw new IllegalArgumentException("machine runtime field '" + key + "' is missing or has the wrong type");
        }
    }
}
