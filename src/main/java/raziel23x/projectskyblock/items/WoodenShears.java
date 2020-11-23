package raziel23x.projectskyblock.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IShearable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IForgeShearable;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.config.Config;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class WoodenShears extends ShearsItem {

    public WoodenShears() {
        super(new Properties()
                .maxStackSize(1)
                .maxDamage(100)
                .group(ProjectSkyblock.TAB)
        );
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
        player.setActiveHand(hand);
        return ActionResult.resultConsume(player.getHeldItem(hand));
    }

    @Override
    public void onUse(World world, @Nonnull LivingEntity living, @Nonnull ItemStack stack, int count) {
        if (world.isRemote) {
            return;
        }

        if (count != getUseDuration(stack) && count % 5 == 0) {
            int range = 12;
            Predicate<Entity> shearablePred = e -> e instanceof IShearable || e instanceof IForgeShearable;
            List<Entity> shearable = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(living.getPosX() - range, living.getPosY() - range, living.getPosZ() - range, living.getPosX() + range, living.getPosY() + range, living.getPosZ() + range), shearablePred);
            if (shearable.size() > 0) {
                for (Entity entity : shearable) {
                    if (entity instanceof IShearable && ((IShearable) entity).isShearable()) {
                        ((IShearable) entity).shear(living.getSoundCategory());
                        stack.damageItem(1, living, l -> l.sendBreakAnimation(l.getActiveHand()));
                        break;
                    } else {
                        IForgeShearable target = (IForgeShearable) entity;
                        if (target.isShearable(stack, entity.world, entity.getPosition())) {
                            PlayerEntity player = living instanceof PlayerEntity ? (PlayerEntity) living : null;
                            List<ItemStack> drops = target.onSheared(player, stack, entity.world, entity.getPosition(), EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));

                            for (ItemStack drop : drops) {
                                entity.entityDropItem(drop, 1.0F);
                            }

                            stack.damageItem(1, living, l -> l.sendBreakAnimation(l.getActiveHand()));
                            break;
                        }
                    }

                }
            }
        }
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
        if (Config.DROP_WOODEN_SHEARS_PRODUCTS.get()) {
            Block block = state.getBlock();
            if (isPresentOnTag(BlockTags.LEAVES, state) || block == Blocks.GRASS || block == Blocks.FERN || block == Blocks.DEAD_BUSH || block == Blocks.VINE
                    || block == Blocks.PUMPKIN || block == Blocks.BEEHIVE || block == Blocks.BEE_NEST) {
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
    public boolean canHarvestBlock(BlockState blockIn) {
        return super.canHarvestBlock(blockIn);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity entity, Hand hand) {
        if (Config.DROP_WOODEN_SHEARS_PRODUCTS.get()) {
            return super.itemInteractionForEntity(stack, playerIn, entity, hand);
        }
        return ActionResultType.PASS;
    }
}
