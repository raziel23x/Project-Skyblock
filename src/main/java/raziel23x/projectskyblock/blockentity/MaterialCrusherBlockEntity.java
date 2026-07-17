package raziel23x.projectskyblock.blockentity;

import java.util.List;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import raziel23x.projectskyblock.block.MaterialCrusherBlock;
import raziel23x.projectskyblock.config.MachineConfig;
import raziel23x.projectskyblock.machine.crusher.CrusherEnergyStorage;
import raziel23x.projectskyblock.machine.crusher.CrusherInventory;
import raziel23x.projectskyblock.machine.crusher.CrusherPowerSource;
import raziel23x.projectskyblock.machine.crusher.CrusherProcessing;
import raziel23x.projectskyblock.machine.crusher.CrusherProcessingResult;
import raziel23x.projectskyblock.machine.crusher.CrusherSidedItemHandler;
import raziel23x.projectskyblock.menu.MaterialCrusherMenu;
import raziel23x.projectskyblock.registry.ModBlockEntities;

public final class MaterialCrusherBlockEntity extends BlockEntity implements MenuProvider {
    public static final int INPUT_SLOT = CrusherInventory.INPUT_SLOT;
    public static final int FUEL_SLOT = CrusherInventory.FUEL_SLOT;
    public static final int OUTPUT_SLOT = CrusherInventory.OUTPUT_SLOT;
    public static final int BYPRODUCT_SLOT = CrusherInventory.BYPRODUCT_SLOT;

    private final CrusherInventory inventory =
            new CrusherInventory(
                    this::setChanged,
                    stack -> level != null
                            && CrusherProcessing.isValidInput(level, stack)
            );
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
    private final IItemHandler unsidedHandler = new CrusherSidedItemHandler(
            inventory,
            new int[]{INPUT_SLOT, FUEL_SLOT, OUTPUT_SLOT, BYPRODUCT_SLOT},
            true,
            true
    );

    private int progress;
    private int burnTimeRemaining;
    private int burnTimeTotal;
    private boolean working;
    private CrusherPowerSource activePowerSource = CrusherPowerSource.NONE;

    private final ContainerData menuData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> Math.max(1, MachineConfig.CRUSHER_PROCESS_TIME.get());
                case 2 -> burnTimeRemaining;
                case 3 -> burnTimeTotal;
                case 4 -> energyStorage.getEnergyStored();
                case 5 -> energyStorage.getMaxEnergyStored();
                case 6 -> activePowerSource.ordinal();
                case 7 -> working ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // Server-owned values. The client receives them through menu synchronization.
        }

        @Override
        public int getCount() {
            return MaterialCrusherMenu.DATA_COUNT;
        }
    };

    public MaterialCrusherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MATERIAL_CRUSHER.get(), pos, state);
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            MaterialCrusherBlockEntity crusher) {
        crusher.energyStorage.setReceivingEnabled(
                MachineConfig.ENABLE_FE_POWER.get()
        );

        ItemStack input = crusher.inventory.getStackInSlot(INPUT_SLOT);
        CrusherProcessingResult maximumResult = CrusherProcessing.maximumResult(level, input);

        if (input.isEmpty()
                || maximumResult.isEmpty()
                || !crusher.canAcceptResult(maximumResult)) {
            crusher.stopAndResetProgress();
            crusher.updateVisualState(level, state);
            return;
        }

        if (!crusher.consumePowerForTick()) {
            crusher.working = false;
            crusher.activePowerSource = CrusherPowerSource.NONE;
            crusher.setChanged();
            crusher.updateVisualState(level, state);
            return;
        }

        crusher.working = true;
        crusher.progress++;

        int processTime = Math.max(1, MachineConfig.CRUSHER_PROCESS_TIME.get());
        if (crusher.progress >= processTime) {
            CrusherProcessingResult result = CrusherProcessing.createResult(
                    level,
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

        crusher.updateVisualState(level, state);
        crusher.setChanged();
    }

    private void updateVisualState(Level level, BlockState state) {
        boolean active = working;
        int frame = active ? (int) ((level.getGameTime() / 3L) & 3L) : 0;
        BlockState current = level.getBlockState(worldPosition);

        if (!current.hasProperty(MaterialCrusherBlock.ACTIVE)
                || !current.hasProperty(MaterialCrusherBlock.GEAR_FRAME)) {
            return;
        }

        if (current.getValue(MaterialCrusherBlock.ACTIVE) != active
                || current.getValue(MaterialCrusherBlock.GEAR_FRAME) != frame) {
            level.setBlock(
                    worldPosition,
                    current.setValue(MaterialCrusherBlock.ACTIVE, active)
                            .setValue(MaterialCrusherBlock.GEAR_FRAME, frame),
                    2
            );
        }
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

    @Override
    public Component getDisplayName() {
        return Component.translatable(
                "container.projectskyblock.material_crusher"
        );
    }

    @Override
    public AbstractContainerMenu createMenu(
            int containerId,
            Inventory playerInventory,
            Player player) {
        if (level == null) {
            return null;
        }
        return new MaterialCrusherMenu(
                containerId,
                playerInventory,
                inventory,
                menuData,
                ContainerLevelAccess.create(level, worldPosition)
        );
    }

    public CrusherInventory getInventory() {
        return inventory;
    }

    public @Nullable IItemHandler getItemHandler(@Nullable Direction side) {
        if (side == null) {
            return unsidedHandler;
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
