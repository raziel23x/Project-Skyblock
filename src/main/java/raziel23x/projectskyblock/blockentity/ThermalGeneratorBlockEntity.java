package raziel23x.projectskyblock.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import raziel23x.projectskyblock.config.MachineConfig;
import raziel23x.projectskyblock.machine.base.BaseGeneratorEnergyStorage;
import raziel23x.projectskyblock.machine.base.BaseMachineInventory;
import raziel23x.projectskyblock.machine.base.MachineSidedItemHandler;
import raziel23x.projectskyblock.machine.fluid.BaseMachineFluidTank;
import raziel23x.projectskyblock.menu.ThermalGeneratorMenu;
import raziel23x.projectskyblock.registry.ModBlockEntities;

public final class ThermalGeneratorBlockEntity extends BlockEntity implements MenuProvider {
    public static final int FUEL_SLOT = 0;
    private final BaseMachineInventory inventory = new BaseMachineInventory(1, this::setChanged,
            (slot, stack) -> slot == FUEL_SLOT && stack.getBurnTime(RecipeType.SMELTING) > 0);
    private final IItemHandler itemHandler = new MachineSidedItemHandler(inventory, new int[]{FUEL_SLOT}, true, false, "thermal generator fuel");
    private final BaseMachineFluidTank lavaTank = new BaseMachineFluidTank(
            MachineConfig.THERMAL_GENERATOR_TANK_CAPACITY_MB.get(),
            stack -> stack.getFluid() == Fluids.LAVA,
            this::setChanged);
    private final BaseGeneratorEnergyStorage energy = new BaseGeneratorEnergyStorage(
            MachineConfig.THERMAL_GENERATOR_ENERGY_CAPACITY.get(),
            MachineConfig.THERMAL_GENERATOR_MAX_OUTPUT_PER_TICK.get(),
            this::setChanged);

    private int lastConvertedFuelMb;
    private int pendingSolidFuelMb;
    private int lavaEnergyRemainder;
    private boolean generating;

