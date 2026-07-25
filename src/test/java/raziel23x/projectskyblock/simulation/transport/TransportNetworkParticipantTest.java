package raziel23x.projectskyblock.simulation.transport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;
import raziel23x.projectskyblock.simulation.core.SimulationBudget;
import raziel23x.projectskyblock.simulation.core.SimulationContext;
import raziel23x.projectskyblock.simulation.core.SimulationContextFactory;
import raziel23x.projectskyblock.simulation.core.SimulationLifecycle;
import raziel23x.projectskyblock.simulation.core.SimulationResult;
import raziel23x.projectskyblock.simulation.core.SimulationScheduler;

class TransportNetworkParticipantTest {
    private static final TransportChannelId ENERGY = new TransportChannelId("projectskyblock:energy");
    private static final TransportChannelId ITEMS = new TransportChannelId("projectskyblock:items");
    private static final SimulationContextFactory CONTEXT_FACTORY =
            (participantId, gameTime, dirtyState) -> new TestContext(gameTime, dirtyState);

    @Test
    void progressingNetworkContinuesUntilItsTypedWorkIsQuiescent() {
        AtomicInteger executions = new AtomicInteger();
        TransportNetworkParticipant participant = new TransportNetworkParticipant(
                new TransportTopology(),
                (topology, dispatchSequence) -> {
                    int execution = executions.incrementAndGet();
                    if (execution == 1) {
                        return step(1L, channel(ENERGY, 100L, 50L, 50L, 0L, 0L));
                    }
                    return step(2L, channel(ENERGY, 50L, 50L, 0L, 0L, 0L));
                });
        SimulationScheduler scheduler = scheduler(participant);

        participant.requestWork();
        scheduler.wake("network");
        scheduler.tick(0L, CONTEXT_FACTORY);
        scheduler.tick(1L, CONTEXT_FACTORY);
        scheduler.tick(2L, CONTEXT_FACTORY);

        assertEquals(2, executions.get());
        assertEquals(SimulationLifecycle.SLEEPING, scheduler.lifecycleOf("network"));
        assertEquals(TransportNetworkActivity.SLEEPING, participant.state().activity());
        assertEquals(2L, participant.state().dispatchSequence());
        assertEquals(50L, participant.diagnostics().channelResults().getFirst().committedUnits());
    }

    @Test
    void unchangedNoProgressFingerprintSleepsAfterOneConfirmation() {
        AtomicInteger executions = new AtomicInteger();
        TransportNetworkParticipant participant = new TransportNetworkParticipant(
                new TransportTopology(),
                (topology, dispatchSequence) -> {
                    executions.incrementAndGet();
                    return step(42L, channel(ITEMS, 8L, 0L, 0L, 8L, 0L));
                });
        SimulationScheduler scheduler = scheduler(participant);

        participant.requestWork();
        scheduler.wake("network");
        scheduler.tick(0L, CONTEXT_FACTORY);
        assertEquals(SimulationLifecycle.SCHEDULED, scheduler.lifecycleOf("network"));
        assertEquals(TransportNetworkActivity.CONFIRMING_STALL, participant.state().activity());

        scheduler.tick(1L, CONTEXT_FACTORY);
        scheduler.tick(2L, CONTEXT_FACTORY);

        assertEquals(2, executions.get());
        assertEquals(SimulationLifecycle.SLEEPING, scheduler.lifecycleOf("network"));
        assertEquals(2L, participant.state().stableNoProgressCount());
    }

    @Test
    void changedNoProgressFingerprintMustStabilizeBeforeSleeping() {
        AtomicInteger executions = new AtomicInteger();
        TransportNetworkParticipant participant = new TransportNetworkParticipant(
                new TransportTopology(),
                (topology, dispatchSequence) -> {
                    int execution = executions.incrementAndGet();
                    long fingerprint = execution == 1 ? 10L : 11L;
                    return step(fingerprint, channel(ITEMS, 4L, 0L, 4L, 0L, 0L));
                });
        SimulationScheduler scheduler = scheduler(participant);

        participant.requestWork();
        scheduler.wake("network");
        scheduler.tick(0L, CONTEXT_FACTORY);
        scheduler.tick(1L, CONTEXT_FACTORY);
        scheduler.tick(2L, CONTEXT_FACTORY);

        assertEquals(3, executions.get());
        assertEquals(SimulationLifecycle.SLEEPING, scheduler.lifecycleOf("network"));
        assertEquals(2L, participant.state().stableNoProgressCount());
    }

    @Test
    void explicitWakeRestartsAStablyBlockedNetwork() {
        AtomicInteger executions = new AtomicInteger();
        TransportNetworkParticipant participant = new TransportNetworkParticipant(
                new TransportTopology(),
                (topology, dispatchSequence) -> {
                    executions.incrementAndGet();
                    return step(99L, channel(ITEMS, 1L, 0L, 0L, 1L, 0L));
                });
        SimulationScheduler scheduler = scheduler(participant);

        participant.requestWork();
        scheduler.wake("network");
        scheduler.tick(0L, CONTEXT_FACTORY);
        scheduler.tick(1L, CONTEXT_FACTORY);
        participant.requestWork();
        scheduler.wake("network");
        scheduler.tick(2L, CONTEXT_FACTORY);
        scheduler.tick(3L, CONTEXT_FACTORY);

        assertEquals(4, executions.get());
        assertEquals(2L, participant.state().wakeSignalCount());
        assertEquals(2L, participant.state().sleepCount());
    }

    @Test
    void diagnosticsNeverAggregateIncompatibleChannelUnits() {
        TransportNetworkStepResult result = step(
                123L,
                channel(ITEMS, 8L, 4L, 4L, 0L, 0L),
                channel(ENERGY, 4_096L, 4_096L, 0L, 0L, 0L));

        assertEquals(List.of(ENERGY, ITEMS), result.channelResults().stream()
                .map(TransportChannelStepResult::channelId)
                .toList());
        assertEquals(4_096L, result.channelResults().get(0).committedUnits());
        assertEquals(4L, result.channelResults().get(1).committedUnits());
        assertTrue(result.madeProgress());
        assertTrue(result.hasPendingWork());
    }

    @Test
    void exhaustedExecutionBudgetDoesNotCallChannelPolicies() {
        AtomicInteger executions = new AtomicInteger();
        TransportNetworkParticipant participant = new TransportNetworkParticipant(
                new TransportTopology(),
                (topology, dispatchSequence) -> {
                    executions.incrementAndGet();
                    return step(0L);
                });

        SimulationResult result = participant.execute(
                new TestContext(0L, new DirtyStateTracker()),
                new SimulationBudget(0));

        assertTrue(result instanceof SimulationResult.ContinueNextTick);
        assertEquals(0, executions.get());
        assertEquals(0L, participant.state().executionCount());
    }

    private static SimulationScheduler scheduler(TransportNetworkParticipant participant) {
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        scheduler.register("network", participant);
        return scheduler;
    }

    private static TransportNetworkStepResult step(
            long fingerprint,
            TransportChannelStepResult... channels) {
        return new TransportNetworkStepResult(fingerprint, List.of(channels));
    }

    private static TransportChannelStepResult channel(
            TransportChannelId channelId,
            long requested,
            long committed,
            long capacityDeferred,
            long endpointBlocked,
            long unroutable) {
        return new TransportChannelStepResult(
                channelId,
                requested,
                committed,
                capacityDeferred,
                endpointBlocked,
                unroutable);
    }

    private record TestContext(long gameTime, DirtyStateTracker dirtyState)
            implements SimulationContext {
    }
}
