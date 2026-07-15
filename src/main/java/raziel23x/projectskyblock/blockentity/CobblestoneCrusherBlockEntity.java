package raziel23x.projectskyblock.blockentity;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import raziel23x.projectskyblock.config.MachineConfig;
import raziel23x.projectskyblock.machine.crusher.CrusherEnergyStorage;
import raziel23x.projectskyblock.machine.crusher.CrusherInventory;
import raziel23x.projectskyblock.machine.crusher.CrusherPowerSource;
import raziel23x.projectskyblock.machine.crusher.CrusherProcessing;
import raziel23x.projectskyblock.machine.crusher.CrusherProcessingResult;
import raziel23x.projectskyblock.machine.crusher.CrusherSidedItemHandler;
import raziel23x.projectskyblock.registry.ModBlockEntities;

public final class CobblestoneCrusherBlockEntity extends BlockEntity {
    public static final int INPUT_SLOT = CrusherInventory.INPUT_SLOT;
    public static final int FUEL_SLOT = CrusherInventory.FUEL_SLOT;
    public static final int OUTPUT_SLOT = CrusherInventory.OUTPUT_SLOT;
    public static final int BYPRODUCT_SLOT = CrusherInventory.BYPRODUCT_SLOT;

    private final CrusherInventory inventory =
            new CrusherInventory(this::setChanged);
    private final CrusherEnergyStorage energyStorage =
            new CrusherEnergyStorage(
                    MachineConfig.CRUSHER_FE_CAPACITY.get(),
                    this::setChanged
            );

    private final IItemHandler inputHandler = new CrusherSidedItemHandler(
            inventory,
            new int[]{INPUT_SLOT},
            true,
            false
    );
    private final IItemHandler fuelHandler = new CrusherSidedItemHandler(
            inventory,
            new int[]{FUEL_SLOT},
            true,
            false
    );
    private final IItemHandler outputHandler = new CrusherSidedItemHandler(
            inventory,
            new int[]{OUTPUT_SLOT, BYPRODUCT_SLOT},
            false,
            true
    );

    private int progress;
    private int burnTimeRemaining;
    private int burnTimeTotal;
    private boolean working;
    private CrusherPowerSource activePowerSource = CrusherPowerSource.NONE;

