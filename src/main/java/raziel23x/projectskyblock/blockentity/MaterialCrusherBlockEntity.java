package raziel23x.projectskyblock.blockentity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.block.MaterialCrusherBlock;
import raziel23x.projectskyblock.config.MachineConfig;
import raziel23x.projectskyblock.menu.MaterialCrusherMenu;
import raziel23x.projectskyblock.platform.neoforge.inventory.ItemStackBoundaryException;
import raziel23x.projectskyblock.platform.neoforge.inventory.MinecraftItemStackCodec;
import raziel23x.projectskyblock.platform.neoforge.machine.EngineMachineBlockEntity;
import raziel23x.projectskyblock.platform.neoforge.machine.MachineRuntimeNbtCodec;
import raziel23x.projectskyblock.platform.neoforge.machine.capability.EngineEnergyStorageAdapter;
import raziel23x.projectskyblock.platform.neoforge.machine.capability.EngineItemHandlerAdapter;
import raziel23x.projectskyblock.platform.neoforge.machine.crusher.MaterialCrusherEnergyStorage;
import raziel23x.projectskyblock.platform.neoforge.machine.crusher.MaterialCrusherItemHandler;
import raziel23x.projectskyblock.platform.neoforge.machine.crusher.MinecraftMaterialCrusherFuelPort;
import raziel23x.projectskyblock.platform.neoforge.machine.crusher.MinecraftMaterialCrusherRecipePort;
import raziel23x.projectskyblock.registry.ModBlockEntities;
import raziel23x.projectskyblock.simulation.core.SimulationScheduler;
import raziel23x.projectskyblock.simulation.energy.EnergyLimits;
import raziel23x.projectskyblock.simulation.energy.SimulationEnergyState;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;
import raziel23x.projectskyblock.simulation.machine.MachineId;
import raziel23x.projectskyblock.simulation.machine.component.MachineEnergyAccess;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventoryAccess;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventorySlotDefinition;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventorySlotRule;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventoryView;
import raziel23x.projectskyblock.simulation.machine.component.MachineThermalAccess;
import raziel23x.projectskyblock.simulation.machine.logic.crusher.MaterialCrusherLegacySnapshotMigration;
import raziel23x.projectskyblock.simulation.machine.logic.crusher.MaterialCrusherLogic;
import raziel23x.projectskyblock.simulation.machine.logic.crusher.MaterialCrusherPowerSource;
import raziel23x.projectskyblock.simulation.machine.logic.crusher.MaterialCrusherSettings;
import raziel23x.projectskyblock.simulation.machine.persistence.MachineRuntimeSnapshot;
import raziel23x.projectskyblock.simulation.machine.runtime.MachineRuntime;
import raziel23x.projectskyblock.simulation.thermal.ThermalProperties;
import raziel23x.projectskyblock.simulation.thermal.ThermalState;

/** Material Crusher platform shell over one engine-owned machine runtime. */
public final class MaterialCrusherBlockEntity extends EngineMachineBlockEntity implements MenuProvider {
    public static final int INPUT_SLOT = MaterialCrusherLogic.INPUT_SLOT;
    public static final int FUEL_SLOT = MaterialCrusherLogic.FUEL_SLOT;
    public static final int OUTPUT_SLOT = MaterialCrusherLogic.OUTPUT_SLOT;
    public static final int BYPRODUCT_SLOT = MaterialCrusherLogic.BYPRODUCT_SLOT;

    private static final long AMBIENT_TEMPERATURE_MK = 293_150L;
    private static final String LEGACY_INVENTORY_KEY = "Inventory";
    private static final String LEGACY_PROGRESS_KEY = "Progress";
    private static final String LEGACY_BURN_REMAINING_KEY = "BurnTimeRemaining";
    private static final String LEGACY_BURN_TOTAL_KEY = "BurnTimeTotal";
    private static final String LEGACY_ENERGY_KEY = "Energy";
    private static final String LEGACY_WORKING_KEY = "Working";
    private static final String LEGACY_POWER_SOURCE_KEY = "PowerSource";

    private final MinecraftItemStackCodec itemCodec = new MinecraftItemStackCodec(
            () -> level == null ? null : level.registryAccess());
    private final IEnergyStorage engineEnergyCapability =
            new EngineEnergyStorageAdapter(() -> machineRuntime().components().energy());
    private final IEnergyStorage energyCapability = new MaterialCrusherEnergyStorage(
            engineEnergyCapability,
            () -> MachineConfig.ENABLE_FE_POWER.get());

