package raziel23x.projectskyblock.items.mixingbowl;

import net.minecraft.item.ItemStack;

public class MixingBowl extends ItemBaseMixingBowl {
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
