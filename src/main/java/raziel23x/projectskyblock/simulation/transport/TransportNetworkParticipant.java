package raziel23x.projectskyblock.simulation.transport;

import java.util.Objects;
import raziel23x.projectskyblock.simulation.core.SimulationBudget;
import raziel23x.projectskyblock.simulation.core.SimulationContext;
import raziel23x.projectskyblock.simulation.core.SimulationParticipant;
import raziel23x.projectskyblock.simulation.core.SimulationResult;

/**
 * Event-driven scheduler participant for one typed transport topology.
 *
 * <p>Platform adapters request work and wake the scheduler after endpoint or topology events. A
 * progressing network continues next tick. Pending work with an unchanged no-progress fingerprint
 * is confirmed once, then sleeps until another event instead of polling forever.</p>
 */
public final class TransportNetworkParticipant
        implements SimulationParticipant<TransportNetworkRuntimeState> {
    private static final int EXECUTION_WORK_UNITS = 1;

    private final TransportTopology topology;
    private final TransportNetworkExecutor executor;
    private final TransportNetworkRuntimeState state = new TransportNetworkRuntimeState();

    public TransportNetworkParticipant(
            TransportTopology topology,
            TransportNetworkExecutor executor) {
        this.topology = Objects.requireNonNull(topology, "topology");
        this.executor = Objects.requireNonNull(executor, "executor");
    }

    @Override
    public TransportNetworkRuntimeState state() {
        return state;
    }

    public TransportTopology topology() {
        return topology;
    }

    public void requestWork() {
        state.requestWork();
    }

    public TransportNetworkRuntimeDiagnostics diagnostics() {
        return TransportNetworkRuntimeDiagnostics.capture(state);
    }

    @Override
    public SimulationResult execute(SimulationContext context, SimulationBudget budget) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(budget, "budget");
        if (!budget.tryConsume(EXECUTION_WORK_UNITS)) {
            return SimulationResult.continueNextTick();
        }

        state.recordExecution(context.gameTime());
        boolean topologyChanged = state.observeTopology(topology.revision());
        boolean workRequested = state.workRequested();
        state.consumeWorkRequest();
        if (topologyChanged || workRequested) {
            state.clearStallConfirmation();
        }

        TransportNetworkStepResult result = Objects.requireNonNull(
                executor.execute(topology, state.dispatchSequence()),
                "transport network executor returned null");
        state.advanceDispatchSequence();
        state.recordStep(result);

        if (result.madeProgress()) {
            if (result.hasPendingWork()) {
                state.becomeActive();
                return SimulationResult.continueNextTick();
            }
            state.clearStallConfirmation();
            state.becomeSleeping();
            return SimulationResult.sleep();
        }

        if (!result.hasPendingWork()) {
            state.clearStallConfirmation();
            state.becomeSleeping();
            return SimulationResult.sleep();
        }

        if (state.confirmsStableStall(result.stateFingerprint())) {
            state.recordConfirmedStall();
            state.becomeSleeping();
            return SimulationResult.sleep();
        }

        state.beginStallConfirmation(result.stateFingerprint());
        return SimulationResult.continueNextTick();
    }
}
