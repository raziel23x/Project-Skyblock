package raziel23x.projectskyblock.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LavaGeneratorBlock extends Block {
    public LavaGeneratorBlock() {
        super(Properties.create(Material.ROCK)
                .sound(SoundType.STONE)
                .hardnessAndResistance(2.0f)
        );
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new LavaGeneratorTile();
    }


    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        ItemStack heldItem = player.getHeldItem(hand);
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity != null) {
            LazyOptional<IFluidHandler> fluidHandlerCap = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
            if (!fluidHandlerCap.isPresent()) {
                //spawnParticles(world, pos, state);
            }
            else
            {
                IFluidHandler fluidHandler = fluidHandlerCap.orElseThrow(IllegalStateException::new);

                if (!FluidUtil.interactWithFluidHandler(player, hand, fluidHandler)) {
                    // Special case for bottles, they can hold 1/3 of a bucket

                        if (heldItem.getItem() == Items.BUCKET) {
                            FluidStack simulated = fluidHandler.drain(1000, IFluidHandler.FluidAction.SIMULATE);

                            if (simulated.getAmount() == 1000) {
                                fluidHandler.drain(1000, IFluidHandler.FluidAction.EXECUTE);

                                if (player.addItemStackToInventory(new ItemStack(Items.LAVA_BUCKET))) {
                                    heldItem.shrink(1);
                                }
                            }
                        }

                }
            }
        }

        return ActionResultType.SUCCESS;
    }
}