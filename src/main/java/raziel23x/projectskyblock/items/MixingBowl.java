package raziel23x.projectskyblock.items;

import net.minecraft.item.ItemStack;
import raziel23x.projectskyblock.utils.RegistryHandler;



public class MixingBowl extends ItemBase {
    public MixingBowl () {
        super(new Item.Properties().group(ProjectSkyblock.TAB));
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
