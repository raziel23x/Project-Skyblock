package raziel23x.projectskyblock.simulation.machine.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ConcurrentModificationException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import raziel23x.projectskyblock.simulation.core.DirtyFlag;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;
import raziel23x.projectskyblock.simulation.energy.EnergyLimits;
import raziel23x.projectskyblock.simulation.energy.SimulationEnergyState;

class MachineEnergyTransactionTest {
    @Test
    void changedCommitPublishesOnceAndWakesOnce() {
        DirtyStateTracker dirtyState = new DirtyStateTracker();
        AtomicInteger wakes = new AtomicInteger();
        MachineEnergyComponent energy = component(
                new EnergyLimits(1_000L, 250L, 200L),
                MachineEnergyAccess.BIDIRECTIONAL,
                0L,
                dirtyState,
                wakes);

        try (MachineEnergyTransaction transaction = energy.beginTransaction()) {
            assertEquals(200L, transaction.receive(200L));
            assertEquals(200L, transaction.storedEnergy());
            assertEquals(0L, energy.storedEnergy());
            assertEquals(0, wakes.get());
            assertTrue(dirtyState.isClean());

            MachineEnergyCommitResult result = transaction.commit();

            assertTrue(result.changed());
            assertEquals(200L, result.receivedEnergy());
            assertEquals(0L, result.extractedEnergy());
            assertEquals(200L, result.storedEnergy());
            assertEquals(1L, result.stateVersion());
        }

        assertEquals(200L, energy.storedEnergy());
        assertEquals(1L, energy.stateVersion());
        assertEquals(1, wakes.get());
        assertTrue(dirtyState.isDirty(DirtyFlag.PERSISTENCE));
        assertTrue(dirtyState.isDirty(DirtyFlag.CLIENT_SYNC));
        assertTrue(dirtyState.isDirty(DirtyFlag.SCHEDULER));
        assertEquals(1L, energy.diagnostics().transactionCommitCount());
        assertEquals(1L, energy.diagnostics().changeCount());
    }

    @Test
    void rollbackAndSimulationCandidateDoNotMutateOrWake() {
        DirtyStateTracker dirtyState = new DirtyStateTracker();
        AtomicInteger wakes = new AtomicInteger();
        MachineEnergyComponent energy = component(
                EnergyLimits.unlimitedThroughput(1_000L),
                MachineEnergyAccess.INPUT,
                0L,
                dirtyState,
                wakes);

        try (MachineEnergyTransaction transaction = energy.beginTransaction()) {
            assertEquals(400L, transaction.receive(400L));
            assertTrue(transaction.changed());
        }

        assertEquals(0L, energy.storedEnergy());
        assertEquals(0L, energy.stateVersion());
        assertEquals(0, wakes.get());
        assertTrue(dirtyState.isClean());
        assertEquals(0L, energy.diagnostics().transactionCommitCount());
    }

    @Test
    void noOpCommitDoesNotAdvanceVersionOrWake() {
        DirtyStateTracker dirtyState = new DirtyStateTracker();
        AtomicInteger wakes = new AtomicInteger();
        MachineEnergyComponent energy = component(
                EnergyLimits.unlimitedThroughput(1_000L),
                MachineEnergyAccess.INPUT,
                0L,
                dirtyState,
                wakes);

        try (MachineEnergyTransaction transaction = energy.beginTransaction()) {
            assertEquals(0L, transaction.receive(0L));
            MachineEnergyCommitResult result = transaction.commit();

            assertFalse(result.changed());
            assertEquals(0L, result.receivedEnergy());
            assertEquals(0L, result.extractedEnergy());
            assertEquals(0L, result.stateVersion());
        }

        assertEquals(0L, energy.stateVersion());
        assertEquals(0L, energy.diagnostics().transactionCommitCount());
        assertEquals(0, wakes.get());
        assertTrue(dirtyState.isClean());
    }

