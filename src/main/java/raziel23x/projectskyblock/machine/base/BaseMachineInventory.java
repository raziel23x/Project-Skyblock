package raziel23x.projectskyblock.machine.base;

import java.util.function.BiPredicate;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

/** Shared inventory implementation with centralized validation and dirty marking. */
public class BaseMachineInventory extends ItemStackHandler {
    private final Runnable changeListener;
    private final BiPredicate<Integer, ItemStack> validator;

    public BaseMachineInventory(
            int slotCount,
            Runnable changeListener,
            BiPredicate<Integer, ItemStack> validator) {
        super(slotCount);
        this.changeListener = changeListener;
        this.validator = validator;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        validateSlotIndex(slot);
        return validator.test(slot, stack);
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        changeListener.run();
    }
}
