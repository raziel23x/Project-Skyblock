package raziel23x.projectskyblock.gameObjs.items;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.utils.ShearsUtil;

public class FlintShears extends ShearsItem {
    public FlintShears() {
        super(new Item.Properties()
                .maxStackSize(1)
                .maxDamage(100)
                .group(ProjectSkyblock.TAB)
        );
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stackIn, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (ProjectSkyblock.CONFIGURATION.FlintShearsVanillaDrops.get()) {
            return super.onBlockDestroyed(stackIn, worldIn, state, pos, entityLiving);
        }

        return false;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ShearsUtil.onUseItem(context);
        return ActionResultType.PASS;
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity entity, Hand hand) {
        if (ProjectSkyblock.CONFIGURATION.FlintShearsVanillaDrops.get()) {
            return super.itemInteractionForEntity(stack, playerIn, entity, hand);
        }

        return ActionResultType.FAIL;
    }
}
