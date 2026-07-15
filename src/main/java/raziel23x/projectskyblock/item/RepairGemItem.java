package raziel23x.projectskyblock.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import raziel23x.projectskyblock.config.CommonConfig;

import java.util.List;

public final class RepairGemItem extends Item {
    public RepairGemItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.projectskyblock.repair_gem.line_1")
                .withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.projectskyblock.repair_gem.line_2")
                .withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!entity.level().isClientSide
                || !CommonConfig.REPAIR_GEM_DROPPED_PARTICLES.get()) {
            return false;
        }

        int interval = CommonConfig.REPAIR_GEM_DROPPED_PARTICLE_INTERVAL.get();
        if (interval <= 0 || entity.tickCount % interval != 0) {
            return false;
        }

        double x = entity.getX();
        double y = entity.getY() + 0.18D;
        double z = entity.getZ();

        for (int i = 0; i < 2; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5D) * 0.35D;
            double offsetY = entity.getRandom().nextDouble() * 0.25D;
            double offsetZ = (entity.getRandom().nextDouble() - 0.5D) * 0.35D;

            entity.level().addParticle(
                    ParticleTypes.HAPPY_VILLAGER,
                    x + offsetX,
                    y + offsetY,
                    z + offsetZ,
                    0.0D,
                    0.01D,
                    0.0D
            );
        }

        if (entity.getRandom().nextInt(5) == 0) {
            entity.level().addParticle(
                    ParticleTypes.END_ROD,
                    x,
                    y + 0.1D,
                    z,
                    0.0D,
                    0.015D,
                    0.0D
            );
        }

        return false;
    }
}
