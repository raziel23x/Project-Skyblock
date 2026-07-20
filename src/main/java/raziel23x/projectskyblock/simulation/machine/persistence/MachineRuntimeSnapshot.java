package raziel23x.projectskyblock.simulation.machine.persistence;

import java.util.List;
import java.util.Objects;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;

/**
 * Minecraft-independent durable state required to reconstruct one machine runtime.
 *
 * <p>Diagnostics, scheduler state, dirty masks, caches, and derived values are deliberately
 * excluded. Platform codecs may encode this record using NBT, JSON, or another format without
 * making the simulation depend on that format.</p>
 */
public record MachineRuntimeSnapshot(
        int schemaVersion,
        long storedEnergy,
        long thermalEnergyMicroJoules,
        List<SimulationItemStack> inventory,
        MachineProcessingSnapshot processing) {

    public static final int CURRENT_SCHEMA_VERSION = 1;

    public MachineRuntimeSnapshot {
        if (schemaVersion <= 0 || schemaVersion > CURRENT_SCHEMA_VERSION) {
            throw new IllegalArgumentException("unsupported machine snapshot schema: " + schemaVersion);
        }
        if (storedEnergy < 0L || thermalEnergyMicroJoules < 0L) {
            throw new IllegalArgumentException("machine resource values must be non-negative");
        }
        inventory = List.copyOf(Objects.requireNonNull(inventory, "inventory"));
        inventory.forEach(stack -> Objects.requireNonNull(stack, "inventory stack"));
        Objects.requireNonNull(processing, "processing");
    }
}
