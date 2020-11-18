package raziel23x.projectskyblock.items.mixingbowl;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import raziel23x.projectskyblock.ProjectSkyblock;

import java.util.List;

public class MixingBowl extends Item {
    public MixingBowl() {

        super(new Properties().group(ProjectSkyblock.TAB).maxStackSize(1));
    }
    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add((new TranslationTextComponent("tip."+ProjectSkyblock.MOD_ID+".mixingbowlline1").mergeStyle(TextFormatting.GREEN)));
        tooltip.add((new TranslationTextComponent("tip."+ProjectSkyblock.MOD_ID+".mixingbowlline2").mergeStyle(TextFormatting.YELLOW)));
        super.addInformation(stack, worldIn, tooltip, flagIn);
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
