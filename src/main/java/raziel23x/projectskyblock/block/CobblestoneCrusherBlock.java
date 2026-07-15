package raziel23x.projectskyblock.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import raziel23x.projectskyblock.blockentity.CobblestoneCrusherBlockEntity;
import raziel23x.projectskyblock.registry.ModBlockEntities;

public final class CobblestoneCrusherBlock extends BaseEntityBlock {
    public static final MapCodec<CobblestoneCrusherBlock> CODEC =
            simpleCodec(CobblestoneCrusherBlock::new);

    public static final DirectionProperty FACING =
            BlockStateProperties.HORIZONTAL_FACING;

    public CobblestoneCrusherBlock(Properties properties) {
        super(properties);
        registerDefaultState(
                stateDefinition.any().setValue(FACING, Direction.NORTH)
        );
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
    protected void createBlockStateDefinition(
            StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(
                FACING,
                context.getHorizontalDirection().getOpposite()
        );
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(
                FACING,
                rotation.rotate(state.getValue(FACING))
        );
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return rotate(
                state,
                mirror.getRotation(state.getValue(FACING))
        );
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CobblestoneCrusherBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }

        return createTickerHelper(
                type,
                ModBlockEntities.COBBLESTONE_CRUSHER.get(),
                CobblestoneCrusherBlockEntity::serverTick
        );
    }

    @Override
    public void animateTick(
            BlockState state,
            Level level,
            BlockPos pos,
            RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof CobblestoneCrusherBlockEntity crusher)
                || !crusher.isWorking()) {
            return;
        }

        if (random.nextInt(3) != 0) {
            return;
        }

        Direction front = state.getValue(FACING);
        double x = pos.getX() + 0.5D + front.getStepX() * 0.52D;
        double y = pos.getY() + 0.55D + random.nextDouble() * 0.30D;
        double z = pos.getZ() + 0.5D + front.getStepZ() * 0.52D;

        level.addParticle(
                ParticleTypes.POOF,
                x, y, z,
                front.getStepX() * 0.025D,
                0.018D,
                front.getStepZ() * 0.025D
        );

        if (random.nextInt(4) == 0) {
            level.addParticle(
                    ParticleTypes.ASH,
                    x, y, z,
                    front.getStepX() * 0.012D,
                    0.008D,
                    front.getStepZ() * 0.012D
            );
        }
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult) {
        if (!level.isClientSide
                && level.getBlockEntity(pos)
                instanceof CobblestoneCrusherBlockEntity crusher) {
            player.openMenu(crusher);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
