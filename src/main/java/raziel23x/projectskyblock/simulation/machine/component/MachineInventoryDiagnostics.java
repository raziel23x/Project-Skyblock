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
        long stateVersion,
        long transactionCommitCount,
        long transactionConflictCount,
        List<MachineInventorySlotSnapshot> slots) {

    public MachineInventoryDiagnostics {
        if (slotCount < 0 || occupiedSlotCount < 0 || occupiedSlotCount > slotCount
                || totalQuantity < 0L || totalInserted < 0L || totalExtracted < 0L
                || changeCount < 0L || stateVersion < 0L || transactionCommitCount < 0L
                || transactionConflictCount < 0L) {
            throw new IllegalArgumentException("inventory diagnostics values are invalid");
        }
        slots = List.copyOf(slots);
        if (slots.size() != slotCount) {
            throw new IllegalArgumentException("slot snapshot count must match slot count");
        }
    }
}
