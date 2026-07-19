package raziel23x.projectskyblock.simulation.machine.component;

/** Immutable diagnostic snapshot for a machine-owned energy component. */
public record MachineEnergyDiagnostics(
        MachineEnergyAccess access,
        long storedEnergy,
        long capacity,
        long availableCapacity,
        long maximumReceivePerTick,
        long maximumExtractPerTick,
        long totalReceived,
        long totalExtracted,
        long changeCount) {

    public MachineEnergyDiagnostics {
        if (access == null) {
            throw new NullPointerException("access");
        }
        if (storedEnergy < 0 || capacity < 0 || availableCapacity < 0
                || maximumReceivePerTick < 0 || maximumExtractPerTick < 0
                || totalReceived < 0 || totalExtracted < 0 || changeCount < 0) {
            throw new IllegalArgumentException("energy diagnostics values must be non-negative");
        }
    }
}
