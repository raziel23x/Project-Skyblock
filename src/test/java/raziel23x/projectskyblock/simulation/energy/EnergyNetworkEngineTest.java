package raziel23x.projectskyblock.simulation.energy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EnergyNetworkEngineTest {
    private static final EnergyNetworkNodeId SOURCE = new EnergyNetworkNodeId("source");
    private static final EnergyNetworkNodeId MID = new EnergyNetworkNodeId("mid");
    private static final EnergyNetworkNodeId TARGET = new EnergyNetworkNodeId("target");

    @Test
    void deterministicRouteTransferPreservesPerHopLossAccounting() {
        EnergyNetworkTopology topology = topology(1_000L, 100_000);
        SimulationEnergyState source = new SimulationEnergyState(
                EnergyLimits.unlimitedThroughput(1_000L),
                1_000L);
        SimulationEnergyState target = new SimulationEnergyState(
                EnergyLimits.unlimitedThroughput(1_000L));

        EnergyNetworkTransferResult result = EnergyNetworkEngine.transfer(
                topology,
                SOURCE,
                source,
                TARGET,
                target,
                100L).orElseThrow();

        assertEquals(100L, result.transfer().extractedEnergy());
        assertEquals(81L, result.transfer().deliveredEnergy());
        assertEquals(19L, result.transfer().lostEnergy());
        assertEquals(2, result.hops().size());
        assertEquals(10L, result.hops().get(0).lostEnergy());
        assertEquals(9L, result.hops().get(1).lostEnergy());
        assertEquals(900L, source.storedEnergy());
        assertEquals(81L, target.storedEnergy());
    }

    @Test
    void targetAcceptanceBoundsSourceExtractionBeforeMutation() {
        EnergyNetworkTopology topology = topology(1_000L, 0);
        SimulationEnergyState source = new SimulationEnergyState(
                EnergyLimits.unlimitedThroughput(1_000L),
                1_000L);
        SimulationEnergyState target = new SimulationEnergyState(
                new EnergyLimits(1_000L, 40L, 1_000L));

        EnergyNetworkTransferResult result = EnergyNetworkEngine.transfer(
                topology,
                SOURCE,
                source,
                TARGET,
                target,
                100L).orElseThrow();

        assertEquals(40L, result.transfer().extractedEnergy());
        assertEquals(40L, result.transfer().deliveredEnergy());
        assertEquals(960L, source.storedEnergy());
        assertEquals(40L, target.storedEnergy());
        assertTrue(result.transfer().movedEnergy());
    }

    private static EnergyNetworkTopology topology(long transferLimit, int lossPartsPerMillion) {
        EnergyNetworkTopology topology = new EnergyNetworkTopology();
        topology.addNode(SOURCE);
        topology.addNode(MID);
        topology.addNode(TARGET);
        topology.connect(new EnergyNetworkConnection(
                SOURCE,
                MID,
                transferLimit,
                lossPartsPerMillion));
        topology.connect(new EnergyNetworkConnection(
                MID,
                TARGET,
                transferLimit,
                lossPartsPerMillion));
        return topology;
    }
}
