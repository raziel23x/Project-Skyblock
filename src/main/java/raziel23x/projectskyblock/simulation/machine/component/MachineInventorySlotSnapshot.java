package raziel23x.projectskyblock.simulation.machine.component;

import java.util.Objects;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;

/** Immutable serialization-friendly snapshot of one machine inventory slot. */
public record MachineInventorySlotSnapshot(
        int slot,
        long capacity,
        MachineInventoryAccess access,
        SimulationItemStack stack) {

    public MachineInventorySlotSnapshot {
        if (slot < 0) {
            throw new IllegalArgumentException("slot must be non-negative");
        }
        if (capacity <= 0L) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        Objects.requireNonNull(access, "access");
        Objects.requireNonNull(stack, "stack");
        if (!stack.isEmpty() && stack.quantity() > Math.min(capacity, stack.maximumStackSize())) {
            throw new IllegalArgumentException("stack exceeds effective slot capacity");
        }
    }
}
