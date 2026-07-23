package raziel23x.projectskyblock.simulation.energy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SimulationEnergyStateTest {
    @Test
    void operationLimitsApplyIndependentlyToEachCall() {
        SimulationEnergyState state = new SimulationEnergyState(new EnergyLimits(1_000L, 100L, 60L));

        assertEquals(100L, state.receive(500L));
        assertEquals(100L, state.receive(500L));
        assertEquals(200L, state.storedEnergy());
        assertEquals(60L, state.extract(500L));
        assertEquals(60L, state.extract(500L));
        assertEquals(80L, state.storedEnergy());
    }

    @Test
    void transferAccountsForExactLossAndBufferLimits() {
        SimulationEnergyState source = new SimulationEnergyState(
                new EnergyLimits(1_000L, 1_000L, 250L), 800L);
        SimulationEnergyState target = new SimulationEnergyState(
                new EnergyLimits(1_000L, 200L, 1_000L));

        EnergyDiagnostics diagnostics = EnergyEngine.transfer(
                source,
                target,
                new EnergyRequest(500L, 900_000));

        assertEquals(223L, diagnostics.flow().transfer().extractedEnergy());
        assertEquals(200L, diagnostics.flow().transfer().deliveredEnergy());
        assertEquals(23L, diagnostics.flow().transfer().lostEnergy());
        assertEquals(577L, source.storedEnergy());
        assertEquals(200L, target.storedEnergy());
        assertTrue(diagnostics.throughputLimited());
    }

    @Test
    void restoreValidationDoesNotMutateState() {
        SimulationEnergyState state = new SimulationEnergyState(
                new EnergyLimits(100L, 100L, 100L), 25L);

        assertThrows(IllegalArgumentException.class, () -> state.validateStoredEnergy(101L));
        assertEquals(25L, state.storedEnergy());
    }
}
