package raziel23x.projectskyblock.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;
import raziel23x.projectskyblock.blockentity.BasicEnergyCableBlockEntity;
import raziel23x.projectskyblock.registry.ModBlockEntities;

public final class BasicEnergyCableBlock extends BaseEntityBlock {
    public static final MapCodec<BasicEnergyCableBlock> CODEC = simpleCodec(BasicEnergyCableBlock::new);

    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty EAST = BooleanProperty.create("east");

    public static final BooleanProperty MACHINE_DOWN = BooleanProperty.create("machine_down");
    public static final BooleanProperty MACHINE_UP = BooleanProperty.create("machine_up");
    public static final BooleanProperty MACHINE_NORTH = BooleanProperty.create("machine_north");
    public static final BooleanProperty MACHINE_SOUTH = BooleanProperty.create("machine_south");
    public static final BooleanProperty MACHINE_WEST = BooleanProperty.create("machine_west");
    public static final BooleanProperty MACHINE_EAST = BooleanProperty.create("machine_east");

    private static final VoxelShape CORE = Block.box(5, 5, 5, 11, 11, 11);
    private static final VoxelShape DOWN_SHAPE = Block.box(5, 0, 5, 11, 5, 11);
    private static final VoxelShape UP_SHAPE = Block.box(5, 11, 5, 11, 16, 11);
    private static final VoxelShape NORTH_SHAPE = Block.box(5, 5, 0, 11, 11, 5);
    private static final VoxelShape SOUTH_SHAPE = Block.box(5, 5, 11, 11, 11, 16);
    private static final VoxelShape WEST_SHAPE = Block.box(0, 5, 5, 5, 11, 11);
    private static final VoxelShape EAST_SHAPE = Block.box(11, 5, 5, 16, 11, 11);

    public BasicEnergyCableBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(DOWN, false)
                .setValue(UP, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(EAST, false)
                .setValue(MACHINE_DOWN, false)
                .setValue(MACHINE_UP, false)
                .setValue(MACHINE_NORTH, false)
                .setValue(MACHINE_SOUTH, false)
                .setValue(MACHINE_WEST, false)
                .setValue(MACHINE_EAST, false));
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DOWN, UP, NORTH, SOUTH, WEST, EAST,
                MACHINE_DOWN, MACHINE_UP, MACHINE_NORTH, MACHINE_SOUTH, MACHINE_WEST, MACHINE_EAST);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return calculateConnections(context.getLevel(), context.getClickedPos(), defaultBlockState());
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        BlockState updated = calculateConnections(level, pos, state);
        if (!updated.equals(state)) {
            level.setBlock(pos, updated, Block.UPDATE_CLIENTS);
        }
    }

    private BlockState calculateConnections(Level level, BlockPos pos, BlockState state) {
        for (Direction direction : Direction.values()) {
            BlockPos targetPos = pos.relative(direction);
            boolean cable = level.getBlockState(targetPos).getBlock() instanceof BasicEnergyCableBlock;
            boolean machine = !cable && level.getCapability(
                    Capabilities.EnergyStorage.BLOCK,
                    targetPos,
                    direction.getOpposite()) != null;

            state = state
                    .setValue(property(direction), cable || machine)
                    .setValue(machineProperty(direction), machine);
        }
        return state;
    }

    private static BooleanProperty property(Direction direction) {
        return switch (direction) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    private static BooleanProperty machineProperty(Direction direction) {
        return switch (direction) {
            case DOWN -> MACHINE_DOWN;
            case UP -> MACHINE_UP;
            case NORTH -> MACHINE_NORTH;
            case SOUTH -> MACHINE_SOUTH;
            case WEST -> MACHINE_WEST;
            case EAST -> MACHINE_EAST;
        };
    }

    @Override
    protected VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE;
        if (state.getValue(DOWN)) shape = Shapes.or(shape, DOWN_SHAPE);
        if (state.getValue(UP)) shape = Shapes.or(shape, UP_SHAPE);
        if (state.getValue(NORTH)) shape = Shapes.or(shape, NORTH_SHAPE);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SOUTH_SHAPE);
        if (state.getValue(WEST)) shape = Shapes.or(shape, WEST_SHAPE);
        if (state.getValue(EAST)) shape = Shapes.or(shape, EAST_SHAPE);
        return shape;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BasicEnergyCableBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return createTickerHelper(type, ModBlockEntities.BASIC_ENERGY_CABLE.get(), BasicEnergyCableBlockEntity::serverTick);
    }
}
