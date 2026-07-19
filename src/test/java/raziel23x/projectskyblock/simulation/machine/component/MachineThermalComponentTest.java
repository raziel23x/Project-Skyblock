package raziel23x.projectskyblock.simulation.machine.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import raziel23x.projectskyblock.simulation.core.DirtyFlag;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;
import raziel23x.projectskyblock.simulation.thermal.ThermalCondition;
import raziel23x.projectskyblock.simulation.thermal.ThermalDiagnostics;
import raziel23x.projectskyblock.simulation.thermal.ThermalEnvironment;
import raziel23x.projectskyblock.simulation.thermal.ThermalProperties;
import raziel23x.projectskyblock.simulation.thermal.ThermalState;

class MachineThermalComponentTest {
    private static final ThermalProperties PROPERTIES = new ThermalProperties(
            10L,
            290_000L,
            350_000L,
            400_000L);

    @Test
    void externalAccessAndInternalOperationsAreSeparate() {
        MachineThermalComponent input = component(MachineThermalAccess.INPUT, 300_000L, 100L);
        assertEquals(1_000L, input.receiveHeat(1_000L));
        assertEquals(0L, input.extractHeat(500L));
        assertEquals(500L, input.consumeHeat(500L));

        MachineThermalComponent output = component(MachineThermalAccess.OUTPUT, 300_000L, 100L);
        assertEquals(0L, output.receiveHeat(1_000L));
        assertEquals(1_000L, output.generateHeat(1_000L));
        assertEquals(500L, output.extractHeat(500L));
    }

    @Test
    void operatingConditionUsesSharedThermalProperties() {
        MachineThermalComponent component = component(MachineThermalAccess.NONE, 280_000L, 10L);
        assertEquals(ThermalCondition.BELOW_OPERATING_RANGE, component.condition());
        assertFalse(component.isWithinOperatingRange());

        component.restoreThermalEnergy(300_000L * 10L);
        assertEquals(ThermalCondition.OPERATING, component.condition());
        assertTrue(component.isWithinOperatingRange());

        component.restoreThermalEnergy(400_000L * 10L);
        assertTrue(component.requiresShutdown());
    }

    @Test
    void environmentExchangeIsBoundedAndRecorded() {
        MachineThermalComponent component = component(MachineThermalAccess.NONE, 350_000L, 100L);
        ThermalEnvironment environment = new ThermalEnvironment(300_000L, 5L, 2_000L);

        long exchanged = component.exchangeWithEnvironment(environment);

        assertEquals(-2_000L, exchanged);
        assertEquals(34_998_000L, component.thermalEnergyMicroJoules());
        assertEquals(-2_000L, component.diagnostics().netEnvironmentalHeatMicroJoules());
        assertEquals(2_000L, component.diagnostics().totalHeatRemovedMicroJoules());
    }

    @Test
    void stepAddsGeneratedHeatAndSignalsOnce() {
        DirtyStateTracker dirty = new DirtyStateTracker();
        AtomicInteger wakes = new AtomicInteger();
        MachineThermalComponent component = new MachineThermalComponent(
                new ThermalState(300_000L, 100L),
                PROPERTIES,
                MachineThermalAccess.BIDIRECTIONAL,
                dirty,
                wakes::incrementAndGet);
        ThermalEnvironment insulated = new ThermalEnvironment(300_000L, 0L, 0L);

        ThermalDiagnostics result = component.step(insulated, 1_000L);

        assertEquals(1_000L, result.generatedHeatMicroJoules());
        assertEquals(0L, result.environmentalHeatMicroJoules());
        assertTrue(dirty.isDirty(DirtyFlag.PERSISTENCE));
        assertTrue(dirty.isDirty(DirtyFlag.CLIENT_SYNC));
        assertTrue(dirty.isDirty(DirtyFlag.SCHEDULER));
        assertEquals(1, wakes.get());
        assertEquals(1L, component.diagnostics().changeCount());
    }

    @Test
    void snapshotAndRestorePreserveAuthoritativeEnergy() {
        MachineThermalComponent component = component(MachineThermalAccess.INPUT, 300_000L, 100L);
        component.restoreThermalEnergy(31_234_567L);

        MachineThermalSnapshot snapshot = component.snapshot();

        assertEquals(31_234_567L, snapshot.thermalEnergyMicroJoules());
        assertEquals(312_345L, snapshot.temperatureMilliKelvin());
        assertEquals(MachineThermalAccess.INPUT, snapshot.access());
    }

    @Test
    void invalidHeatRequestsFailFastAndZeroOperationsDoNotSignal() {
        DirtyStateTracker dirty = new DirtyStateTracker();
        AtomicInteger wakes = new AtomicInteger();
        MachineThermalComponent component = new MachineThermalComponent(
                new ThermalState(300_000L, 100L),
                PROPERTIES,
                MachineThermalAccess.BIDIRECTIONAL,
                dirty,
                wakes::incrementAndGet);

        assertThrows(IllegalArgumentException.class, () -> component.receiveHeat(-1L));
        assertThrows(IllegalArgumentException.class, () -> component.restoreThermalEnergy(-1L));
        assertEquals(0L, component.generateHeat(0L));
        assertEquals(0, wakes.get());
        assertEquals(0L, component.diagnostics().changeCount());
    }

    private static MachineThermalComponent component(
            MachineThermalAccess access,
            long temperatureMilliKelvin,
            long heatCapacityMicroJoulesPerMilliKelvin) {
        return new MachineThermalComponent(
                new ThermalState(temperatureMilliKelvin, heatCapacityMicroJoulesPerMilliKelvin),
                PROPERTIES,
                access);
    }
}
