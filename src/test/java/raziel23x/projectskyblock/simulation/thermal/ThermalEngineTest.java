package raziel23x.projectskyblock.simulation.thermal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ThermalEngineTest {
    @Test
    void transferCannotCrossEquilibrium() {
        ThermalState hot = new ThermalState(400L, 10L);
        ThermalState cold = new ThermalState(300L, 10L);

        long transferred = ThermalTransfer.transfer(hot, cold, 1_000L, Long.MAX_VALUE);

        assertEquals(500L, transferred);
        assertEquals(350L, hot.temperatureMilliKelvin());
        assertEquals(350L, cold.temperatureMilliKelvin());
    }

    @Test
    void fractionalHeatIsRetainedEvenWithoutTemperatureChange() {
        ThermalState state = new ThermalState(300L, 100L);
        state.addHeatMicroJoules(99L);

        assertEquals(300L, state.temperatureMilliKelvin());
        assertEquals(30_099L, state.thermalEnergyMicroJoules());
    }

    @Test
    void ambientExchangeIsBounded() {
        ThermalState state = new ThermalState(400L, 10L);
        ThermalProperties properties = new ThermalProperties(100L, 0L, 1_000L, 2_000L);
        ThermalEnvironment environment = new ThermalEnvironment(300L, 100L, 75L);

        long exchanged = ThermalEngine.exchangeWithEnvironment(state, properties, environment);

        assertEquals(-75L, exchanged);
        assertEquals(3_925L, state.thermalEnergyMicroJoules());
    }

    @Test
    void operatingConditionUsesExactBoundaries() {
        ThermalProperties properties = new ThermalProperties(0L, 300L, 400L, 450L);

        assertTrue(properties.isWithinOperatingRange(new ThermalState(300L, 1L)));
        assertTrue(properties.isWithinOperatingRange(new ThermalState(400L, 1L)));
        assertEquals(ThermalCondition.ABOVE_OPERATING_RANGE,
                properties.condition(new ThermalState(401L, 1L)));
        assertEquals(ThermalCondition.SHUTDOWN,
                properties.condition(new ThermalState(450L, 1L)));
    }
}
