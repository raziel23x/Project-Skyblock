package raziel23x.projectskyblock.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import raziel23x.projectskyblock.ProjectSkyblock;

import java.util.List;

public class ItemRepairGem extends Item {
    public ItemRepairGem() {

        super(new Properties().group(ProjectSkyblock.TAB).maxStackSize(1));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

     //   tooltip.add(new StringTextComponent("Test Information"));

        tooltip.add((new TranslationTextComponent("item.projectskyblock.repair_gem.line1").mergeStyle(TextFormatting.GREEN)));
        tooltip.add((new TranslationTextComponent("item.projectskyblock.repair_gem.line2").mergeStyle(TextFormatting.YELLOW)));

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
