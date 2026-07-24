package raziel23x.projectskyblock.simulation.machine.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemKey;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;

class MachineInventoryViewTest {
    private static final SimulationItemKey IRON = SimulationItemKey.parse("minecraft:iron_ingot");
    private static final SimulationItemKey GOLD = SimulationItemKey.parse("minecraft:gold_ingot");

    @Test
    void automationAndMenuExtractionUseDifferentContracts() {
        MachineInventoryComponent inventory = new MachineInventoryComponent(List.of(
                new MachineInventorySlotDefinition(64L, MachineInventoryAccess.INPUT, IRON::equals)));
        inventory.store(0, stack(IRON, 4L));
        MachineInventoryView automation = MachineInventoryView.automation(
                inventory,
                new int[] {0},
                true,
                true,
                "input automation");
        MachineInventoryView menu = MachineInventoryView.menu(
                inventory,
                new int[] {0},
                true,
                true,
                "input menu");

        try (MachineInventoryTransaction transaction = inventory.beginTransaction()) {
            assertTrue(automation.extract(transaction, 0, 1L).isEmpty());
        }
        try (MachineInventoryTransaction transaction = inventory.beginTransaction()) {
            assertEquals(1L, menu.extract(transaction, 0, 1L).quantity());
            transaction.commit();
        }

        assertEquals(3L, inventory.stack(0).quantity());
        assertEquals(MachineInventoryViewKind.AUTOMATION, automation.kind());
        assertEquals(MachineInventoryViewKind.MENU, menu.kind());
    }

    @Test
    void viewMappingAndInsertionRulesRemainExplicit() {
        MachineInventoryComponent inventory = new MachineInventoryComponent(List.of(
                new MachineInventorySlotDefinition(16L, MachineInventoryAccess.INPUT, IRON::equals),
                new MachineInventorySlotDefinition(64L, MachineInventoryAccess.OUTPUT, item -> true)));
        MachineInventoryView input = MachineInventoryView.automation(
                inventory,
                new int[] {0},
                true,
                false,
                "top input");

        assertTrue(input.isItemValid(0, stack(IRON, 1L)));
        assertFalse(input.isItemValid(0, stack(GOLD, 1L)));
        assertEquals(16L, input.slotCapacity(0));
        assertThrows(IndexOutOfBoundsException.class, () -> input.stack(1));
        assertThrows(IllegalArgumentException.class, () -> MachineInventoryView.automation(
                inventory,
                new int[] {0, 0},
                true,
                false,
                "duplicate"));
    }

    @Test
    void viewRejectsTransactionsFromAnotherInventory() {
        MachineInventoryComponent first = new MachineInventoryComponent(
                List.of(MachineInventorySlotDefinition.unrestricted(64L)));
        MachineInventoryComponent second = new MachineInventoryComponent(
                List.of(MachineInventorySlotDefinition.unrestricted(64L)));
        MachineInventoryView view = MachineInventoryView.menu(
                first,
                new int[] {0},
                true,
                true,
                "menu");

        try (MachineInventoryTransaction transaction = second.beginTransaction()) {
            assertThrows(IllegalArgumentException.class,
                    () -> view.insert(transaction, 0, stack(IRON, 1L)));
        }
    }

    private static SimulationItemStack stack(SimulationItemKey key, long quantity) {
        return SimulationItemStack.of(key, quantity, 64L);
    }
}
