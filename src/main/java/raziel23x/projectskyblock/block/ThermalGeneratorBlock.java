package raziel23x.projectskyblock.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import raziel23x.projectskyblock.blockentity.ThermalGeneratorBlockEntity;
import raziel23x.projectskyblock.registry.ModBlockEntities;

public final class ThermalGeneratorBlock extends BaseEntityBlock {
    public static final MapCodec<ThermalGeneratorBlock> CODEC = simpleCodec(ThermalGeneratorBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final IntegerProperty FAN_FRAME = IntegerProperty.create("fan_frame", 0, 3);

    public ThermalGeneratorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ACTIVE, false)
                .setValue(FAN_FRAME, 0));
    }

    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING, ACTIVE, FAN_FRAME);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new ThermalGeneratorBlockEntity(pos, state); }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return createTickerHelper(type, ModBlockEntities.THERMAL_GENERATOR.get(), ThermalGeneratorBlockEntity::serverTick);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(ACTIVE)) return;

        Direction front = state.getValue(FACING);
        Direction back = front.getOpposite();

        if (random.nextInt(2) == 0) {
            // The rear of the Mk I is a full-face radiator/exhaust grille. Spread
            // exhaust across the complete panel instead of emitting from one corner.
            Direction sideways = back.getClockWise();
            int particleCount = 1 + random.nextInt(2);

            for (int i = 0; i < particleCount; i++) {
                double lateralOffset = (random.nextDouble() - 0.5D) * 0.82D;
                double x = pos.getX() + 0.5D
                        + back.getStepX() * 0.48D
                        + sideways.getStepX() * lateralOffset;
                double y = pos.getY() + 0.12D + random.nextDouble() * 0.76D;
                double z = pos.getZ() + 0.5D
                        + back.getStepZ() * 0.48D
                        + sideways.getStepZ() * lateralOffset;

                // Vary particle type, size impression, velocity, and rise so the
                // exhaust does not repeat as a uniform puff train.
                int smokeRoll = random.nextInt(8);
                var smokeType = smokeRoll == 0
                        ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE
                        : smokeRoll <= 2
                                ? ParticleTypes.CAMPFIRE_COSY_SMOKE
                                : ParticleTypes.SMOKE;
                double outwardSpeed = 0.008D + random.nextDouble() * 0.020D;
                double riseSpeed = 0.012D + random.nextDouble() * 0.040D;

                level.addParticle(
                        smokeType,
                        x, y, z,
                        back.getStepX() * outwardSpeed,
                        riseSpeed,
                        back.getStepZ() * outwardSpeed);
            }
        }

        if (random.nextInt(4) == 0) {
            double x = pos.getX() + 0.5D + front.getStepX() * 0.52D;
            double y = pos.getY() + 0.35D + random.nextDouble() * 0.24D;
            double z = pos.getZ() + 0.5D + front.getStepZ() * 0.52D;
            level.addParticle(
                    random.nextBoolean() ? ParticleTypes.FLAME : ParticleTypes.LAVA,
                    x, y, z,
                    front.getStepX() * 0.006D,
                    0.012D,
                    front.getStepZ() * 0.006D);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof ThermalGeneratorBlockEntity generator) {
            player.openMenu(generator);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
