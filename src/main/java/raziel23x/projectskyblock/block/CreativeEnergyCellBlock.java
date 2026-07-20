package raziel23x.projectskyblock.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import raziel23x.projectskyblock.blockentity.CreativeEnergyCellBlockEntity;

public final class CreativeEnergyCellBlock extends BaseEntityBlock {
    public static final MapCodec<CreativeEnergyCellBlock> CODEC =
            simpleCodec(CreativeEnergyCellBlock::new);

    public CreativeEnergyCellBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeEnergyCellBlockEntity(pos, state);
    }

    @Override
    protected void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block neighborBlock,
            BlockPos neighborPos,
            boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!level.isClientSide
                && level.getBlockEntity(pos) instanceof CreativeEnergyCellBlockEntity cell) {
            cell.requestNeighborRecheck();
        }
    }

    @Override
    public void animateTick(
            BlockState state,
            Level level,
            BlockPos pos,
            RandomSource random) {
        if (random.nextInt(3) != 0) {
            return;
        }

        DustParticleOptions particle = new DustParticleOptions(
                new Vector3f(0.15F, 0.75F, 1.0F),
                1.0F
        );

        double x = pos.getX() + 0.15D + random.nextDouble() * 0.70D;
        double y = pos.getY() + 1.02D + random.nextDouble() * 0.18D;
        double z = pos.getZ() + 0.15D + random.nextDouble() * 0.70D;

        level.addParticle(
                particle,
                x, y, z,
                0.0D, 0.02D, 0.0D
        );
    }
}
