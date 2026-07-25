package raziel23x.projectskyblock.simulation.transport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class TransportStepReservationsTest {
    private static final TransportChannelId ENERGY = new TransportChannelId("projectskyblock:energy");
    private static final TransportChannelId ITEMS = new TransportChannelId("projectskyblock:items");

    private static final TransportNodeId SOURCE_A = new TransportNodeId("source_a");
    private static final TransportNodeId SOURCE_B = new TransportNodeId("source_b");
    private static final TransportNodeId LEFT = new TransportNodeId("left");
    private static final TransportNodeId RIGHT = new TransportNodeId("right");
    private static final TransportNodeId TARGET_A = new TransportNodeId("target_a");
    private static final TransportNodeId TARGET_B = new TransportNodeId("target_b");

    @Test
    void competingRoutesShareOnePerChannelEdgeBudget() {
        Fixture fixture = fixture();
        TransportStepReservations reservations = new TransportStepReservations(
                fixture.topology(), ENERGY);
        TransportRoute firstRoute = fixture.topology()
                .findRoute(SOURCE_A, TARGET_A, ENERGY)
                .orElseThrow();
        TransportRoute secondRoute = fixture.topology()
                .findRoute(SOURCE_B, TARGET_B, ENERGY)
                .orElseThrow();

        assertEquals(80L, reservations.reserve(firstRoute, 80L));
        assertEquals(20L, reservations.reserve(secondRoute, 80L));
        assertEquals(100L, reservations.reservedCapacity(fixture.sharedConnection()));
        assertEquals(0L, reservations.remainingCapacity(fixture.sharedConnection()));
        assertEquals(0L, reservations.maximumReservable(secondRoute));
    }

    @Test
    void channelBudgetsRemainIndependentOnTheSamePhysicalEdge() {
        Fixture fixture = fixture();
        TransportRoute energyRoute = fixture.topology()
                .findRoute(SOURCE_A, TARGET_A, ENERGY)
                .orElseThrow();
        TransportRoute itemRoute = fixture.topology()
                .findRoute(SOURCE_A, TARGET_A, ITEMS)
                .orElseThrow();
        TransportStepReservations energyReservations = new TransportStepReservations(
                fixture.topology(), ENERGY);
        TransportStepReservations itemReservations = new TransportStepReservations(
                fixture.topology(), ITEMS);

        assertEquals(100L, energyReservations.reserve(energyRoute, 100L));
        assertEquals(4L, itemReservations.maximumReservable(itemRoute));
        assertEquals(4L, itemReservations.reserve(itemRoute, 4L));
    }

    @Test
    void invalidRouteValidationDoesNotConsumeAnyCapacity() {
        Fixture fixture = fixture();
        TransportStepReservations reservations = new TransportStepReservations(
                fixture.topology(), ENERGY);
        TransportProfile foreignProfile = profile("projectskyblock:foreign", 200L, 8L);
        TransportConnection foreignShared = new TransportConnection(LEFT, RIGHT, foreignProfile);
        TransportRoute foreignRoute = new TransportRoute(
                ENERGY,
                List.of(SOURCE_A, LEFT, RIGHT, TARGET_A),
                List.of(
                        fixture.sourceAConnection(),
                        foreignShared,
                        fixture.targetAConnection()));

        assertThrows(IllegalArgumentException.class, () -> reservations.reserve(foreignRoute, 50L));
        assertEquals(100L, reservations.remainingCapacity(fixture.sharedConnection()));
        assertEquals(0L, reservations.reservedCapacity(fixture.sharedConnection()));
    }


    @Test
    void topologyMutationInvalidatesExistingStepReservations() {
        Fixture fixture = fixture();
        TransportStepReservations reservations = new TransportStepReservations(
                fixture.topology(), ENERGY);
        fixture.topology().addNode(new TransportNodeId("late_node"));

        assertThrows(
                IllegalStateException.class,
                () -> reservations.remainingCapacity(fixture.sharedConnection()));
    }

    @Test
    void channelMismatchFailsBeforeReservationMutation() {
        Fixture fixture = fixture();
        TransportStepReservations reservations = new TransportStepReservations(
                fixture.topology(), ENERGY);
        TransportRoute itemRoute = fixture.topology()
                .findRoute(SOURCE_A, TARGET_A, ITEMS)
                .orElseThrow();

        assertThrows(IllegalArgumentException.class, () -> reservations.reserve(itemRoute, 1L));
        assertEquals(100L, reservations.remainingCapacity(fixture.sharedConnection()));
    }

    private static Fixture fixture() {
        TransportTopology topology = new TransportTopology();
        for (TransportNodeId node : List.of(
                SOURCE_A, SOURCE_B, LEFT, RIGHT, TARGET_A, TARGET_B)) {
            topology.addNode(node);
        }
        TransportProfile endpointProfile = profile("projectskyblock:endpoint", 1_000L, 64L);
        TransportProfile sharedProfile = profile("projectskyblock:shared", 100L, 4L);
        TransportConnection sourceA = new TransportConnection(SOURCE_A, LEFT, endpointProfile);
        TransportConnection sourceB = new TransportConnection(SOURCE_B, LEFT, endpointProfile);
        TransportConnection shared = new TransportConnection(LEFT, RIGHT, sharedProfile);
        TransportConnection targetA = new TransportConnection(RIGHT, TARGET_A, endpointProfile);
        TransportConnection targetB = new TransportConnection(RIGHT, TARGET_B, endpointProfile);
        topology.connect(sourceA);
        topology.connect(sourceB);
        topology.connect(shared);
        topology.connect(targetA);
        topology.connect(targetB);
        return new Fixture(topology, sourceA, shared, targetA);
    }

    private static TransportProfile profile(String id, long energy, long items) {
        return new TransportProfile(
                new TransportProfileId(id),
                List.of(
                        new TransportChannelProfile(ENERGY, energy),
                        new TransportChannelProfile(ITEMS, items)));
    }

    private record Fixture(
            TransportTopology topology,
            TransportConnection sourceAConnection,
            TransportConnection sharedConnection,
            TransportConnection targetAConnection) {
    }
}
