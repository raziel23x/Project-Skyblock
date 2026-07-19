package raziel23x.projectskyblock.simulation.energy;

import java.util.Objects;
import raziel23x.projectskyblock.simulation.core.SimulationBudget;
import raziel23x.projectskyblock.simulation.core.SimulationContext;
import raziel23x.projectskyblock.simulation.core.SimulationParticipant;
import raziel23x.projectskyblock.simulation.core.SimulationResult;

/**
 * Scheduler participant that keeps an unchanged energy network asleep.
 *
 * <p>Call {@link #requestWork()} and wake the registered scheduler participant
 * when production, demand, or topology events require reevaluation. The actual
 * multi-source transfer solver remains a later milestone.</p>
 */
public final class EnergyNetworkParticipant
        implements SimulationParticipant<EnergyNetworkRuntimeState> {
    private static final int EXECUTION_WORK_UNITS = 1;

    private final EnergyNetworkTopology topology;
    private final EnergyNetworkRuntimeState state = new EnergyNetworkRuntimeState();

    public EnergyNetworkParticipant(EnergyNetworkTopology topology) {
        this.topology = Objects.requireNonNull(topology, "topology");
    }

    @Override
    public EnergyNetworkRuntimeState state() {
        return state;
    }

    public EnergyNetworkTopology topology() {
        return topology;
    }

    /** Marks production, demand, or another external event as pending work. */
    public void requestWork() {
        state.requestWork();
    }

    public EnergyNetworkRuntimeDiagnostics diagnostics() {
        return EnergyNetworkRuntimeDiagnostics.capture(state);
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
        if (state.workRequested() || topologyChanged) {
            state.becomeActive();
            return SimulationResult.continueNextTick();
        }
        if (state.activity() == EnergyNetworkActivity.ACTIVE) {
            state.becomeIdle();
            return SimulationResult.continueNextTick();
        }

        state.becomeSleeping();
        return SimulationResult.sleep();
    }
}
