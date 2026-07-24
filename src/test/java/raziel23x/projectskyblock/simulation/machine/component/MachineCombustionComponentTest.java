package raziel23x.projectskyblock.simulation.machine.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import raziel23x.projectskyblock.simulation.core.DirtyFlag;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;

class MachineCombustionComponentTest {
    @Test
    void ignitionAndConsumptionPreserveCycleTotals() {
        DirtyStateTracker dirty = new DirtyStateTracker();
        MachineCombustionComponent combustion = new MachineCombustionComponent(dirty);

        combustion.ignite(200L);
        assertTrue(combustion.burning());
        assertEquals(200L, combustion.remainingBurnUnits());
        assertEquals(200L, combustion.totalBurnUnits());
        assertEquals(25L, combustion.consume(25L));
        assertEquals(175L, combustion.remainingBurnUnits());
        assertEquals(200L, combustion.totalBurnUnits());
        assertTrue(dirty.isDirty(DirtyFlag.PERSISTENCE));
        assertTrue(dirty.isDirty(DirtyFlag.CLIENT_SYNC));
        assertFalse(dirty.isDirty(DirtyFlag.SCHEDULER));
    }

    @Test
    void consumeIsBoundedAndRepeatedIgnitionIsRejected() {
        MachineCombustionComponent combustion = new MachineCombustionComponent();
        combustion.ignite(10L);

        assertThrows(IllegalStateException.class, () -> combustion.ignite(5L));
        assertEquals(10L, combustion.consume(100L));
        assertFalse(combustion.burning());
        assertEquals(0L, combustion.consume(1L));

        combustion.ignite(5L);
        assertEquals(5L, combustion.remainingBurnUnits());
        assertEquals(2L, combustion.diagnostics().ignitionCount());
        assertEquals(10L, combustion.diagnostics().totalUnitsConsumed());
    }

    @Test
    void restoreValidatesBeforeMutationAndMarksOnlyClientSync() {
        DirtyStateTracker dirty = new DirtyStateTracker();
        MachineCombustionComponent combustion = new MachineCombustionComponent(dirty);
        combustion.restore(40L, 80L);

        assertEquals(40L, combustion.remainingBurnUnits());
        assertEquals(80L, combustion.totalBurnUnits());
        assertTrue(dirty.isDirty(DirtyFlag.CLIENT_SYNC));
        assertFalse(dirty.isDirty(DirtyFlag.PERSISTENCE));
        assertFalse(dirty.isDirty(DirtyFlag.SCHEDULER));

        assertThrows(IllegalArgumentException.class, () -> combustion.restore(81L, 80L));
        assertEquals(40L, combustion.remainingBurnUnits());
        assertEquals(80L, combustion.totalBurnUnits());
    }

    @Test
    void invalidCombustionStatesFailClosed() {
        assertThrows(IllegalArgumentException.class,
                () -> MachineCombustionComponent.validateRestore(-1L, 0L));
        assertThrows(IllegalArgumentException.class,
                () -> MachineCombustionComponent.validateRestore(1L, 0L));
        assertThrows(IllegalArgumentException.class,
                () -> MachineCombustionComponent.validateRestore(11L, 10L));
    }
}
