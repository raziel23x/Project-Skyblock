package raziel23x.projectskyblock.item;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import raziel23x.projectskyblock.config.CommonConfig;

public final class ReagentItem extends Item {
    private final Vector3f particleColor;

    public ReagentItem(Properties properties, Vector3f particleColor) {
        super(properties);
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
                0.8F
        );

        double x = entity.getX();
        double y = entity.getY() + 0.12D;
        double z = entity.getZ();

        for (int i = 0; i < 2; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5D) * 0.28D;
            double offsetY = entity.getRandom().nextDouble() * 0.20D;
            double offsetZ = (entity.getRandom().nextDouble() - 0.5D) * 0.28D;

            entity.level().addParticle(
                    particle,
                    x + offsetX,
                    y + offsetY,
                    z + offsetZ,
                    0.0D,
                    0.008D,
                    0.0D
            );
        }

        return false;
    }
}
