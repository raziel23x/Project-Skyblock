package raziel23x.projectskyblock.simulation.energy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EnergyNetworkTopologyTest {
    private static final EnergyNetworkNodeId A = new EnergyNetworkNodeId("a");
    private static final EnergyNetworkNodeId B = new EnergyNetworkNodeId("b");
    private static final EnergyNetworkNodeId C = new EnergyNetworkNodeId("c");
    private static final EnergyNetworkNodeId D = new EnergyNetworkNodeId("d");

    @Test
    void orderingRemainsConsistentWithSemanticEquality() {
        EnergyNetworkConnection first = new EnergyNetworkConnection(A, B, 100L, 0);
        EnergyNetworkConnection second = new EnergyNetworkConnection(A, B, 200L, 0);

        assertNotEquals(first, second);
        assertNotEquals(0, first.compareTo(second));
    }

    @Test
    void conflictingConnectionRegistrationFailsExplicitly() {
        EnergyNetworkTopology topology = new EnergyNetworkTopology();
        topology.addNode(A);
        topology.addNode(B);
        assertTrue(topology.connect(new EnergyNetworkConnection(A, B, 100L, 0)));

        assertThrows(IllegalArgumentException.class,
                () -> topology.connect(new EnergyNetworkConnection(A, B, 200L, 0)));
        assertEquals(1, topology.connectionCount());
        assertEquals(3L, topology.revision());
    }

    @Test
    void equalLengthRoutesUseStableNodeOrdering() {
        EnergyNetworkTopology topology = new EnergyNetworkTopology();
        topology.addNode(A);
        topology.addNode(B);
        topology.addNode(C);
        topology.addNode(D);
        topology.connect(new EnergyNetworkConnection(A, C, 100L, 0));
        topology.connect(new EnergyNetworkConnection(C, D, 100L, 0));
        topology.connect(new EnergyNetworkConnection(A, B, 100L, 0));
        topology.connect(new EnergyNetworkConnection(B, D, 100L, 0));

        EnergyRoute route = topology.findRoute(A, D).orElseThrow();

        assertEquals(A, route.nodes().get(0));
        assertEquals(B, route.nodes().get(1));
        assertEquals(D, route.nodes().get(2));
    }
}
