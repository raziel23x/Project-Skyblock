package raziel23x.projectskyblock.simulation.machine.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import raziel23x.projectskyblock.simulation.core.DirtyFlag;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemKey;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;

class MachineInventoryComponentTest {
    private static final SimulationItemKey IRON = SimulationItemKey.parse("minecraft:iron_ingot");
    private static final SimulationItemKey GOLD = SimulationItemKey.parse("minecraft:gold_ingot");

    @Test
    void insertionRespectsCapacityAndReturnsRemainder() {
        MachineInventoryComponent inventory = inventory(MachineInventoryAccess.BIDIRECTIONAL, item -> true, 64L);

        SimulationItemStack remainder = inventory.insert(0, SimulationItemStack.of(IRON, 80L, 99L));

        assertEquals(64L, inventory.stack(0).quantity());
        assertEquals(16L, remainder.quantity());
        assertEquals(64L, inventory.diagnostics().totalInserted());
    }

    @Test
    void insertionRejectsWrongItemAndOccupiedDifferentItem() {
        MachineInventoryComponent inventory = inventory(MachineInventoryAccess.BIDIRECTIONAL, IRON::equals, 64L);

        SimulationItemStack rejected = inventory.insert(0, SimulationItemStack.of(GOLD, 4L, 64L));
        assertEquals(4L, rejected.quantity());
        assertTrue(inventory.stack(0).isEmpty());

        inventory.store(0, SimulationItemStack.of(IRON, 2L, 64L));
        rejected = inventory.store(0, SimulationItemStack.of(GOLD, 2L, 64L));
        assertEquals(2L, rejected.quantity());
        assertEquals(IRON, inventory.stack(0).item());
    }

    @Test
    void externalAccessAndInternalOperationsAreSeparate() {
        MachineInventoryComponent input = inventory(MachineInventoryAccess.INPUT, item -> true, 64L);
        input.insert(0, SimulationItemStack.of(IRON, 8L, 64L));
        assertTrue(input.extract(0, 2L).isEmpty());
        assertEquals(2L, input.consume(0, 2L).quantity());

        MachineInventoryComponent output = inventory(MachineInventoryAccess.OUTPUT, item -> true, 64L);
        assertEquals(8L, output.insert(0, SimulationItemStack.of(IRON, 8L, 64L)).quantity());
        assertTrue(output.store(0, SimulationItemStack.of(IRON, 8L, 64L)).isEmpty());
        assertEquals(3L, output.extract(0, 3L).quantity());
    }

    @Test
    void changesMarkDirtyAndWakeOwner() {
        DirtyStateTracker dirty = new DirtyStateTracker();
        AtomicInteger wakes = new AtomicInteger();
        MachineInventoryComponent inventory = new MachineInventoryComponent(
                List.of(MachineInventorySlotDefinition.unrestricted(64L)), dirty, wakes::incrementAndGet);

        inventory.insert(0, SimulationItemStack.of(IRON, 1L, 64L));

        assertTrue(dirty.isDirty(DirtyFlag.PERSISTENCE));
        assertTrue(dirty.isDirty(DirtyFlag.CLIENT_SYNC));
        assertTrue(dirty.isDirty(DirtyFlag.SCHEDULER));
        assertEquals(1, wakes.get());
        assertEquals(1L, inventory.diagnostics().changeCount());
    }

    @Test
    void restoreValidatesSlotRulesAndCapacity() {
        MachineInventoryComponent inventory = inventory(MachineInventoryAccess.INPUT, IRON::equals, 16L);

        inventory.restore(List.of(SimulationItemStack.of(IRON, 12L, 64L)));
        assertEquals(12L, inventory.stack(0).quantity());

        assertThrows(IllegalArgumentException.class,
                () -> inventory.restore(List.of(SimulationItemStack.of(GOLD, 1L, 64L))));
        assertThrows(IllegalArgumentException.class,
                () -> inventory.restore(List.of(SimulationItemStack.of(IRON, 17L, 64L))));
    }

    @Test
    void invalidSlotsAndNegativeRequestsFailFast() {
        MachineInventoryComponent inventory = inventory(MachineInventoryAccess.BIDIRECTIONAL, item -> true, 64L);

        assertThrows(IndexOutOfBoundsException.class, () -> inventory.stack(1));
        assertThrows(IllegalArgumentException.class, () -> inventory.extract(0, -1L));
        inventory.store(0, SimulationItemStack.of(IRON, 64L, 64L));
        assertFalse(inventory.canInsert(0, SimulationItemStack.of(IRON, 1L, 64L)));
    }

    private static MachineInventoryComponent inventory(
            MachineInventoryAccess access,
            MachineInventorySlotRule rule,
            long capacity) {
        return new MachineInventoryComponent(List.of(
                new MachineInventorySlotDefinition(capacity, access, rule)));
    }
}
