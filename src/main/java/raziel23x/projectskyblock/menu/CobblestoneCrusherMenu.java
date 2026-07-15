package raziel23x.projectskyblock.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import raziel23x.projectskyblock.blockentity.CobblestoneCrusherBlockEntity;
import raziel23x.projectskyblock.machine.crusher.CrusherProcessing;
import raziel23x.projectskyblock.registry.ModBlocks;
import raziel23x.projectskyblock.registry.ModMenus;

public final class CobblestoneCrusherMenu extends AbstractContainerMenu {
    public static final int MACHINE_SLOT_COUNT = 4;
    public static final int DATA_COUNT = 8;

    private static final int PLAYER_INVENTORY_START = MACHINE_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final ContainerLevelAccess access;
    private final ContainerData data;

    public CobblestoneCrusherMenu(int containerId, Inventory playerInventory) {
        this(
                containerId,
                playerInventory,
                new ItemStackHandler(MACHINE_SLOT_COUNT),
                new SimpleContainerData(DATA_COUNT),
                ContainerLevelAccess.NULL
        );
    }

    public CobblestoneCrusherMenu(
            int containerId,
            Inventory playerInventory,
            IItemHandler machineInventory,
            ContainerData data,
            ContainerLevelAccess access) {
        super(ModMenus.COBBLESTONE_CRUSHER.get(), containerId);
        checkContainerDataCount(data, DATA_COUNT);
        this.access = access;
        this.data = data;

        addSlot(new SlotItemHandler(
                machineInventory,
                CobblestoneCrusherBlockEntity.INPUT_SLOT,
                28,
                32
        ));
        addSlot(new SlotItemHandler(
                machineInventory,
                CobblestoneCrusherBlockEntity.FUEL_SLOT,
                28,
                68
        ));
        addSlot(new OutputSlot(
                machineInventory,
                CobblestoneCrusherBlockEntity.OUTPUT_SLOT,
                140,
                32
        ));
        addSlot(new OutputSlot(
                machineInventory,
                CobblestoneCrusherBlockEntity.BYPRODUCT_SLOT,
                140,
                68
        ));

        addPlayerInventory(playerInventory);
        addDataSlots(data);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(
                        playerInventory,
                        column + row * 9 + 9,
                        9 + column * 18,
                        121 + row * 18
                ));
            }
        }

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(
                    playerInventory,
                    column,
                    9 + column * 18,
                    179
            ));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.COBBLESTONE_CRUSHER.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (!sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack originalStack = sourceStack.copy();

        if (index < MACHINE_SLOT_COUNT) {
            if (!moveItemStackTo(
                    sourceStack,
                    PLAYER_INVENTORY_START,
                    HOTBAR_END,
                    true
            )) {
                return ItemStack.EMPTY;
            }
        } else if (CrusherProcessing.isValidInput(player.level(), sourceStack)) {
            if (!moveItemStackTo(
                    sourceStack,
                    CobblestoneCrusherBlockEntity.INPUT_SLOT,
                    CobblestoneCrusherBlockEntity.INPUT_SLOT + 1,
                    false
            )) {
                return ItemStack.EMPTY;
            }
        } else if (sourceStack.getBurnTime(RecipeType.SMELTING) > 0) {
            if (!moveItemStackTo(
                    sourceStack,
                    CobblestoneCrusherBlockEntity.FUEL_SLOT,
                    CobblestoneCrusherBlockEntity.FUEL_SLOT + 1,
                    false
            )) {
                return ItemStack.EMPTY;
            }
        } else if (index < PLAYER_INVENTORY_END) {
            if (!moveItemStackTo(sourceStack, HOTBAR_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(
                sourceStack,
                PLAYER_INVENTORY_START,
                PLAYER_INVENTORY_END,
                false
        )) {
            return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.setByPlayer(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        if (sourceStack.getCount() == originalStack.getCount()) {
            return ItemStack.EMPTY;
        }

        sourceSlot.onTake(player, sourceStack);
        return originalStack;
    }

    public int getProgress() {
        return data.get(0);
    }

    public int getProcessTime() {
        return Math.max(1, data.get(1));
    }

    public int getBurnTimeRemaining() {
        return data.get(2);
    }

    public int getBurnTimeTotal() {
        return Math.max(1, data.get(3));
    }

    public int getEnergyStored() {
        return data.get(4);
    }

    public int getEnergyCapacity() {
        return Math.max(1, data.get(5));
    }

    public int getPowerSourceId() {
        return data.get(6);
    }

    public boolean isWorking() {
        return data.get(7) != 0;
    }

    public int getScaledProgress(int width) {
        return Math.min(width, getProgress() * width / getProcessTime());
    }

    public int getScaledBurnTime(int height) {
        return Math.min(height, getBurnTimeRemaining() * height / getBurnTimeTotal());
    }

    public int getScaledEnergy(int height) {
        return Math.min(height, getEnergyStored() * height / getEnergyCapacity());
    }

    private static final class OutputSlot extends SlotItemHandler {
        private OutputSlot(IItemHandler handler, int slot, int x, int y) {
            super(handler, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}
