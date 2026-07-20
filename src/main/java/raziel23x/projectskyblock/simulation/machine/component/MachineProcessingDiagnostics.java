package raziel23x.projectskyblock.simulation.machine.component;

/** Immutable diagnostic snapshot for a machine processing component. */
public record MachineProcessingDiagnostics(
        MachineProcessingStatus status,
        String processId,
        long completedUnits,
        long requiredUnits,
        String blockedReason,
        long completedProcesses,
        long changeCount) {

    public MachineProcessingDiagnostics {
        if (status == null) {
            throw new NullPointerException("status");
        }
        if (processId == null) {
            throw new NullPointerException("processId");
        }
        if (blockedReason == null) {
            throw new NullPointerException("blockedReason");
        }
        if (completedUnits < 0L || requiredUnits < 0L || completedProcesses < 0L || changeCount < 0L) {
            throw new IllegalArgumentException("processing diagnostic counters must be non-negative");
        }
    }

    public boolean active() {
        return status != MachineProcessingStatus.IDLE;
    }

    public double progressFraction() {
        if (requiredUnits == 0L) {
            return 0.0D;
        }
        return Math.min(1.0D, (double) completedUnits / (double) requiredUnits);
    }
}
