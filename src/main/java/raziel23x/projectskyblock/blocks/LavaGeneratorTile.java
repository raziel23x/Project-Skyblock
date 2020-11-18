package raziel23x.projectskyblock.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static raziel23x.projectskyblock.utils.RegistryHandler.LAVAGENERATOR_TILE;

public class LavaGeneratorTile extends TileEntity implements ITickableTileEntity {
    protected FluidTank tank = new FluidTank(FluidAttributes.BUCKET_VOLUME * 8) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == Fluids.LAVA;
        }
    };

    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);

    private int ticks;

    public LavaGeneratorTile() {
        super(LAVAGENERATOR_TILE.get());
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return holder.cast();
        return super.getCapability(capability, facing);
    }

    public FluidTank getTank() {
        return this.tank;
    }

    @Override
    public void tick() {
        ticks++;
        if (ticks == 20) {
            ticks = 0;

            this.tank.fill(new FluidStack(Fluids.LAVA.getFluid(), 17), IFluidHandler.FluidAction.EXECUTE);
            this.markDirty();
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        getTank().readFromNBT(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag = super.write(tag);
        getTank().writeToNBT(tag);
        return tag;
    }
}
