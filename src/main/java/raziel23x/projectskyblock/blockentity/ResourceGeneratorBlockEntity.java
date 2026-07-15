package raziel23x.projectskyblock.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import raziel23x.projectskyblock.block.ResourceGeneratorBlock;
import raziel23x.projectskyblock.registry.ModBlockEntities;

public final class ResourceGeneratorBlockEntity extends BlockEntity {
    public static final int COBBLESTONE_CAPACITY = 64;
    public static final int FLUID_CAPACITY = 8_000;
    private static final int GENERATION_INTERVAL = 20;

    private int generationTicks;

    private final ItemStackHandler cobblestoneStorage = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) {
            return COBBLESTONE_CAPACITY;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false; // Pipes may extract, but cannot insert.
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final FluidTank fluidStorage = new FluidTank(FLUID_CAPACITY) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    private final IFluidHandler extractionOnlyFluidHandler = new IFluidHandler() {
        @Override public int getTanks() { return fluidStorage.getTanks(); }
        @Override public FluidStack getFluidInTank(int tank) { return fluidStorage.getFluidInTank(tank); }
        @Override public int getTankCapacity(int tank) { return fluidStorage.getTankCapacity(tank); }
        @Override public boolean isFluidValid(int tank, FluidStack stack) { return false; }
        @Override public int fill(FluidStack resource, FluidAction action) { return 0; }
        @Override public FluidStack drain(FluidStack resource, FluidAction action) { return fluidStorage.drain(resource, action); }
        @Override public FluidStack drain(int maxDrain, FluidAction action) { return fluidStorage.drain(maxDrain, action); }
    };

    public ResourceGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESOURCE_GENERATOR.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ResourceGeneratorBlockEntity blockEntity) {
        if (++blockEntity.generationTicks < GENERATION_INTERVAL) return;
        blockEntity.generationTicks = 0;

        ResourceGeneratorBlock.Output output = blockEntity.getOutput();
        if (output == ResourceGeneratorBlock.Output.COBBLESTONE) {
            blockEntity.generateCobblestone();
            blockEntity.pushCobblestoneUp();
        } else {
            blockEntity.generateFluid(output);
        }
    }

    private ResourceGeneratorBlock.Output getOutput() {
        if (getBlockState().getBlock() instanceof ResourceGeneratorBlock generatorBlock) {
            return generatorBlock.getOutput();
        }
        return ResourceGeneratorBlock.Output.COBBLESTONE;
    }

    private void generateCobblestone() {
        ItemStack stored = cobblestoneStorage.getStackInSlot(0);
        if (stored.isEmpty()) {
            cobblestoneStorage.setStackInSlot(0, new ItemStack(Items.COBBLESTONE));
        } else if (stored.getCount() < COBBLESTONE_CAPACITY) {
            stored.grow(1);
            cobblestoneStorage.setStackInSlot(0, stored);
        }
    }

    private void pushCobblestoneUp() {
        if (level == null || cobblestoneStorage.getStackInSlot(0).isEmpty()) return;

        IItemHandler target = level.getCapability(
                Capabilities.ItemHandler.BLOCK,
                worldPosition.above(),
                Direction.DOWN
        );
        if (target == null) return;

        ItemStack one = cobblestoneStorage.extractItem(0, 1, true);
        if (one.isEmpty()) return;

        ItemStack remainder = ItemHandlerHelper.insertItemStacked(target, one, false);
        if (remainder.isEmpty()) {
            cobblestoneStorage.extractItem(0, 1, false);
        }
    }

    private void generateFluid(ResourceGeneratorBlock.Output output) {
        if (fluidStorage.getFluidAmount() >= FLUID_CAPACITY) return;

        FluidStack generated = new FluidStack(
                output == ResourceGeneratorBlock.Output.WATER ? Fluids.WATER : Fluids.LAVA,
                1_000
        );
        fluidStorage.fill(generated, IFluidHandler.FluidAction.EXECUTE);
    }

    public IItemHandler getOutputForItemCapability() {
        return getOutput() == ResourceGeneratorBlock.Output.COBBLESTONE ? cobblestoneStorage : null;
    }

    public IFluidHandler getOutputForFluidCapability() {
        return getOutput() == ResourceGeneratorBlock.Output.COBBLESTONE ? null : extractionOnlyFluidHandler;
    }

    public int getStoredCobblestone() {
        return cobblestoneStorage.getStackInSlot(0).getCount();
    }

    public int getStoredFluid() {
        return fluidStorage.getFluidAmount();
    }

    public boolean takeBucket() {
        if (fluidStorage.getFluidAmount() < 1_000) return false;
        fluidStorage.drain(1_000, IFluidHandler.FluidAction.EXECUTE);
        return true;
    }

    public ItemStack takeCobblestone() {
        return cobblestoneStorage.extractItem(0, 1, false);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("CobblestoneStorage", cobblestoneStorage.serializeNBT(registries));
        tag.put("FluidStorage", fluidStorage.writeToNBT(registries, new CompoundTag()));
        tag.putInt("GenerationTicks", generationTicks);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("CobblestoneStorage")) {
            cobblestoneStorage.deserializeNBT(registries, tag.getCompound("CobblestoneStorage"));
        }
        if (tag.contains("FluidStorage")) {
            fluidStorage.readFromNBT(registries, tag.getCompound("FluidStorage"));
        }
        generationTicks = tag.getInt("GenerationTicks");
    }
}