    private MinecraftMaterialCrusherRecipePort recipePort;
    private MinecraftMaterialCrusherFuelPort fuelPort;
    private MaterialCrusherLogic crusherLogic;
    private IItemHandler inputHandler;
    private IItemHandler fuelHandler;
    private IItemHandler outputHandler;
    private IItemHandler unsidedHandler;
    private IItemHandler menuHandler;
    private ItemStack clientInputStack = ItemStack.EMPTY;
    private boolean legacyPresentationPending;
    private boolean legacyWorking;
    private MaterialCrusherPowerSource legacyPowerSource = MaterialCrusherPowerSource.NONE;

    private final ContainerData menuData = new ContainerData() {
        @Override
        public int get(int index) {
            if (!hasMachineRuntime()) {
                return 0;
            }
            var components = machineRuntime().components();
            return switch (index) {
                case 0 -> toMenuInt(components.processing().completedUnits());
                case 1 -> toMenuInt(components.processing().requiredUnits() > 0L
                        ? components.processing().requiredUnits()
                        : Math.max(1, MachineConfig.CRUSHER_PROCESS_TIME.get()));
                case 2 -> toMenuInt(components.combustion().remainingBurnUnits());
                case 3 -> toMenuInt(components.combustion().totalBurnUnits());
                case 4 -> toMenuInt(components.energy().storedEnergy());
                case 5 -> toMenuInt(components.energy().capacity());
                case 6 -> crusherLogic == null
                        ? MaterialCrusherPowerSource.NONE.ordinal()
                        : crusherLogic.activePowerSource().ordinal();
                case 7 -> crusherLogic != null && crusherLogic.working() ? 1 : 0;
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

    @Override
    protected MachineRuntime createMachineRuntime(SimulationScheduler scheduler) {
        recipePort = new MinecraftMaterialCrusherRecipePort(() -> level, itemCodec);
        fuelPort = new MinecraftMaterialCrusherFuelPort(itemCodec);
        crusherLogic = new MaterialCrusherLogic(
                recipePort,
                fuelPort,
                MaterialCrusherBlockEntity::currentSettings);
        if (legacyPresentationPending) {
            crusherLogic.restorePresentation(legacyWorking, legacyPowerSource);
        }

        long capacity = Math.max(0, MachineConfig.CRUSHER_FE_CAPACITY.get());
        String dimension = level instanceof ServerLevel serverLevel
                ? serverLevel.dimension().location().toString()
                : "unbound";
        MachineId id = new MachineId("material_crusher@" + dimension + ":" + worldPosition.asLong());
        MachineRuntime runtime = new MachineRuntime(
                scheduler,
                id,
                new SimulationEnergyState(EnergyLimits.unlimitedThroughput(capacity)),
                MachineEnergyAccess.INPUT,
                createInventorySlots(),
                new ThermalState(AMBIENT_TEMPERATURE_MK, 1L),
                new ThermalProperties(0L, 0L, Long.MAX_VALUE, Long.MAX_VALUE),
                MachineThermalAccess.NONE,
                crusherLogic);
        createInventoryAdapters(runtime);
        return runtime;
    }

    @Override
    protected MachineRuntimeSnapshot readLegacyMachineSnapshot(
            CompoundTag tag,
            HolderLookup.Provider registries) {
        if (!containsLegacyState(tag)) {
            return null;
        }

        MinecraftItemStackCodec legacyCodec = new MinecraftItemStackCodec(() -> registries);
        ItemStackHandler legacyInventory = new ItemStackHandler(MaterialCrusherLogic.SLOT_COUNT);
        if (tag.contains(LEGACY_INVENTORY_KEY, Tag.TAG_COMPOUND)) {
            legacyInventory.deserializeNBT(registries, tag.getCompound(LEGACY_INVENTORY_KEY));
        }
        List<SimulationItemStack> inventory = new ArrayList<>(MaterialCrusherLogic.SLOT_COUNT);
        for (int slot = 0; slot < MaterialCrusherLogic.SLOT_COUNT; slot++) {
            inventory.add(legacyCodec.encode(legacyInventory.getStackInSlot(slot)));
        }

        long remainingBurn = tag.getInt(LEGACY_BURN_REMAINING_KEY);
        long totalBurn = tag.getInt(LEGACY_BURN_TOTAL_KEY);
        long capacity = MachineConfig.CRUSHER_FE_CAPACITY.get();
        long storedEnergy = tag.getInt(LEGACY_ENERGY_KEY);
        long processTime = MachineConfig.CRUSHER_PROCESS_TIME.get();
        long progress = tag.getInt(LEGACY_PROGRESS_KEY);

        legacyWorking = tag.getBoolean(LEGACY_WORKING_KEY);
        legacyPowerSource = parseLegacyPowerSource(tag.getString(LEGACY_POWER_SOURCE_KEY));
        legacyPresentationPending = true;
        ProjectSkyblock.LOGGER.info(
                "Migrating legacy Material Crusher state at {} into engine snapshot schema {}",
                worldPosition,
                MachineRuntimeSnapshot.CURRENT_SCHEMA_VERSION);

        return MaterialCrusherLegacySnapshotMigration.migrate(
                inventory,
                progress,
                processTime,
                remainingBurn,
                totalBurn,
                storedEnergy,
                capacity,
                AMBIENT_TEMPERATURE_MK);
    }

    @Override
    protected void afterMachineSnapshotRestored(MachineRuntimeSnapshot snapshot) {
        if (legacyPresentationPending && crusherLogic != null) {
            crusherLogic.restorePresentation(legacyWorking, legacyPowerSource);
            legacyPresentationPending = false;
        }
    }

    @Override
    protected void afterMachineExecution() {
        updateVisualState();
    }

    private void updateVisualState() {
        if (level == null || crusherLogic == null) {
            return;
        }
        boolean active = crusherLogic.working();
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
                    2);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        clientInputStack = readVisualInput(tag, registries);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.projectskyblock.material_crusher");
    }

    @Override
    public AbstractContainerMenu createMenu(
            int containerId,
            Inventory playerInventory,
            Player player) {
        if (level == null || !hasMachineRuntime() || menuHandler == null) {
            return null;
        }
        return new MaterialCrusherMenu(
                containerId,
                playerInventory,
                menuHandler,
                menuData,
                ContainerLevelAccess.create(level, worldPosition));
    }

    public @Nullable IItemHandler getItemHandler(@Nullable Direction side) {
        if (!hasMachineRuntime()) {
            return null;
        }
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
        return hasMachineRuntime() ? energyCapability : null;
    }

    public ItemStack getInputStackForVisuals() {
        if (hasMachineRuntime()) {
            try {
                return itemCodec.decode(machineRuntime().components().inventory().stack(INPUT_SLOT));
            } catch (ItemStackBoundaryException exception) {
                return ItemStack.EMPTY;
            }
        }
        return clientInputStack.copy();
    }

    public List<ItemStack> takeAllOutputs() {
        if (!hasMachineRuntime() || outputHandler == null) {
            return List.of(ItemStack.EMPTY, ItemStack.EMPTY);
        }
        return List.of(
                outputHandler.extractItem(0, outputHandler.getStackInSlot(0).getCount(), false),
                outputHandler.extractItem(1, outputHandler.getStackInSlot(1).getCount(), false));
    }

    public boolean isWorking() {
        return crusherLogic != null && crusherLogic.working();
    }

    public int getProgress() {
        return hasMachineRuntime()
                ? toMenuInt(machineRuntime().components().processing().completedUnits())
                : 0;
    }

    public int getBurnTimeRemaining() {
        return hasMachineRuntime()
                ? toMenuInt(machineRuntime().components().combustion().remainingBurnUnits())
                : 0;
    }

    public int getBurnTimeTotal() {
        return hasMachineRuntime()
                ? toMenuInt(machineRuntime().components().combustion().totalBurnUnits())
                : 0;
    }

    public MaterialCrusherPowerSource getActivePowerSource() {
        return crusherLogic == null
                ? MaterialCrusherPowerSource.NONE
                : crusherLogic.activePowerSource();
    }

    public void requestRecipeRecheck() {
        if (hasMachineRuntime()) {
            machineRuntime().requestWork();
        }
    }

    private void createInventoryAdapters(MachineRuntime runtime) {
        var inventory = runtime.components().inventory();
        EngineItemHandlerAdapter rawInput = new EngineItemHandlerAdapter(
                MachineInventoryView.automation(
                        inventory,
                        new int[]{INPUT_SLOT},
                        true,
                        false,
                        "Material Crusher top input"),
                itemCodec);
        EngineItemHandlerAdapter rawFuel = new EngineItemHandlerAdapter(
                MachineInventoryView.automation(
                        inventory,
                        new int[]{FUEL_SLOT},
                        true,
                        false,
                        "Material Crusher side fuel"),
                itemCodec);
        EngineItemHandlerAdapter rawOutput = new EngineItemHandlerAdapter(
                MachineInventoryView.automation(
                        inventory,
                        new int[]{OUTPUT_SLOT, BYPRODUCT_SLOT},
                        false,
                        true,
                        "Material Crusher bottom output"),
                itemCodec);
        EngineItemHandlerAdapter rawUnsided = new EngineItemHandlerAdapter(
                MachineInventoryView.menu(
                        inventory,
                        new int[]{INPUT_SLOT, FUEL_SLOT, OUTPUT_SLOT, BYPRODUCT_SLOT},
                        true,
                        true,
                        "Material Crusher unsided"),
                itemCodec);
        EngineItemHandlerAdapter rawMenu = new EngineItemHandlerAdapter(
                MachineInventoryView.menu(
                        inventory,
                        new int[]{INPUT_SLOT, FUEL_SLOT, OUTPUT_SLOT, BYPRODUCT_SLOT},
                        true,
                        true,
                        "Material Crusher menu"),
                itemCodec);

        inputHandler = new MaterialCrusherItemHandler(
                rawInput,
                (slot, stack) -> recipePort.accepts(stack));
        fuelHandler = new MaterialCrusherItemHandler(
                rawFuel,
                (slot, stack) -> fuelPort.accepts(stack));
        outputHandler = rawOutput;
        unsidedHandler = new MaterialCrusherItemHandler(
                rawUnsided,
                this::isValidMenuInsertion);
        menuHandler = new MaterialCrusherItemHandler(
                rawMenu,
                this::isValidMenuInsertion);
    }

    private boolean isValidMenuInsertion(int slot, ItemStack stack) {
        return switch (slot) {
            case INPUT_SLOT -> recipePort.accepts(stack);
            case FUEL_SLOT -> fuelPort.accepts(stack);
            case OUTPUT_SLOT, BYPRODUCT_SLOT -> false;
            default -> false;
        };
    }

    private static List<MachineInventorySlotDefinition> createInventorySlots() {
        MachineInventorySlotDefinition slot = new MachineInventorySlotDefinition(
                64L,
                MachineInventoryAccess.BIDIRECTIONAL,
                MachineInventorySlotRule.ACCEPT_ALL);
        return List.of(slot, slot, slot, slot);
    }

    private static MaterialCrusherSettings currentSettings() {
        return new MaterialCrusherSettings(
                MachineConfig.ENABLE_FUEL_POWER.get(),
                MachineConfig.ENABLE_FE_POWER.get(),
                MachineConfig.PREFER_FE.get(),
                Math.max(1, MachineConfig.CRUSHER_PROCESS_TIME.get()),
                Math.max(0, MachineConfig.CRUSHER_FE_PER_TICK.get()),
                MachineConfig.FUEL_BURN_MULTIPLIER.get());
    }

    private static boolean containsLegacyState(CompoundTag tag) {
        return tag.contains(LEGACY_INVENTORY_KEY, Tag.TAG_COMPOUND)
                || tag.contains(LEGACY_PROGRESS_KEY, Tag.TAG_INT)
                || tag.contains(LEGACY_BURN_REMAINING_KEY, Tag.TAG_INT)
                || tag.contains(LEGACY_BURN_TOTAL_KEY, Tag.TAG_INT)
                || tag.contains(LEGACY_ENERGY_KEY, Tag.TAG_INT)
                || tag.contains(LEGACY_WORKING_KEY, Tag.TAG_BYTE)
                || tag.contains(LEGACY_POWER_SOURCE_KEY, Tag.TAG_STRING);
    }

    private static MaterialCrusherPowerSource parseLegacyPowerSource(String name) {
        try {
            return MaterialCrusherPowerSource.valueOf(name);
        } catch (IllegalArgumentException exception) {
            return MaterialCrusherPowerSource.NONE;
        }
    }

    private static int toMenuInt(long value) {
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, value));
    }

    private static ItemStack readVisualInput(
            CompoundTag tag,
            HolderLookup.Provider registries) {
        try {
            if (tag.contains(MachineRuntimeNbtCodec.ROOT_KEY, Tag.TAG_COMPOUND)) {
                MachineRuntimeSnapshot snapshot = MachineRuntimeNbtCodec.read(tag);
                if (snapshot.inventory().size() > INPUT_SLOT) {
                    return new MinecraftItemStackCodec(() -> registries)
                            .decode(snapshot.inventory().get(INPUT_SLOT));
                }
            } else if (tag.contains(LEGACY_INVENTORY_KEY, Tag.TAG_COMPOUND)) {
                ItemStackHandler legacyInventory = new ItemStackHandler(MaterialCrusherLogic.SLOT_COUNT);
                legacyInventory.deserializeNBT(registries, tag.getCompound(LEGACY_INVENTORY_KEY));
                return legacyInventory.getStackInSlot(INPUT_SLOT).copy();
            }
        } catch (RuntimeException ignored) {
            // Client-only particle presentation fails closed without affecting authoritative state.
        }
        return ItemStack.EMPTY;
    }
}
