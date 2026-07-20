package raziel23x.projectskyblock.simulation.machine.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import raziel23x.projectskyblock.simulation.core.DirtyFlag;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;

class MachineProcessingComponentTest {
    @Test
    void operationProgressesToReadyAndCompletesExplicitly() {
        MachineProcessingComponent processing = new MachineProcessingComponent();

        processing.start("projectskyblock:crushing/cobblestone", 4L);
        assertTrue(processing.running());
        assertEquals(2L, processing.advance(2L));
        assertEquals(2L, processing.remainingUnits());
        assertEquals(2L, processing.advance(20L));
        assertTrue(processing.readyToComplete());

        processing.complete();

        assertTrue(processing.idle());
        assertEquals(1L, processing.diagnostics().completedProcesses());
    }

    @Test
    void blockedOperationRetainsProgressAndCanResume() {
        MachineProcessingComponent processing = new MachineProcessingComponent();
        processing.start("test:operation", 10L);
        processing.advance(3L);

        processing.block("missing energy");
        assertTrue(processing.blocked());
        assertEquals(0L, processing.advance(1L));
        assertEquals(3L, processing.completedUnits());

        processing.resume();
        assertTrue(processing.running());
        assertEquals(1L, processing.advance(1L));
        assertEquals(4L, processing.completedUnits());
    }

    @Test
    void progressDoesNotRequestRedundantSchedulerWake() {
        DirtyStateTracker dirtyState = new DirtyStateTracker();
        AtomicInteger wakeCount = new AtomicInteger();
        MachineProcessingComponent processing =
                new MachineProcessingComponent(dirtyState, wakeCount::incrementAndGet);

        processing.start("test:operation", 3L);
        assertEquals(1, wakeCount.get());
        dirtyState.clearAll();

        processing.advance(1L);

        assertEquals(1, wakeCount.get());
        assertTrue(dirtyState.isDirty(DirtyFlag.PERSISTENCE));
        assertTrue(dirtyState.isDirty(DirtyFlag.CLIENT_SYNC));
        assertFalse(dirtyState.isDirty(DirtyFlag.SCHEDULER));
    }

    @Test
    void stateTransitionsWakeAndMarkSchedulerDirty() {
        DirtyStateTracker dirtyState = new DirtyStateTracker();
        AtomicInteger wakeCount = new AtomicInteger();
        MachineProcessingComponent processing =
                new MachineProcessingComponent(dirtyState, wakeCount::incrementAndGet);

        processing.start("test:operation", 2L);
        assertTrue(dirtyState.isDirty(DirtyFlag.SCHEDULER));
        assertEquals(1, wakeCount.get());

        dirtyState.clearAll();
        processing.block("missing input");
        assertTrue(dirtyState.isDirty(DirtyFlag.SCHEDULER));
        assertEquals(2, wakeCount.get());
    }

    @Test
    void restoreValidatesDurableState() {
        MachineProcessingComponent processing = new MachineProcessingComponent();

        processing.restore(
                MachineProcessingStatus.BLOCKED,
                "test:operation",
                2L,
                5L,
                "missing output space",
                7L);

        assertTrue(processing.blocked());
        assertEquals(2L, processing.completedUnits());
        assertEquals(7L, processing.diagnostics().completedProcesses());
        assertThrows(IllegalArgumentException.class, () -> processing.restore(
                MachineProcessingStatus.READY_TO_COMPLETE,
                "test:operation",
                1L,
                5L,
                "",
                0L));
    }

    @Test
    void invalidLifecycleCallsAreRejected() {
        MachineProcessingComponent processing = new MachineProcessingComponent();

        assertThrows(IllegalArgumentException.class, () -> processing.start("", 1L));
        assertThrows(IllegalArgumentException.class, () -> processing.start("test:operation", 0L));
        assertThrows(IllegalStateException.class, () -> processing.block("missing input"));
        assertThrows(IllegalStateException.class, processing::complete);

        processing.start("test:operation", 1L);
        assertThrows(IllegalStateException.class, () -> processing.start("test:other", 1L));
    }
}
