package raziel23x.projectskyblock.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import raziel23x.projectskyblock.block.ResourceGeneratorBlock;
import raziel23x.projectskyblock.config.GeneratorConfig;

public final class GeneratorBlockItem extends BlockItem {
    private final ResourceGeneratorBlock.Output output;

    public GeneratorBlockItem(
            Block block,
            Properties properties,
            ResourceGeneratorBlock.Output output) {
        super(block, properties);
        this.output = output;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!entity.level().isClientSide
                || !GeneratorConfig.PARTICLES_ENABLED.get()) {
            return false;
        }

        int interval = switch (output) {
            case COBBLESTONE -> GeneratorConfig.COBBLESTONE_PARTICLE_CHANCE.get();
            case WATER -> GeneratorConfig.WATER_PARTICLE_CHANCE.get();
            case LAVA -> GeneratorConfig.LAVA_PARTICLE_CHANCE.get();
        };

        // Reuse the configured chance as a dropped-item pulse interval.
        // Clamp it so the effect stays visible without becoming noisy.
        interval = Math.max(4, Math.min(interval * 2, 40));

        if (entity.tickCount % interval != 0) {
            return false;
        }

        double x = entity.getX();
        double y = entity.getY() + 0.18D;
        double z = entity.getZ();

        for (int i = 0; i < 2; i++) {
            double angle = entity.getRandom().nextDouble() * Math.PI * 2.0D;
            double radius = 0.16D + entity.getRandom().nextDouble() * 0.14D;
            double px = x + Math.cos(angle) * radius;
            double py = y + entity.getRandom().nextDouble() * 0.20D;
            double pz = z + Math.sin(angle) * radius;

            switch (output) {
                case COBBLESTONE -> {
                    entity.level().addParticle(
                            ParticleTypes.POOF,
                            px, py, pz,
                            0.0D, 0.012D, 0.0D
                    );

                    if (entity.getRandom().nextInt(4) == 0) {
                        entity.level().addParticle(
                                ParticleTypes.ASH,
                                px, py, pz,
                                0.0D, 0.008D, 0.0D
                        );
                    }
                }

                case WATER -> {
                    entity.level().addParticle(
                            ParticleTypes.DRIPPING_WATER,
                            px, py, pz,
                            0.0D, -0.01D, 0.0D
                    );

                    entity.level().addParticle(
                            ParticleTypes.SPLASH,
                            px, py + 0.02D, pz,
                            0.0D, 0.025D, 0.0D
                    );
                }

                case LAVA -> {
                    entity.level().addParticle(
                            ParticleTypes.SMOKE,
                            px, py, pz,
                            0.0D, 0.018D, 0.0D
                    );

                    if (entity.getRandom().nextInt(3) == 0) {
                        entity.level().addParticle(
                                ParticleTypes.LAVA,
                                px, py, pz,
                                0.0D, 0.0D, 0.0D
                        );
                    }
                }
            }
        }

        return false;
    }
}
