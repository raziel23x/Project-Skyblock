package raziel23x.projectskyblock.platform.neoforge.machine;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemKey;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;
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
        int schema = root.getInt("Schema");

        ListTag encodedInventory = root.getList("Inventory", Tag.TAG_COMPOUND);
        List<SimulationItemStack> inventory = new ArrayList<>(encodedInventory.size());
        for (int index = 0; index < encodedInventory.size(); index++) {
            CompoundTag encoded = encodedInventory.getCompound(index);
            if (!encoded.contains("Item", Tag.TAG_STRING)) {
                inventory.add(SimulationItemStack.empty());
                continue;
            }
            inventory.add(SimulationItemStack.of(
                    SimulationItemKey.parse(encoded.getString("Item")),
                    encoded.getLong("Quantity"),
                    encoded.getLong("Maximum")));
        }

        CompoundTag processTag = root.getCompound("Processing");
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
                schema,
                root.getLong("Energy"),
                root.getLong("ThermalEnergy"),
                inventory,
                processing);
    }
}
