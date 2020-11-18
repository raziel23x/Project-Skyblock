package raziel23x.projectskyblock.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import raziel23x.projectskyblock.ProjectSkyblock;

import java.util.List;


public class BlockItemBaseWaterGenerator extends BlockItem {
    public BlockItemBaseWaterGenerator(Block block) {
        super(block, new Properties().group(ProjectSkyblock.TAB).maxStackSize(1));
    }
    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add((new TranslationTextComponent("tip."+ProjectSkyblock.MOD_ID+".WaterGeneratorline1").mergeStyle(TextFormatting.GREEN)));
        tooltip.add((new TranslationTextComponent("tip."+ProjectSkyblock.MOD_ID+".WaterGeneratorline2").mergeStyle(TextFormatting.YELLOW)));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }


}
