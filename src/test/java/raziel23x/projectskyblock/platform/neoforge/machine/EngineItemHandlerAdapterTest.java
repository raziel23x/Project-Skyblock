package raziel23x.projectskyblock.platform.neoforge.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;
import raziel23x.projectskyblock.platform.neoforge.inventory.MinecraftItemStackCodec;
import raziel23x.projectskyblock.platform.neoforge.machine.capability.EngineItemHandlerAdapter;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemKey;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemState;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventoryAccess;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventoryComponent;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventorySlotDefinition;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventoryView;

class EngineItemHandlerAdapterTest {
    private final MinecraftItemStackCodec codec =
            new MinecraftItemStackCodec(
                    () -> RegistryLayer.createRegistryAccess().compositeAccess());

    @Test
    void simulatedInsertionDoesNotMutateAndRealInsertionWakesOnce() {
        AtomicInteger wakes = new AtomicInteger();
        MachineInventoryComponent inventory = new MachineInventoryComponent(
                List.of(new MachineInventorySlotDefinition(
                        16L,
                        MachineInventoryAccess.INPUT,
                        item -> item.serializedName().equals("minecraft:cobblestone"))),
                new DirtyStateTracker(),
                wakes::incrementAndGet);
        EngineItemHandlerAdapter adapter = new EngineItemHandlerAdapter(
                MachineInventoryView.automation(
                        inventory,
                        new int[] {0},
                        true,
                        false,
                        "top input"),
                codec);
        ItemStack offered = new ItemStack(Items.COBBLESTONE, 32);

        assertEquals(16, adapter.insertItem(0, offered, true).getCount());
        assertTrue(inventory.stack(0).isEmpty());
        assertEquals(0, wakes.get());

        assertEquals(16, adapter.insertItem(0, offered, false).getCount());
        assertEquals(16L, inventory.stack(0).quantity());
        assertEquals(1, wakes.get());
    }

    @Test
    void extractionConvertsBeforeCommitAndPreservesSimulationSemantics() {
        MachineInventoryComponent inventory = new MachineInventoryComponent(
                List.of(new MachineInventorySlotDefinition(
                        64L,
                        MachineInventoryAccess.OUTPUT,
                        item -> true)));
        inventory.store(0, codec.encode(new ItemStack(Items.GOLD_INGOT, 5)));
        EngineItemHandlerAdapter adapter = new EngineItemHandlerAdapter(
                MachineInventoryView.automation(
                        inventory,
                        new int[] {0},
                        false,
                        true,
                        "bottom output"),
                codec);

        assertEquals(2, adapter.extractItem(0, 2, true).getCount());
        assertEquals(5L, inventory.stack(0).quantity());
        assertEquals(2, adapter.extractItem(0, 2, false).getCount());
        assertEquals(3L, inventory.stack(0).quantity());
    }

    @Test
    void menuCanReturnInputWhileAutomationCannotExtractIt() {
        MachineInventoryComponent inventory = new MachineInventoryComponent(
                List.of(new MachineInventorySlotDefinition(
                        64L,
                        MachineInventoryAccess.INPUT,
                        item -> true)));
        inventory.store(0, codec.encode(new ItemStack(Items.IRON_INGOT, 4)));
        EngineItemHandlerAdapter automation = new EngineItemHandlerAdapter(
                MachineInventoryView.automation(
                        inventory,
                        new int[] {0},
                        true,
                        true,
                        "input automation"),
                codec);
        EngineItemHandlerAdapter menu = new EngineItemHandlerAdapter(
                MachineInventoryView.menu(
                        inventory,
                        new int[] {0},
                        true,
                        true,
                        "input menu"),
                codec);

        assertTrue(automation.extractItem(0, 1, false).isEmpty());
        assertEquals(1, menu.extractItem(0, 1, false).getCount());
        assertEquals(3L, inventory.stack(0).quantity());
    }

    @Test
    void distinctDataComponentsDoNotMerge() {
        MachineInventoryComponent inventory = new MachineInventoryComponent(
                List.of(new MachineInventorySlotDefinition(
                        1L,
                        MachineInventoryAccess.INPUT,
                        item -> true)));
        EngineItemHandlerAdapter adapter = new EngineItemHandlerAdapter(
                MachineInventoryView.automation(
                        inventory,
                        new int[] {0},
                        true,
                        false,
                        "tool input"),
                codec);
        ItemStack first = new ItemStack(Items.DIAMOND_PICKAXE);
        first.set(DataComponents.DAMAGE, 1);
        ItemStack second = new ItemStack(Items.DIAMOND_PICKAXE);
        second.set(DataComponents.DAMAGE, 2);

        assertTrue(adapter.insertItem(0, first, false).isEmpty());
        assertEquals(1, adapter.insertItem(0, second, false).getCount());
    }

    @Test
    void undecodableEngineStateIsNeverExtractedOrOverwritten() {
        MachineInventoryComponent inventory = new MachineInventoryComponent(
                List.of(new MachineInventorySlotDefinition(
                        1L,
                        MachineInventoryAccess.BIDIRECTIONAL,
                        item -> true)));
        SimulationItemStack unsupported = SimulationItemStack.of(
                new SimulationItemKey(
                        "minecraft",
                        "diamond_pickaxe",
                        SimulationItemState.opaque("example:unknown", new byte[] {1})),
                1L,
                1L);
        inventory.restore(List.of(unsupported));
        EngineItemHandlerAdapter adapter = new EngineItemHandlerAdapter(
                MachineInventoryView.automation(
                        inventory,
                        new int[] {0},
                        true,
                        true,
                        "corrupt boundary"),
                codec);

        assertTrue(adapter.getStackInSlot(0).isEmpty());
        assertTrue(adapter.extractItem(0, 1, false).isEmpty());
        assertEquals(unsupported, inventory.stack(0));
        assertEquals(1, adapter.insertItem(0, new ItemStack(Items.DIAMOND_PICKAXE), false).getCount());
        assertEquals(unsupported, inventory.stack(0));
    }
}
