package raziel23x.projectskyblock.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.ShearsItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.utils.ShearsUtil;

import java.util.Random;

public class WoodenShears extends ShearsItem {
    public WoodenShears() {
        super(new Properties()
                .maxStackSize(1)
                .maxDamage(100)
                .group(ProjectSkyblock.TAB)
        );
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        ItemStack container = itemStack.copy();
        if (container.attemptDamageItem(1, new Random(), null))
            return ItemStack.EMPTY;
        else
            return container;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stackIn, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        //ProjectSkyblock.LOGGER.info("SHEARS: Here");

        if (ProjectSkyblock.CONFIGURATION.WoodenShearsVanillaDrops.get()) {
            Block block = state.getBlock();
            if (isPresentOnTag(BlockTags.LEAVES, state) || block == Blocks.GRASS || block == Blocks.TALL_GRASS
                    || block == Blocks.FERN || block == Blocks.DEAD_BUSH || block == Blocks.VINE
                    || block == Blocks.ROSE_BUSH || block == Blocks.POPPY || block == Blocks.BLUE_ORCHID
                    || block == Blocks.SEAGRASS || block == Blocks.DANDELION || block == Blocks.NETHER_SPROUTS) {
                Block.spawnAsEntity(worldIn, pos, new ItemStack(state.getBlock().asItem()));
            }
        }
        return super.onBlockDestroyed(stackIn, worldIn, state, pos, entityLiving);
    }

    public boolean isPresentOnTag(ITag.INamedTag<Block> tag, BlockState state) {
        for (Block block : tag.getAllElements()) {
            if (state.isIn(block)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ShearsUtil.onUseItem(context);
        return ActionResultType.PASS;
    }

    @Override
    public boolean canHarvestBlock(BlockState blockIn) {
        return super.canHarvestBlock(blockIn);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity entity, Hand hand) {
        if (ProjectSkyblock.CONFIGURATION.WoodenShearsVanillaDrops.get()) {
            return super.itemInteractionForEntity(stack, playerIn, entity, hand);
        }
        return ActionResultType.FAIL;
    }
}
