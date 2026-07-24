package raziel23x.projectskyblock.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import raziel23x.projectskyblock.blockentity.MaterialCrusherBlockEntity;

public final class MaterialCrusherBlock extends BaseEntityBlock {
    public static final MapCodec<MaterialCrusherBlock> CODEC =
            simpleCodec(MaterialCrusherBlock::new);

    public static final DirectionProperty FACING =
            BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final IntegerProperty GEAR_FRAME = IntegerProperty.create("gear_frame", 0, 3);

    public MaterialCrusherBlock(Properties properties) {
        super(properties);
        registerDefaultState(
                stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(ACTIVE, false)
                        .setValue(GEAR_FRAME, 0)
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
        builder.add(FACING, ACTIVE, GEAR_FRAME);
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
        return new MaterialCrusherBlockEntity(pos, state);
    }

    @Override
    public void animateTick(
            BlockState state,
            Level level,
            BlockPos pos,
            RandomSource random) {
        if (!state.getValue(ACTIVE)) {
            return;
        }

        Direction front = state.getValue(FACING);
        Direction back = front.getOpposite();

        if (random.nextInt(3) == 0) {
            double frontX = pos.getX() + 0.5D + front.getStepX() * 0.52D;
            double frontY = pos.getY() + 0.48D + random.nextDouble() * 0.34D;
            double frontZ = pos.getZ() + 0.5D + front.getStepZ() * 0.52D;

            level.addParticle(
                    ParticleTypes.POOF,
                    frontX, frontY, frontZ,
                    front.getStepX() * 0.025D,
                    0.018D,
                    front.getStepZ() * 0.025D
            );

            if (level.getBlockEntity(pos) instanceof MaterialCrusherBlockEntity crusher) {
                ItemStack input = crusher.getInputStackForVisuals();
                Block inputBlock = Block.byItem(input.getItem());
                if (!input.isEmpty() && inputBlock != net.minecraft.world.level.block.Blocks.AIR) {
                    level.addParticle(
                            new BlockParticleOption(
                                    ParticleTypes.FALLING_DUST,
                                    inputBlock.defaultBlockState()
                            ),
                            frontX,
                            frontY,
                            frontZ,
                            front.getStepX() * 0.012D,
                            0.010D,
                            front.getStepZ() * 0.012D
                    );
                } else if (random.nextBoolean()) {
                    level.addParticle(
                            ParticleTypes.ASH,
                            frontX,
                            frontY,
                            frontZ,
                            front.getStepX() * 0.010D,
                            0.008D,
                            front.getStepZ() * 0.010D
                    );
                }
            }
        }

        if (random.nextInt(2) == 0) {
            double backX = pos.getX() + 0.5D + back.getStepX() * 0.53D;
            double backY = pos.getY() + 0.62D + random.nextDouble() * 0.22D;
            double backZ = pos.getZ() + 0.5D + back.getStepZ() * 0.53D;

            level.addParticle(
                    random.nextInt(4) == 0
                            ? ParticleTypes.CAMPFIRE_COSY_SMOKE
                            : ParticleTypes.SMOKE,
                    backX,
                    backY,
                    backZ,
                    back.getStepX() * 0.012D,
                    0.035D + random.nextDouble() * 0.018D,
                    back.getStepZ() * 0.012D
            );

            if (random.nextInt(3) == 0) {
                level.addParticle(
                        ParticleTypes.ASH,
                        backX,
                        backY,
                        backZ,
                        back.getStepX() * 0.008D,
                        0.020D,
                        back.getStepZ() * 0.008D
                );
            }
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
                instanceof MaterialCrusherBlockEntity crusher) {
            player.openMenu(crusher);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
