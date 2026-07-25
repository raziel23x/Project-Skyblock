package raziel23x.projectskyblock.simulation.transport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class TransportDispatchPlannerTest {
    private static final TransportChannelId ENERGY = new TransportChannelId("projectskyblock:energy");
    private static final TransportChannelId ITEMS = new TransportChannelId("projectskyblock:items");

    private static final TransportNodeId SOURCE_A = new TransportNodeId("source_a");
    private static final TransportNodeId SOURCE_B = new TransportNodeId("source_b");
    private static final TransportNodeId LEFT = new TransportNodeId("left");
    private static final TransportNodeId RIGHT = new TransportNodeId("right");
    private static final TransportNodeId TARGET = new TransportNodeId("target");
    private static final TransportNodeId ISOLATED = new TransportNodeId("isolated");

    @Test
    void fairnessRotationPreventsStableIdStarvationAcrossSteps() {
        TransportTopology topology = sharedTopology(100L);
        TransportDispatchRequest first = request("a", SOURCE_A, TARGET, 100L);
        TransportDispatchRequest second = request("b", SOURCE_B, TARGET, 100L);

        TransportDispatchPlan stepZero = TransportDispatchPlanner.plan(
                topology, ENERGY, List.of(second, first), 0L);
        TransportDispatchPlan stepOne = TransportDispatchPlanner.plan(
                topology, ENERGY, List.of(second, first), stepZero.nextFairnessSequence());

        assertEquals("a", stepZero.decisions().get(0).request().id().value());
        assertEquals(100L, stepZero.decisions().get(0).plannedUnits());
        assertEquals(0L, stepZero.decisions().get(1).plannedUnits());
        assertEquals(100L, stepZero.deferredUnits());

        assertEquals("b", stepOne.decisions().get(0).request().id().value());
        assertEquals(100L, stepOne.decisions().get(0).plannedUnits());
        assertEquals(0L, stepOne.decisions().get(1).plannedUnits());
        assertEquals(1, stepOne.startIndex());
    }

    @Test
    void partialPlanningAccountsForEveryNativeUnit() {
        TransportTopology topology = sharedTopology(150L);
        TransportDispatchPlan plan = TransportDispatchPlanner.plan(
                topology,
                ENERGY,
                List.of(
                        request("a", SOURCE_A, TARGET, 100L),
                        request("b", SOURCE_B, TARGET, 100L)),
                0L);

        assertEquals(200L, plan.requestedUnits());
        assertEquals(150L, plan.plannedUnits());
        assertEquals(50L, plan.deferredUnits());
        assertEquals(0L, plan.unroutableUnits());
        assertEquals(TransportDispatchStatus.PARTIAL, plan.decisions().get(1).status());
    }

    @Test
    void unsupportedChannelIsReportedAsUnroutableWithoutInventingZeroSpeed() {
        TransportTopology topology = sharedTopology(100L);
        TransportDispatchPlan plan = TransportDispatchPlanner.plan(
                topology,
                ITEMS,
                List.of(request("items", SOURCE_A, TARGET, 8L)),
                0L);

        assertEquals(0L, plan.plannedUnits());
        assertEquals(0L, plan.deferredUnits());
        assertEquals(8L, plan.unroutableUnits());
        assertEquals(TransportDispatchStatus.UNROUTABLE, plan.decisions().getFirst().status());
    }

    @Test
    void absentPhysicalRouteIsSeparatedFromCapacityDeferral() {
        TransportTopology topology = sharedTopology(100L);
        TransportDispatchPlan plan = TransportDispatchPlanner.plan(
                topology,
                ENERGY,
                List.of(
                        request("routable", SOURCE_A, TARGET, 150L),
                        request("isolated", ISOLATED, TARGET, 25L)),
                0L);

        assertEquals(100L, plan.plannedUnits());
        assertEquals(50L, plan.deferredUnits());
        assertEquals(25L, plan.unroutableUnits());
    }

    @Test
    void duplicateRequestIdsFailBeforeAnyPlanIsPublished() {
        TransportTopology topology = sharedTopology(100L);

        assertThrows(IllegalArgumentException.class, () -> TransportDispatchPlanner.plan(
                topology,
                ENERGY,
                List.of(
                        request("duplicate", SOURCE_A, TARGET, 10L),
                        request("duplicate", SOURCE_B, TARGET, 10L)),
                0L));
    }

    @Test
    void emptyPlanDoesNotAdvanceFairnessSequence() {
        TransportTopology topology = sharedTopology(100L);

        TransportDispatchPlan plan = TransportDispatchPlanner.plan(
                topology, ENERGY, List.of(), 7L);

        assertEquals(7L, plan.fairnessSequence());
        assertEquals(7L, plan.nextFairnessSequence());
        assertEquals(0, plan.startIndex());
        assertEquals(List.of(), plan.decisions());
    }

    private static TransportTopology sharedTopology(long sharedEnergyLimit) {
        TransportTopology topology = new TransportTopology();
        for (TransportNodeId node : List.of(
                SOURCE_A, SOURCE_B, LEFT, RIGHT, TARGET, ISOLATED)) {
            topology.addNode(node);
        }
        TransportProfile endpoint = energyProfile("projectskyblock:endpoint", 1_000L);
        TransportProfile shared = energyProfile("projectskyblock:shared", sharedEnergyLimit);
        topology.connect(new TransportConnection(SOURCE_A, LEFT, endpoint));
        topology.connect(new TransportConnection(SOURCE_B, LEFT, endpoint));
        topology.connect(new TransportConnection(LEFT, RIGHT, shared));
        topology.connect(new TransportConnection(RIGHT, TARGET, endpoint));
        return topology;
    }

    private static TransportProfile energyProfile(String id, long limit) {
        return new TransportProfile(
                new TransportProfileId(id),
                List.of(new TransportChannelProfile(ENERGY, limit)));
    }

    private static TransportDispatchRequest request(
            String id,
            TransportNodeId source,
            TransportNodeId target,
            long units) {
        return new TransportDispatchRequest(
                new TransportDispatchRequestId(id), source, target, units);
    }
}
