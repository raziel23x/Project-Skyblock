package raziel23x.projectskyblock.platform.neoforge.machine.crusher;

import java.util.Objects;
import java.util.function.BiPredicate;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

/** Stateless validation wrapper over an engine-owned crusher inventory view. */
public final class MaterialCrusherItemHandler implements IItemHandler {
    private final IItemHandler delegate;
    private final BiPredicate<Integer, ItemStack> insertionValidator;

    public MaterialCrusherItemHandler(
            IItemHandler delegate,
            BiPredicate<Integer, ItemStack> insertionValidator) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.insertionValidator = Objects.requireNonNull(insertionValidator, "insertionValidator");
    }

    @Override
    public int getSlots() {
        return delegate.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return delegate.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        Objects.requireNonNull(stack, "stack");
        return insertionValidator.test(slot, stack)
                ? delegate.insertItem(slot, stack, simulate)
                : stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return delegate.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return delegate.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        Objects.requireNonNull(stack, "stack");
        return insertionValidator.test(slot, stack) && delegate.isItemValid(slot, stack);
    }
}
