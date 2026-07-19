package raziel23x.projectskyblock.simulation.energy;

/** Immutable diagnostics snapshot for one scheduled energy network. */
public record EnergyNetworkRuntimeDiagnostics(
        EnergyNetworkActivity activity,
        long observedTopologyRevision,
        long executionCount,
        long wakeSignalCount,
        long sleepCount,
        long lastExecutionGameTime,
        boolean workRequested) {

    public static EnergyNetworkRuntimeDiagnostics capture(EnergyNetworkRuntimeState state) {
        if (state == null) {
            throw new NullPointerException("state");
        }
        return new EnergyNetworkRuntimeDiagnostics(
                state.activity(),
                state.observedTopologyRevision(),
                state.executionCount(),
                state.wakeSignalCount(),
                state.sleepCount(),
                state.lastExecutionGameTime(),
                state.workRequested());
    }
}