    public CobblestoneCrusherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COBBLESTONE_CRUSHER.get(), pos, state);
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            CobblestoneCrusherBlockEntity crusher) {
        crusher.energyStorage.setReceivingEnabled(
                MachineConfig.ENABLE_FE_POWER.get()
        );

        ItemStack input = crusher.inventory.getStackInSlot(INPUT_SLOT);
        CrusherProcessingResult maximumResult = crusher.maximumResult(input);

        if (input.isEmpty()
                || maximumResult.isEmpty()
                || !crusher.canAcceptResult(maximumResult)) {
            crusher.stopAndResetProgress();
            return;
        }

        if (!crusher.consumePowerForTick()) {
            crusher.working = false;
            crusher.activePowerSource = CrusherPowerSource.NONE;
            crusher.setChanged();
            return;
        }

        crusher.working = true;
        crusher.progress++;

        int processTime = Math.max(1, MachineConfig.CRUSHER_PROCESS_TIME.get());
        if (crusher.progress >= processTime) {
            CrusherProcessingResult result = CrusherProcessing.createResult(
                    input,
                    level.random
            );

            if (!result.isEmpty() && crusher.canAcceptResult(result)) {
                crusher.inventory.extractItem(INPUT_SLOT, 1, false);
                crusher.insertResult(result);
                crusher.progress = 0;
            } else {
                crusher.progress = processTime - 1;
            }
        }

        crusher.setChanged();
    }

    private CrusherProcessingResult maximumResult(ItemStack input) {
        if (input.is(Items.COBBLESTONE)) {
            int count = MachineConfig.CRUSHER_EXTRA_GRAVEL_CHANCE.get() > 0.0D
                    ? 2 : 1;
            return new CrusherProcessingResult(
                    new ItemStack(Items.GRAVEL, count),
                    ItemStack.EMPTY
            );
        }

        if (input.is(Items.GRAVEL)) {
            int count = MachineConfig.CRUSHER_EXTRA_SAND_CHANCE.get() > 0.0D
                    ? 2 : 1;
            ItemStack byproduct = MachineConfig.CRUSHER_FLINT_CHANCE.get() > 0.0D
                    ? new ItemStack(Items.FLINT)
                    : ItemStack.EMPTY;
            return new CrusherProcessingResult(
                    new ItemStack(Items.SAND, count),
                    byproduct
            );
        }

        if (CrusherProcessing.isValidInput(input)) {
            return CrusherProcessing.createResult(input, level.random);
        }

        return CrusherProcessingResult.EMPTY;
    }

    private void stopAndResetProgress() {
        boolean changed = progress != 0
                || working
                || activePowerSource != CrusherPowerSource.NONE;
        progress = 0;
        working = false;
        activePowerSource = CrusherPowerSource.NONE;
        if (changed) {
            setChanged();
        }
    }

    private boolean consumePowerForTick() {
        if (MachineConfig.PREFER_FE.get()) {
            if (tryConsumeFE()) {
                activePowerSource = CrusherPowerSource.FE;
                return true;
            }
            if (tryConsumeFuel()) {
                activePowerSource = CrusherPowerSource.FUEL;
                return true;
            }
        } else {
            if (tryConsumeFuel()) {
                activePowerSource = CrusherPowerSource.FUEL;
                return true;
            }
            if (tryConsumeFE()) {
                activePowerSource = CrusherPowerSource.FE;
                return true;
            }
        }
        return false;
    }

    private boolean tryConsumeFE() {
        if (!MachineConfig.ENABLE_FE_POWER.get()) {
            return false;
        }
        return energyStorage.consumeInternally(
                MachineConfig.CRUSHER_FE_PER_TICK.get()
        );
    }

    private boolean tryConsumeFuel() {
        if (!MachineConfig.ENABLE_FUEL_POWER.get()) {
            return false;
        }
        if (burnTimeRemaining <= 0 && !igniteFuel()) {
            return false;
        }
        burnTimeRemaining--;
        return true;
    }

    private boolean igniteFuel() {
        ItemStack fuel = inventory.getStackInSlot(FUEL_SLOT);
        if (fuel.isEmpty()) {
            return false;
        }

        int vanillaBurnTime = fuel.getBurnTime(RecipeType.SMELTING);
        if (vanillaBurnTime <= 0) {
            return false;
        }

        burnTimeTotal = Math.max(
                1,
                (int) Math.round(
                        vanillaBurnTime
                                * MachineConfig.FUEL_BURN_MULTIPLIER.get()
                )
        );
        burnTimeRemaining = burnTimeTotal;

        boolean lavaBucket = fuel.is(Items.LAVA_BUCKET);
        inventory.extractItem(FUEL_SLOT, 1, false);
        if (lavaBucket && inventory.getStackInSlot(FUEL_SLOT).isEmpty()) {
            inventory.setStackInSlot(FUEL_SLOT, new ItemStack(Items.BUCKET));
        }
        return true;
    }

    private boolean canAcceptResult(CrusherProcessingResult result) {
        return canAcceptStack(OUTPUT_SLOT, result.primary())
                && canAcceptStack(BYPRODUCT_SLOT, result.byproduct());
    }

    private boolean canAcceptStack(int slot, ItemStack result) {
        if (result.isEmpty()) {
            return true;
        }

        ItemStack existing = inventory.getStackInSlot(slot);
        return existing.isEmpty()
                || ItemStack.isSameItemSameComponents(existing, result)
                && existing.getCount() + result.getCount()
                <= existing.getMaxStackSize();
    }

    private void insertResult(CrusherProcessingResult result) {
        insertStack(OUTPUT_SLOT, result.primary());
        insertStack(BYPRODUCT_SLOT, result.byproduct());
    }

    private void insertStack(int slot, ItemStack result) {
        if (result.isEmpty()) {
            return;
        }

        ItemStack existing = inventory.getStackInSlot(slot);
        if (existing.isEmpty()) {
            inventory.setStackInSlot(slot, result.copy());
        } else {
            ItemStack updated = existing.copy();
            updated.grow(result.getCount());
            inventory.setStackInSlot(slot, updated);
        }
    }

    public List<ItemStack> takeAllOutputs() {
        return List.of(
                inventory.extractItem(
                        OUTPUT_SLOT,
                        inventory.getStackInSlot(OUTPUT_SLOT).getCount(),
                        false
                ),
                inventory.extractItem(
                        BYPRODUCT_SLOT,
                        inventory.getStackInSlot(BYPRODUCT_SLOT).getCount(),
                        false
                )
        );
    }

    public CrusherInventory getInventory() {
        return inventory;
    }

    public @Nullable IItemHandler getItemHandler(@Nullable Direction side) {
        if (side == null) {
            return null;
        }
        return switch (side) {
            case UP -> inputHandler;
            case DOWN -> outputHandler;
            default -> fuelHandler;
        };
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public boolean isWorking() {
        return working;
    }

    public int getProgress() {
        return progress;
    }

    public int getBurnTimeRemaining() {
        return burnTimeRemaining;
    }

    public int getBurnTimeTotal() {
        return burnTimeTotal;
    }

    public CrusherPowerSource getActivePowerSource() {
        return activePowerSource;
    }

    @Override
    protected void saveAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Progress", progress);
        tag.putInt("BurnTimeRemaining", burnTimeRemaining);
        tag.putInt("BurnTimeTotal", burnTimeTotal);
        tag.putInt("Energy", energyStorage.getEnergyStored());
        tag.putBoolean("Working", working);
        tag.putString("PowerSource", activePowerSource.name());
    }

    @Override
    protected void loadAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
        progress = Math.max(0, tag.getInt("Progress"));
        burnTimeRemaining = Math.max(0, tag.getInt("BurnTimeRemaining"));
        burnTimeTotal = Math.max(0, tag.getInt("BurnTimeTotal"));
        energyStorage.setStoredEnergy(tag.getInt("Energy"));
        working = tag.getBoolean("Working");

        try {
            activePowerSource = CrusherPowerSource.valueOf(
                    tag.getString("PowerSource")
            );
        } catch (IllegalArgumentException ignored) {
            activePowerSource = CrusherPowerSource.NONE;
        }
    }
}
