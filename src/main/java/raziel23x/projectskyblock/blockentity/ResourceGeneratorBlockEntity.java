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
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.block.ResourceGeneratorBlock;
import raziel23x.projectskyblock.config.GeneratorConfig;
import raziel23x.projectskyblock.registry.ModBlockEntities;

public final class ResourceGeneratorBlockEntity extends BlockEntity {
    private int generationTicks;

    private final ItemStackHandler cobblestoneStorage = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) {
            return GeneratorConfig.COBBLESTONE_CAPACITY.get();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final FluidTank fluidStorage = new FluidTank(getConfiguredFluidCapacity()) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    private final IFluidHandler extractionOnlyFluidHandler = new IFluidHandler() {
        @Override
        public int getTanks() {
            return fluidStorage.getTanks();
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return fluidStorage.getFluidInTank(tank);
        }

        @Override
        public int getTankCapacity(int tank) {
            return fluidStorage.getTankCapacity(tank);
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return false;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return 0;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            return fluidStorage.drain(resource, action);
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            return fluidStorage.drain(maxDrain, action);
        }
    };

    public ResourceGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESOURCE_GENERATOR.get(), pos, state);
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            ResourceGeneratorBlockEntity blockEntity) {
        ResourceGeneratorBlock.Output output = blockEntity.getOutput();
        int interval = blockEntity.getGenerationInterval(output);

        if (++blockEntity.generationTicks < interval) {
            return;
        }

        blockEntity.generationTicks = 0;

        if (output == ResourceGeneratorBlock.Output.COBBLESTONE) {
            blockEntity.generateCobblestone();

            if (GeneratorConfig.COBBLESTONE_AUTO_PUSH_UP.get()) {
                blockEntity.pushCobblestoneUp();
            }
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

    private int getGenerationInterval(ResourceGeneratorBlock.Output output) {
        return switch (output) {
            case COBBLESTONE -> GeneratorConfig.COBBLESTONE_INTERVAL_TICKS.get();
            case WATER -> GeneratorConfig.WATER_INTERVAL_TICKS.get();
            case LAVA -> GeneratorConfig.LAVA_INTERVAL_TICKS.get();
        };
    }

    private static int getConfiguredFluidCapacity() {
        // The block type is not available before the BlockEntity constructor calls this field
        // initializer, so allocate enough room for either configured fluid generator.
        return Math.max(
                GeneratorConfig.WATER_CAPACITY_MB.get(),
                GeneratorConfig.LAVA_CAPACITY_MB.get()
        );
    }

    private int getFluidCapacity(ResourceGeneratorBlock.Output output) {
        return output == ResourceGeneratorBlock.Output.WATER
                ? GeneratorConfig.WATER_CAPACITY_MB.get()
                : GeneratorConfig.LAVA_CAPACITY_MB.get();
    }

    private int getFluidAmountPerCycle(ResourceGeneratorBlock.Output output) {
        return output == ResourceGeneratorBlock.Output.WATER
                ? GeneratorConfig.WATER_PER_CYCLE_MB.get()
                : GeneratorConfig.LAVA_PER_CYCLE_MB.get();
    }

    private void generateCobblestone() {
        int capacity = GeneratorConfig.COBBLESTONE_CAPACITY.get();
        int amount = GeneratorConfig.COBBLESTONE_PER_CYCLE.get();
        ItemStack stored = cobblestoneStorage.getStackInSlot(0);

        if (stored.isEmpty()) {
            cobblestoneStorage.setStackInSlot(
                    0,
                    new ItemStack(Items.COBBLESTONE, Math.min(amount, capacity))
            );
        } else if (stored.getCount() < capacity) {
            ItemStack updated = stored.copy();
            updated.grow(Math.min(amount, capacity - updated.getCount()));
            cobblestoneStorage.setStackInSlot(0, updated);
        }

        debug("Generated cobblestone. Stored: {}/{}", getStoredCobblestone(), capacity);
    }

    private void pushCobblestoneUp() {
        if (level == null || cobblestoneStorage.getStackInSlot(0).isEmpty()) {
            return;
        }

        IItemHandler target = level.getCapability(
                Capabilities.ItemHandler.BLOCK,
                worldPosition.above(),
                Direction.DOWN
        );

        if (target == null) {
            return;
        }

        ItemStack available = cobblestoneStorage.extractItem(
                0,
                cobblestoneStorage.getStackInSlot(0).getCount(),
                true
        );

        if (available.isEmpty()) {
            return;
        }

        ItemStack remainder = ItemHandlerHelper.insertItemStacked(target, available, false);
        int inserted = available.getCount() - remainder.getCount();

        if (inserted > 0) {
            cobblestoneStorage.extractItem(0, inserted, false);
            debug("Pushed {} cobblestone into the inventory above.", inserted);
        }
    }

    private void generateFluid(ResourceGeneratorBlock.Output output) {
        int configuredCapacity = getFluidCapacity(output);
        int stored = fluidStorage.getFluidAmount();

        if (stored >= configuredCapacity) {
            return;
        }

        int amount = Math.min(getFluidAmountPerCycle(output), configuredCapacity - stored);
        FluidStack generated = new FluidStack(
                output == ResourceGeneratorBlock.Output.WATER ? Fluids.WATER : Fluids.LAVA,
                amount
        );

        fluidStorage.fill(generated, IFluidHandler.FluidAction.EXECUTE);
        debug("Generated {} mB of {}. Stored: {}/{} mB",
                amount,
                output.name().toLowerCase(),
                fluidStorage.getFluidAmount(),
                configuredCapacity);
    }

    public IItemHandler getOutputForItemCapability() {
        return getOutput() == ResourceGeneratorBlock.Output.COBBLESTONE
                && GeneratorConfig.COBBLESTONE_PIPE_EXTRACTION.get()
                ? cobblestoneStorage
                : null;
    }

    public IFluidHandler getOutputForFluidCapability() {
        ResourceGeneratorBlock.Output output = getOutput();

        if (output == ResourceGeneratorBlock.Output.COBBLESTONE) {
            return null;
        }

        boolean enabled = output == ResourceGeneratorBlock.Output.WATER
                ? GeneratorConfig.WATER_PIPE_EXTRACTION.get()
                : GeneratorConfig.LAVA_PIPE_EXTRACTION.get();

        return enabled ? extractionOnlyFluidHandler : null;
    }

    public int getStoredCobblestone() {
        return cobblestoneStorage.getStackInSlot(0).getCount();
    }

    public int getStoredFluid() {
        return fluidStorage.getFluidAmount();
    }

    public boolean takeBucket() {
        ResourceGeneratorBlock.Output output = getOutput();
        boolean enabled = output == ResourceGeneratorBlock.Output.WATER
                ? GeneratorConfig.WATER_BUCKET_EXTRACTION.get()
                : GeneratorConfig.LAVA_BUCKET_EXTRACTION.get();

        if (!enabled || fluidStorage.getFluidAmount() < 1_000) {
            return false;
        }

        fluidStorage.drain(1_000, IFluidHandler.FluidAction.EXECUTE);
        return true;
    }

    public ItemStack takeCobblestone() {
        if (!GeneratorConfig.COBBLESTONE_MANUAL_EXTRACTION.get()) {
            return ItemStack.EMPTY;
        }
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
            cobblestoneStorage.deserializeNBT(
                    registries,
                    tag.getCompound("CobblestoneStorage")
            );
        }

        if (tag.contains("FluidStorage")) {
            fluidStorage.readFromNBT(
                    registries,
                    tag.getCompound("FluidStorage")
            );
        }

        generationTicks = tag.getInt("GenerationTicks");
    }

    private static void debug(String message, Object... arguments) {
        if (GeneratorConfig.DEBUG_LOGGING.get()) {
            ProjectSkyblock.LOGGER.info("[Generator Debug] " + message, arguments);
        }
    }
}
