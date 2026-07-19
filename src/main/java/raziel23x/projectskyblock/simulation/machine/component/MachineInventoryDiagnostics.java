package raziel23x.projectskyblock.simulation.machine.component;

import java.util.List;

/** Immutable diagnostic snapshot for a machine-owned inventory component. */
public record MachineInventoryDiagnostics(
        int slotCount,
        int occupiedSlotCount,
        long totalQuantity,
        long totalInserted,
        long totalExtracted,
        long changeCount,
        List<MachineInventorySlotSnapshot> slots) {

    public MachineInventoryDiagnostics {
        if (slotCount < 0 || occupiedSlotCount < 0 || occupiedSlotCount > slotCount
                || totalQuantity < 0 || totalInserted < 0 || totalExtracted < 0 || changeCount < 0) {
            throw new IllegalArgumentException("inventory diagnostics values are invalid");
        }
        slots = List.copyOf(slots);
        if (slots.size() != slotCount) {
            throw new IllegalArgumentException("slot snapshot count must match slot count");
        }
    }
}
