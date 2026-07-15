package raziel23x.projectskyblock.machine.crusher;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public final class CrusherSidedItemHandler implements IItemHandler {
    private final CrusherInventory inventory;
    private final int[] slots;
    private final boolean allowInsert;
    private final boolean allowExtract;

    public CrusherSidedItemHandler(
            CrusherInventory inventory,
            int[] slots,
            boolean allowInsert,
            boolean allowExtract) {
        this.inventory = inventory;
        this.slots = slots.clone();
        this.allowInsert = allowInsert;
        this.allowExtract = allowExtract;
    }

    @Override
    public int getSlots() {
        return slots.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(mapSlot(slot));
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        int mapped = mapSlot(slot);
        return allowInsert
                ? inventory.insertItem(mapped, stack, simulate)
                : stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        int mapped = mapSlot(slot);
        return allowExtract
                ? inventory.extractItem(mapped, amount, simulate)
                : ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return inventory.getSlotLimit(mapSlot(slot));
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        int mapped = mapSlot(slot);
        return allowInsert && inventory.isItemValid(mapped, stack);
    }

    private int mapSlot(int slot) {
        if (slot < 0 || slot >= slots.length) {
            throw new IndexOutOfBoundsException(
                    "Crusher sided slot " + slot
                            + " outside valid range [0," + slots.length + ")"
            );
        }
        return slots[slot];
    }
}
