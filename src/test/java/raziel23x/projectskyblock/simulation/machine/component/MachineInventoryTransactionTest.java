package raziel23x.projectskyblock.simulation.machine.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import raziel23x.projectskyblock.simulation.core.DirtyFlag;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemKey;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;

class MachineInventoryTransactionTest {
    private static final SimulationItemKey IRON = SimulationItemKey.parse("minecraft:iron_ingot");
    private static final SimulationItemKey GOLD = SimulationItemKey.parse("minecraft:gold_ingot");

    @Test
    void multiSlotCommitIsAtomicAndWakesOnce() {
        DirtyStateTracker dirtyState = new DirtyStateTracker();
        AtomicInteger wakes = new AtomicInteger();
        MachineInventoryComponent inventory = new MachineInventoryComponent(
                List.of(
                        new MachineInventorySlotDefinition(
                                64L,
                                MachineInventoryAccess.INPUT,
                                MachineInventorySlotRule.ACCEPT_ALL),
                        new MachineInventorySlotDefinition(
                                64L,
                                MachineInventoryAccess.OUTPUT,
                                MachineInventorySlotRule.ACCEPT_ALL)),
                dirtyState,
                wakes::incrementAndGet);

        try (MachineInventoryTransaction transaction = inventory.beginTransaction()) {
            assertTrue(transaction.insert(0, stack(IRON, 5L)).isEmpty());
            assertTrue(transaction.store(1, stack(GOLD, 3L)).isEmpty());
            assertTrue(inventory.stack(0).isEmpty());
            assertTrue(inventory.stack(1).isEmpty());

            MachineInventoryCommitResult result = transaction.commit();

            assertTrue(result.changed());
            assertEquals(8L, result.insertedQuantity());
            assertEquals(0L, result.extractedQuantity());
            assertEquals(1L, result.stateVersion());
        }

        assertEquals(5L, inventory.stack(0).quantity());
        assertEquals(3L, inventory.stack(1).quantity());
        assertEquals(1, wakes.get());
        assertTrue(dirtyState.isDirty(DirtyFlag.PERSISTENCE));
        assertTrue(dirtyState.isDirty(DirtyFlag.CLIENT_SYNC));
        assertTrue(dirtyState.isDirty(DirtyFlag.SCHEDULER));
        assertEquals(1L, inventory.diagnostics().transactionCommitCount());
    }

    @Test
    void rollbackAndSimulationDoNotMutateOrWake() {
        AtomicInteger wakes = new AtomicInteger();
        MachineInventoryComponent inventory = new MachineInventoryComponent(
                List.of(MachineInventorySlotDefinition.unrestricted(64L)),
                new DirtyStateTracker(),
                wakes::incrementAndGet);

        try (MachineInventoryTransaction transaction = inventory.beginTransaction()) {
            transaction.store(0, stack(IRON, 4L));
            assertTrue(transaction.changed());
        }

        assertTrue(inventory.stack(0).isEmpty());
        assertEquals(0, wakes.get());
        assertEquals(0L, inventory.diagnostics().changeCount());
    }

    @Test
    void staleCandidateFailsClosed() {
        MachineInventoryComponent inventory = new MachineInventoryComponent(
                List.of(MachineInventorySlotDefinition.unrestricted(64L)));
        MachineInventoryTransaction first = inventory.beginTransaction();
        MachineInventoryTransaction stale = inventory.beginTransaction();

        first.store(0, stack(IRON, 1L));
        first.commit();
        stale.store(0, stack(GOLD, 1L));

        assertThrows(ConcurrentModificationException.class, stale::commit);
        assertEquals(IRON, inventory.stack(0).item());
        assertEquals(1L, inventory.diagnostics().transactionConflictCount());
        assertThrows(IllegalStateException.class, () -> stale.stack(0));
    }

    @Test
    void oneTransactionCanConsumeInputsAndStoreOutputsTogether() {
        MachineInventoryComponent inventory = new MachineInventoryComponent(List.of(
                new MachineInventorySlotDefinition(
                        64L,
                        MachineInventoryAccess.INPUT,
                        MachineInventorySlotRule.ACCEPT_ALL),
                new MachineInventorySlotDefinition(
                        64L,
                        MachineInventoryAccess.OUTPUT,
                        MachineInventorySlotRule.ACCEPT_ALL)));
        inventory.store(0, stack(IRON, 2L));

        try (MachineInventoryTransaction transaction = inventory.beginTransaction()) {
            assertEquals(1L, transaction.consume(0, 1L).quantity());
            assertTrue(transaction.store(1, stack(GOLD, 2L)).isEmpty());
            MachineInventoryCommitResult result = transaction.commit();
            assertEquals(1L, result.extractedQuantity());
            assertEquals(2L, result.insertedQuantity());
        }

        assertEquals(1L, inventory.stack(0).quantity());
        assertEquals(2L, inventory.stack(1).quantity());
    }

    private static SimulationItemStack stack(SimulationItemKey key, long quantity) {
        return SimulationItemStack.of(key, quantity, 64L);
    }
}
