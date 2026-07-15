package raziel23x.projectskyblock.machine.crusher;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.items.ItemStackHandler;

public final class CrusherInventory extends ItemStackHandler {
    public static final int INPUT_SLOT = 0;
    public static final int FUEL_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    public static final int BYPRODUCT_SLOT = 3;
    public static final int SLOT_COUNT = 4;

    private final Runnable changeListener;

    public CrusherInventory(Runnable changeListener) {
        super(SLOT_COUNT);
        this.changeListener = changeListener;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        validateSlotIndex(slot);
        return switch (slot) {
            case INPUT_SLOT -> CrusherProcessing.isValidInput(stack);
            case FUEL_SLOT -> stack.getBurnTime(RecipeType.SMELTING) > 0;
            case OUTPUT_SLOT, BYPRODUCT_SLOT -> false;
            default -> false;
        };
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        changeListener.run();
    }
}
