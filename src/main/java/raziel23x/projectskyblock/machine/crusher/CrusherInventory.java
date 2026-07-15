package raziel23x.projectskyblock.machine.crusher;

import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import raziel23x.projectskyblock.machine.base.BaseMachineInventory;

public final class CrusherInventory extends BaseMachineInventory {
    public static final int INPUT_SLOT = 0;
    public static final int FUEL_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    public static final int BYPRODUCT_SLOT = 3;
    public static final int SLOT_COUNT = 4;

    public CrusherInventory(
            Runnable changeListener,
            Predicate<ItemStack> inputValidator) {
        super(
                SLOT_COUNT,
                changeListener,
                (slot, stack) -> switch (slot) {
                    case INPUT_SLOT -> inputValidator.test(stack);
                    case FUEL_SLOT -> stack.getBurnTime(RecipeType.SMELTING) > 0;
                    case OUTPUT_SLOT, BYPRODUCT_SLOT -> false;
                    default -> false;
                }
        );
    }
}