    @Test
    void staleCandidateFailsClosedWithoutAdditionalWake() {
        DirtyStateTracker dirtyState = new DirtyStateTracker();
        AtomicInteger wakes = new AtomicInteger();
        MachineEnergyComponent energy = component(
                EnergyLimits.unlimitedThroughput(1_000L),
                MachineEnergyAccess.INPUT,
                0L,
                dirtyState,
                wakes);
        MachineEnergyTransaction first = energy.beginTransaction();
        MachineEnergyTransaction stale = energy.beginTransaction();

        assertEquals(100L, first.receive(100L));
        first.commit();
        int wakesAfterFirstCommit = wakes.get();
        assertEquals(50L, stale.receive(50L));

        assertThrows(ConcurrentModificationException.class, stale::commit);
        assertEquals(100L, energy.storedEnergy());
        assertEquals(wakesAfterFirstCommit, wakes.get());
        assertEquals(1L, energy.diagnostics().transactionConflictCount());
        assertThrows(IllegalStateException.class, stale::storedEnergy);
    }

    @Test
    void accessChangeInvalidatesOpenExternalCandidate() {
        AtomicInteger wakes = new AtomicInteger();
        MachineEnergyComponent energy = component(
                EnergyLimits.unlimitedThroughput(1_000L),
                MachineEnergyAccess.INPUT,
                0L,
                new DirtyStateTracker(),
                wakes);
        MachineEnergyTransaction transaction = energy.beginTransaction();
        assertEquals(100L, transaction.receive(100L));

        energy.setAccess(MachineEnergyAccess.NONE);
        int wakesAfterAccessChange = wakes.get();

        assertThrows(ConcurrentModificationException.class, transaction::commit);
        assertEquals(0L, energy.storedEnergy());
        assertEquals(1L, energy.stateVersion());
        assertEquals(wakesAfterAccessChange, wakes.get());
        assertFalse(energy.access().acceptsEnergy());
    }

    @Test
    void externalAccessIsSeparateFromInternalProduceAndConsume() {
        MachineEnergyComponent energy = new MachineEnergyComponent(
                new EnergyLimits(1_000L, 300L, 200L),
                MachineEnergyAccess.NONE);

        assertEquals(0L, energy.receive(100L));
        assertEquals(300L, energy.produce(500L));
        assertEquals(300L, energy.storedEnergy());
        assertEquals(0L, energy.extract(100L));
        assertEquals(200L, energy.consume(500L));
        assertEquals(100L, energy.storedEnergy());
        assertEquals(2L, energy.stateVersion());
    }

    @Test
    void oneTransactionUsesOneAggregateDirectionalThroughputBudget() {
        MachineEnergyComponent energy = new MachineEnergyComponent(
                new EnergyLimits(1_000L, 100L, 100L),
                MachineEnergyAccess.BIDIRECTIONAL);

        try (MachineEnergyTransaction transaction = energy.beginTransaction()) {
            assertEquals(80L, transaction.receive(80L));
            assertEquals(20L, transaction.receive(80L));
            assertEquals(100L, transaction.receivedEnergy());
            assertEquals(100L, transaction.storedEnergy());
            assertThrows(IllegalStateException.class, () -> transaction.extract(1L));
            transaction.commit();
        }

        assertEquals(100L, energy.storedEnergy());
    }

    @Test
    void changedRestoreInvalidatesTransactionsWithoutPersistenceDirtyOrWake() {
        DirtyStateTracker dirtyState = new DirtyStateTracker();
        AtomicInteger wakes = new AtomicInteger();
        MachineEnergyComponent energy = component(
                EnergyLimits.unlimitedThroughput(1_000L),
                MachineEnergyAccess.INPUT,
                0L,
                dirtyState,
                wakes);
        MachineEnergyTransaction stale = energy.beginTransaction();
        assertEquals(25L, stale.receive(25L));

        energy.restoreStoredEnergy(50L);

        assertEquals(50L, energy.storedEnergy());
        assertEquals(1L, energy.stateVersion());
        assertEquals(0, wakes.get());
        assertFalse(dirtyState.isDirty(DirtyFlag.PERSISTENCE));
        assertTrue(dirtyState.isDirty(DirtyFlag.CLIENT_SYNC));
        assertFalse(dirtyState.isDirty(DirtyFlag.SCHEDULER));
        assertThrows(ConcurrentModificationException.class, stale::commit);
        assertEquals(50L, energy.storedEnergy());
    }

    private static MachineEnergyComponent component(
            EnergyLimits limits,
            MachineEnergyAccess access,
            long storedEnergy,
            DirtyStateTracker dirtyState,
            AtomicInteger wakes) {
        return new MachineEnergyComponent(
                new SimulationEnergyState(limits, storedEnergy),
                access,
                dirtyState,
                wakes::incrementAndGet);
    }
}
