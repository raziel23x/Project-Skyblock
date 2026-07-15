package raziel23x.projectskyblock.item;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.joml.Vector3f;
import raziel23x.projectskyblock.config.CommonConfig;

public final class ReagentBlockItem extends BlockItem {
    private final Vector3f particleColor;

    public ReagentBlockItem(
            Block block,
            Properties properties,
            Vector3f particleColor) {
        super(block, properties);
        this.particleColor = particleColor;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!entity.level().isClientSide
                || !CommonConfig.REAGENT_PARTICLES_ENABLED.get()) {
            return false;
        }

        int interval = CommonConfig.REAGENT_DROPPED_PARTICLE_INTERVAL.get();
        if (interval <= 0 || entity.tickCount % interval != 0) {
            return false;
        }

        DustParticleOptions particle = new DustParticleOptions(
                new Vector3f(particleColor),
                1.1F
        );

        double x = entity.getX();
        double y = entity.getY() + 0.18D;
        double z = entity.getZ();

        for (int i = 0; i < 3; i++) {
            double angle = entity.getRandom().nextDouble() * Math.PI * 2.0D;
            double radius = 0.18D + entity.getRandom().nextDouble() * 0.12D;

            entity.level().addParticle(
                    particle,
                    x + Math.cos(angle) * radius,
                    y + entity.getRandom().nextDouble() * 0.22D,
                    z + Math.sin(angle) * radius,
                    Math.cos(angle) * 0.006D,
                    0.014D,
                    Math.sin(angle) * 0.006D
            );
        }

        return false;
    }
}
