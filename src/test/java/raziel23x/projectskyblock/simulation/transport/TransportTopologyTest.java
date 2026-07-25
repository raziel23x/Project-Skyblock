package raziel23x.projectskyblock.simulation.transport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class TransportTopologyTest {
    private static final TransportChannelId ENERGY = new TransportChannelId("projectskyblock:energy");
    private static final TransportChannelId ITEMS = new TransportChannelId("projectskyblock:items");
    private static final TransportChannelId FLUIDS = new TransportChannelId("projectskyblock:fluids");
    private static final TransportChannelId GASES = new TransportChannelId("projectskyblock:gases");

    private static final TransportNodeId A = new TransportNodeId("a");
    private static final TransportNodeId B = new TransportNodeId("b");
    private static final TransportNodeId C = new TransportNodeId("c");
    private static final TransportNodeId D = new TransportNodeId("d");

    @Test
    void oneProfileKeepsEveryChannelInItsNativeThroughputUnit() {
        TransportProfile profile = profile(
                "projectskyblock:test_universal",
                channel(ENERGY, 4_096L),
                channel(ITEMS, 8L),
                channel(FLUIDS, 1_000L),
                channel(GASES, 500L));

        assertEquals(4_096L, profile.maximumUnitsPerStep(ENERGY).orElseThrow());
        assertEquals(8L, profile.maximumUnitsPerStep(ITEMS).orElseThrow());
        assertEquals(1_000L, profile.maximumUnitsPerStep(FLUIDS).orElseThrow());
        assertEquals(500L, profile.maximumUnitsPerStep(GASES).orElseThrow());
        assertEquals(4, profile.channels().size());
    }

    @Test
    void routeDiscoveryFiltersThePhysicalGraphByChannelSupport() {
        TransportTopology topology = topology(A, B, C);
        topology.connect(connection(A, B, profile(
                "projectskyblock:mixed",
                channel(ENERGY, 1_000L),
                channel(ITEMS, 4L))));
        topology.connect(connection(B, C, profile(
                "projectskyblock:energy_only",
                channel(ENERGY, 500L))));

        TransportRoute energyRoute = topology.findRoute(A, C, ENERGY).orElseThrow();

        assertEquals(500L, energyRoute.maximumUnitsPerStep());
        assertTrue(topology.findRoute(A, C, ITEMS).isEmpty());
        assertEquals(1, topology.connectedComponents(ENERGY).size());
        assertEquals(2, topology.connectedComponents(ITEMS).size());
    }

    @Test
    void equalLengthTypedRoutesUseStableNodeOrdering() {
        TransportTopology topology = topology(A, B, C, D);
        TransportProfile profile = profile(
                "projectskyblock:energy",
                channel(ENERGY, 100L));
        topology.connect(connection(A, C, profile));
        topology.connect(connection(C, D, profile));
        topology.connect(connection(A, B, profile));
        topology.connect(connection(B, D, profile));

        TransportRoute route = topology.findRoute(A, D, ENERGY).orElseThrow();

        assertEquals(List.of(A, B, D), route.nodes());
    }

    @Test
    void connectionOrderingRemainsConsistentWithSemanticEquality() {
        TransportConnection first = connection(A, B, profile(
                "projectskyblock:first",
                channel(ENERGY, 100L)));
        TransportConnection second = connection(A, B, profile(
                "projectskyblock:first",
                channel(ENERGY, 200L)));

        assertNotEquals(first, second);
        assertNotEquals(0, first.compareTo(second));
    }

    @Test
    void conflictingPhysicalConnectionProfileFailsExplicitly() {
        TransportTopology topology = topology(A, B);
        assertTrue(topology.connect(connection(A, B, profile(
                "projectskyblock:slow",
                channel(ENERGY, 100L)))));

        assertThrows(IllegalArgumentException.class, () -> topology.connect(connection(
                A,
                B,
                profile("projectskyblock:fast", channel(ENERGY, 200L)))));
        assertEquals(1, topology.connectionCount());
        assertEquals(3L, topology.revision());
    }

    @Test
    void unsupportedChannelDoesNotPretendToHaveZeroSpeed() {
        TransportProfile profile = profile(
                "projectskyblock:energy_only",
                channel(ENERGY, 100L));

        assertFalse(profile.supports(ITEMS));
        assertTrue(profile.maximumUnitsPerStep(ITEMS).isEmpty());
    }

    private static TransportTopology topology(TransportNodeId... nodes) {
        TransportTopology topology = new TransportTopology();
        for (TransportNodeId node : nodes) {
            topology.addNode(node);
        }
        return topology;
    }

    private static TransportConnection connection(
            TransportNodeId first,
            TransportNodeId second,
            TransportProfile profile) {
        return new TransportConnection(first, second, profile);
    }

    private static TransportProfile profile(
            String id,
            TransportChannelProfile... channels) {
        return new TransportProfile(new TransportProfileId(id), List.of(channels));
    }

    private static TransportChannelProfile channel(
            TransportChannelId id,
            long throughput) {
        return new TransportChannelProfile(id, throughput);
    }
}
