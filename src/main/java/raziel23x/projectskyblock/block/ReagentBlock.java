package raziel23x.projectskyblock.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import raziel23x.projectskyblock.config.CommonConfig;

public final class ReagentBlock extends Block {
    private final Vector3f particleColor;

    public ReagentBlock(Properties properties, Vector3f particleColor) {
        super(properties);
        this.particleColor = particleColor;
    }

    @Override
    public void animateTick(
            BlockState state,
            Level level,
            BlockPos pos,
            RandomSource random) {
        if (!CommonConfig.REAGENT_PARTICLES_ENABLED.get()) {
            return;
        }

        int chance = CommonConfig.REAGENT_BLOCK_PARTICLE_CHANCE.get();
        if (chance <= 0 || random.nextInt(chance) != 0) {
            return;
        }

        DustParticleOptions particle = new DustParticleOptions(
                new Vector3f(particleColor),
                1.15F
        );

        // Spawn above and just outside the block. The previous version spawned
        // particles inside the solid cube, where they were hidden by the model.
        for (int i = 0; i < 2; i++) {
            double x = pos.getX() + 0.15D + random.nextDouble() * 0.70D;
            double y = pos.getY() + 1.02D + random.nextDouble() * 0.22D;
            double z = pos.getZ() + 0.15D + random.nextDouble() * 0.70D;

            level.addParticle(
                    particle,
                    x,
                    y,
                    z,
                    (random.nextDouble() - 0.5D) * 0.01D,
                    0.018D + random.nextDouble() * 0.012D,
                    (random.nextDouble() - 0.5D) * 0.01D
            );
        }

        // Occasionally add a mote beside the block for a fuller aura.
        if (random.nextInt(3) == 0) {
            int side = random.nextInt(4);
            double x = pos.getX() + (side == 0 ? -0.03D : side == 1 ? 1.03D : random.nextDouble());
            double z = pos.getZ() + (side == 2 ? -0.03D : side == 3 ? 1.03D : random.nextDouble());
            double y = pos.getY() + 0.25D + random.nextDouble() * 0.65D;

            level.addParticle(
                    particle,
                    x,
                    y,
                    z,
                    0.0D,
                    0.012D,
                    0.0D
            );
        }
    }
}
