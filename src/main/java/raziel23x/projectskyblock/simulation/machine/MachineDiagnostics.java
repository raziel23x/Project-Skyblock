package raziel23x.projectskyblock.simulation.machine;

import raziel23x.projectskyblock.simulation.core.SimulationState;

/** Immutable machine lifecycle snapshot for diagnostics and future adapter use. */
public record MachineDiagnostics(
        MachineId id,
        MachineActivity activity,
        boolean enabled,
        boolean workRequested,
        long executionCount,
        long lastExecutionGameTime,
        String statusReason) {

    public static MachineDiagnostics capture(MachineState<? extends SimulationState> state) {
        return new MachineDiagnostics(
                state.id(),
                state.activity(),
                state.enabled(),
                state.workRequested(),
                state.executionCount(),
                state.lastExecutionGameTime(),
                state.statusReason());
    }
}
