package raziel23x.projectskyblock.simulation.machine.component;

/** Immutable diagnostic snapshot for a machine-owned energy component. */
public record MachineEnergyDiagnostics(
        MachineEnergyAccess access,
        long storedEnergy,
        long capacity,
        long availableCapacity,
        long maximumReceivePerOperation,
        long maximumExtractPerOperation,
        long totalReceived,
        long totalExtracted,
        long changeCount,
        long stateVersion,
        long transactionCommitCount,
        long transactionConflictCount) {

    public MachineEnergyDiagnostics {
        if (access == null) {
            throw new NullPointerException("access");
        }
        if (storedEnergy < 0L
                || capacity < 0L
                || availableCapacity < 0L
                || maximumReceivePerOperation < 0L
                || maximumExtractPerOperation < 0L
                || totalReceived < 0L
                || totalExtracted < 0L
                || changeCount < 0L
                || stateVersion < 0L
                || transactionCommitCount < 0L
                || transactionConflictCount < 0L) {
            throw new IllegalArgumentException("energy diagnostics values must be non-negative");
        }
        if (storedEnergy > capacity || availableCapacity != capacity - storedEnergy) {
            throw new IllegalArgumentException("energy diagnostics capacity accounting is inconsistent");
        }
    }
}
