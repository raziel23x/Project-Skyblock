package raziel23x.projectskyblock.items;

import net.minecraft.item.ItemStack;

public class MixingBowl extends ItemBase {
    public MixingBowl () {

    }

    @Override
    public boolean hasContainerItem (ItemStack stack){
        return true;
    }

    @Override
    public ItemStack getContainerItem (ItemStack stack){
        return stack.copy();
    }
}
