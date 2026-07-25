package raziel23x.projectskyblock.simulation.transport;

import java.util.List;
import java.util.Objects;

/** Immutable diagnostics snapshot for one scheduled typed transport network. */
public record TransportNetworkRuntimeDiagnostics(
        TransportNetworkActivity activity,
        long observedTopologyRevision,
        long executionCount,
        long dispatchSequence,
        long wakeSignalCount,
        long sleepCount,
        long stableNoProgressCount,
        long lastExecutionGameTime,
        boolean workRequested,
        List<TransportChannelStepResult> channelResults) {

    public TransportNetworkRuntimeDiagnostics {
        Objects.requireNonNull(activity, "activity");
        channelResults = List.copyOf(channelResults);
    }

    public static TransportNetworkRuntimeDiagnostics capture(TransportNetworkRuntimeState state) {
        Objects.requireNonNull(state, "state");
        return new TransportNetworkRuntimeDiagnostics(
                state.activity(),
                state.observedTopologyRevision(),
                state.executionCount(),
                state.dispatchSequence(),
                state.wakeSignalCount(),
                state.sleepCount(),
                state.stableNoProgressCount(),
                state.lastExecutionGameTime(),
                state.workRequested(),
                state.lastChannelResults());
    }
}
