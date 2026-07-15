package raziel23x.projectskyblock.machine.crusher;

import net.minecraft.world.item.ItemStack;

public record CrusherProcessingResult(ItemStack primary, ItemStack byproduct) {
    public static final CrusherProcessingResult EMPTY =
            new CrusherProcessingResult(ItemStack.EMPTY, ItemStack.EMPTY);

    public boolean isEmpty() {
        return primary.isEmpty();
    }
}
