package raziel23x.projectskyblock.simulation.machine.component;

import java.util.Objects;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemKey;

/** Immutable configuration for one machine-owned inventory slot. */
public record MachineInventorySlotDefinition(
        long capacity,
        MachineInventoryAccess access,
        MachineInventorySlotRule insertionRule) {

    public MachineInventorySlotDefinition {
        if (capacity <= 0L) {
            throw new IllegalArgumentException("slot capacity must be positive");
        }
        Objects.requireNonNull(access, "access");
        Objects.requireNonNull(insertionRule, "insertionRule");
    }

    public static MachineInventorySlotDefinition unrestricted(long capacity) {
        return new MachineInventorySlotDefinition(
                capacity,
                MachineInventoryAccess.BIDIRECTIONAL,
                MachineInventorySlotRule.ACCEPT_ALL);
    }

    public boolean accepts(SimulationItemKey item) {
        return insertionRule.accepts(Objects.requireNonNull(item, "item"));
    }
}
