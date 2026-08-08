package raziel23x.projectskyblock.simulation.energy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;
import raziel23x.projectskyblock.simulation.core.SimulationBudget;
import raziel23x.projectskyblock.simulation.core.SimulationContext;
import raziel23x.projectskyblock.simulation.core.SimulationContextFactory;
import raziel23x.projectskyblock.simulation.core.SimulationLifecycle;
import raziel23x.projectskyblock.simulation.core.SimulationResult;
import raziel23x.projectskyblock.simulation.core.SimulationScheduler;

class EnergyNetworkParticipantTest {
    private static final SimulationContextFactory CONTEXT_FACTORY =
            (participantId, gameTime, dirtyState) -> new TestContext(gameTime, dirtyState);

    @Test
    void requestedWorkGetsOneActiveRecheckThenSleepsWithoutPolling() {
        EnergyNetworkParticipant participant = new EnergyNetworkParticipant(new EnergyNetworkTopology());
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        scheduler.register("network", participant);

        participant.requestWork();
        scheduler.wake("network");
        scheduler.tick(0L, CONTEXT_FACTORY);
        assertEquals(SimulationLifecycle.SCHEDULED, scheduler.lifecycleOf("network"));
        assertEquals(EnergyNetworkActivity.ACTIVE, participant.state().activity());

        scheduler.tick(1L, CONTEXT_FACTORY);
        assertEquals(EnergyNetworkActivity.IDLE, participant.state().activity());

        scheduler.tick(2L, CONTEXT_FACTORY);
        scheduler.tick(3L, CONTEXT_FACTORY);

        assertEquals(SimulationLifecycle.SLEEPING, scheduler.lifecycleOf("network"));
        assertEquals(EnergyNetworkActivity.SLEEPING, participant.state().activity());
        assertEquals(3L, participant.state().executionCount());
    }

    @Test
    void topologyChangeTriggersReevaluationAndBudgetExhaustionDoesNotAdvanceState() {
        EnergyNetworkTopology topology = new EnergyNetworkTopology();
        EnergyNetworkParticipant participant = new EnergyNetworkParticipant(topology);
        SimulationResult exhausted = participant.execute(
                new TestContext(0L, new DirtyStateTracker()),
                new SimulationBudget(0));

        assertTrue(exhausted instanceof SimulationResult.ContinueNextTick);
        assertEquals(0L, participant.state().executionCount());

        topology.addNode(new EnergyNetworkNodeId("new_node"));
        SimulationResult changed = participant.execute(
                new TestContext(1L, new DirtyStateTracker()),
                new SimulationBudget(1));

        assertTrue(changed instanceof SimulationResult.ContinueNextTick);
        assertEquals(EnergyNetworkActivity.ACTIVE, participant.state().activity());
        assertEquals(topology.revision(), participant.state().observedTopologyRevision());
    }

    private record TestContext(long gameTime, DirtyStateTracker dirtyState)
            implements SimulationContext {
    }
}
