package raziel23x.projectskyblock.items.gems;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemCustomRepairGem extends Item
{

    public ItemCustomRepairGem(Properties properties)
    {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add((new TranslationTextComponent("item.projectskyblock.repair_gem.line1").mergeStyle(TextFormatting.GREEN)));
        tooltip.add((new TranslationTextComponent("item.projectskyblock.repair_gem.line2").mergeStyle(TextFormatting.YELLOW)));
    }
}
