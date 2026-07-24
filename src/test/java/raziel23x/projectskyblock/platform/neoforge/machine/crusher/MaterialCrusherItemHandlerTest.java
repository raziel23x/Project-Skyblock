package raziel23x.projectskyblock.platform.neoforge.machine.crusher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.junit.jupiter.api.Test;

class MaterialCrusherItemHandlerTest {
    @Test
    void rejectsInvalidInsertionsWithoutMutatingTheDelegate() {
        ItemStackHandler delegate = new ItemStackHandler(1);
        MaterialCrusherItemHandler handler = new MaterialCrusherItemHandler(
                delegate,
                (slot, stack) -> stack.is(Items.COBBLESTONE));

        ItemStack rejected = new ItemStack(Items.DIRT, 4);
        assertEquals(4, handler.insertItem(0, rejected, false).getCount());
        assertTrue(delegate.getStackInSlot(0).isEmpty());
        assertFalse(handler.isItemValid(0, rejected));

        ItemStack accepted = new ItemStack(Items.COBBLESTONE, 3);
        assertTrue(handler.insertItem(0, accepted, false).isEmpty());
        assertEquals(3, delegate.getStackInSlot(0).getCount());
        assertTrue(handler.isItemValid(0, accepted));
    }

    @Test
    void extractionRemainsControlledByTheWrappedView() {
        ItemStackHandler delegate = new ItemStackHandler(1);
        delegate.setStackInSlot(0, new ItemStack(Items.IRON_INGOT, 5));
        MaterialCrusherItemHandler handler = new MaterialCrusherItemHandler(
                delegate,
                (slot, stack) -> false);

        assertEquals(2, handler.extractItem(0, 2, false).getCount());
        assertEquals(3, delegate.getStackInSlot(0).getCount());
    }
}
