package raziel23x.projectskyblock.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

public class WaterGeneratorBlock extends Block {
    private  static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public WaterGeneratorBlock() {
        super(AbstractBlock.Properties.create(Material.ROCK)
                .sound(SoundType.STONE)
                .hardnessAndResistance(2.0f, 3.0f)
                .harvestLevel(0)
                .setRequiresTool()
                .harvestTool(ToolType.PICKAXE)
                .notSolid()
        );
    }
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new WaterGeneratorTile();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        ItemStack heldItem = player.getHeldItem(hand);
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity != null) {
            LazyOptional<IFluidHandler> fluidHandlerCap = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);

            if (fluidHandlerCap.isPresent()) {
                IFluidHandler fluidHandler = fluidHandlerCap.orElseThrow(IllegalStateException::new);

                if (!FluidUtil.interactWithFluidHandler(player, hand, fluidHandler)) {
                    if (heldItem.getItem() == Items.GLASS_BOTTLE) {
                        if (fluidHandler.drain(333, IFluidHandler.FluidAction.SIMULATE).getAmount() == 333) {
                            fluidHandler.drain(333, IFluidHandler.FluidAction.EXECUTE);

                            heldItem.shrink(1);
                            ItemStack itemPotion = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER);

                            if (!player.addItemStackToInventory(itemPotion))
                                spawnAsEntity(world, player.getPosition(), itemPotion);

                        }
                    }

                    else if (heldItem.getItem() == Items.POTION && heldItem.getTag() != null) {
                        if (heldItem.getTag().getString("Potion").equals("minecraft:water")) {
                            if (fluidHandler.fill(new FluidStack(Fluids.WATER, 333), IFluidHandler.FluidAction.SIMULATE) == 333) {
                                fluidHandler.fill(new FluidStack(Fluids.WATER, 333), IFluidHandler.FluidAction.EXECUTE);

                                heldItem.shrink(1);
                                ItemStack itemBottle = new ItemStack(Items.GLASS_BOTTLE);

                                if (!player.addItemStackToInventory(itemBottle))
                                    spawnAsEntity(world, player.getPosition(), itemBottle);

                            }
                        }
                    }

                    else {
                        if (fluidHandler.drain(1000, IFluidHandler.FluidAction.SIMULATE).getAmount() == 1000)
                            fluidHandler.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    }
                }
            }
        }

        return ActionResultType.SUCCESS;
    }
}