    private final ContainerData data = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> lastConvertedFuelMb;
                case 1 -> Math.max(1, MachineConfig.THERMAL_GENERATOR_TANK_CAPACITY_MB.get());
                case 2 -> lavaTank.getFluidAmount();
                case 3 -> lavaTank.getCapacity();
                case 4 -> energy.getEnergyStored();
                case 5 -> energy.getMaxEnergyStored();
                case 6 -> generating ? 1 : 0;
                case 7 -> MachineConfig.THERMAL_GENERATOR_FE_PER_TICK.get();
                default -> 0;
            };
        }
        @Override public void set(int index, int value) {}
        @Override public int getCount() { return ThermalGeneratorMenu.DATA_COUNT; }
    };

    public ThermalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.THERMAL_GENERATOR.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ThermalGeneratorBlockEntity generator) {
        generator.generating = false;

        // Solid furnace fuels are converted into lava-equivalent millibuckets first.
        // Large fuels may exceed the tank's currently available space, so their
        // conversion is queued and transferred into the tank over time without loss.
        generator.transferPendingSolidFuelToTank();
        generator.tryQueueSolidFuelConversion();
        generator.transferPendingSolidFuelToTank();

        // Export stored FE through the standard NeoForge energy capability.
        // The receiver is queried on the face touching this generator.
        generator.pushEnergyToNeighbors(level, pos);

        int target = Math.min(MachineConfig.THERMAL_GENERATOR_FE_PER_TICK.get(), generator.energy.getRemainingCapacity());
        if (target <= 0) return;

        int fePerMb = Math.max(1, MachineConfig.THERMAL_GENERATOR_FE_PER_MB.get());
        int totalNeeded = target + generator.lavaEnergyRemainder;
        int mbNeeded = Math.max(1, (totalNeeded + fePerMb - 1) / fePerMb);
        FluidStack simulated = generator.lavaTank.drain(mbNeeded, IFluidHandler.FluidAction.SIMULATE);
        if (simulated.getAmount() <= 0) return;

        int availableFe = simulated.getAmount() * fePerMb;
        int accepted = generator.energy.generateInternally(Math.min(target, availableFe));
        if (accepted > 0) {
            int mbUsed = Math.max(1, (accepted + fePerMb - 1) / fePerMb);
            generator.lavaTank.drain(mbUsed, IFluidHandler.FluidAction.EXECUTE);
            generator.lavaEnergyRemainder = mbUsed * fePerMb - accepted;
            generator.generating = true;
            generator.setChanged();
        }
    }

    private void pushEnergyToNeighbors(Level level, BlockPos pos) {
        int remaining = Math.min(
                MachineConfig.THERMAL_GENERATOR_MAX_OUTPUT_PER_TICK.get(),
                energy.getEnergyStored());
        if (remaining <= 0) return;

        for (Direction direction : Direction.values()) {
            if (remaining <= 0) break;

            BlockPos targetPos = pos.relative(direction);
            IEnergyStorage receiver = level.getCapability(
                    Capabilities.EnergyStorage.BLOCK,
                    targetPos,
                    direction.getOpposite());
            if (receiver == null || !receiver.canReceive()) continue;

            int accepted = receiver.receiveEnergy(remaining, true);
            if (accepted <= 0) continue;

            int extracted = energy.extractEnergy(accepted, false);
            if (extracted <= 0) break;

            int actuallyReceived = receiver.receiveEnergy(extracted, false);
            remaining -= actuallyReceived;

            // A correctly implemented FE receiver accepts the same amount it
            // reported during simulation. Restore any unexpected remainder.
            if (actuallyReceived < extracted) {
                energy.generateInternally(extracted - actuallyReceived);
            }
        }
    }

    private void tryQueueSolidFuelConversion() {
        if (pendingSolidFuelMb > 0) return;

        ItemStack stack = inventory.getStackInSlot(FUEL_SLOT);
        int burnTicks = stack.getBurnTime(RecipeType.SMELTING);
        if (burnTicks <= 0) {
            lastConvertedFuelMb = 0;
            return;
        }

        long calculated = (long) burnTicks * MachineConfig.THERMAL_FUEL_MB_PER_BURN_TICK.get();
        int lavaMb = (int) Math.min(Integer.MAX_VALUE, calculated);
        if (lavaMb <= 0) return;

        inventory.extractItem(FUEL_SLOT, 1, false);
        pendingSolidFuelMb = lavaMb;
        lastConvertedFuelMb = lavaMb;
        setChanged();
    }

    private void transferPendingSolidFuelToTank() {
        if (pendingSolidFuelMb <= 0) return;

        int accepted = lavaTank.fill(
                new FluidStack(Fluids.LAVA, pendingSolidFuelMb),
                IFluidHandler.FluidAction.EXECUTE);
        if (accepted <= 0) return;

        pendingSolidFuelMb -= accepted;
        setChanged();
    }

    public IItemHandler getItemHandler(@Nullable Direction side) { return itemHandler; }
    public IFluidHandler getFluidHandler(@Nullable Direction side) { return lavaTank; }
    public IEnergyStorage getEnergyStorage(@Nullable Direction side) { return energy; }
    public boolean isGenerating() { return generating; }

    @Override public Component getDisplayName() { return Component.translatable("container.projectskyblock.thermal_generator"); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        if (level == null) return null;
        return new ThermalGeneratorMenu(id, playerInventory, inventory, data, ContainerLevelAccess.create(level, worldPosition));
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.put("LavaTank", lavaTank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("Energy", energy.getEnergyStored());
        tag.putInt("LastConvertedFuelMb", lastConvertedFuelMb);
        tag.putInt("PendingSolidFuelMb", pendingSolidFuelMb);
        tag.putInt("LavaEnergyRemainder", lavaEnergyRemainder);
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        if (tag.contains("LavaTank")) lavaTank.readFromNBT(registries, tag.getCompound("LavaTank"));
        energy.setStoredEnergy(tag.getInt("Energy"));
        lastConvertedFuelMb = tag.getInt("LastConvertedFuelMb");
        pendingSolidFuelMb = tag.getInt("PendingSolidFuelMb");
        lavaEnergyRemainder = tag.getInt("LavaEnergyRemainder");
    }
}
