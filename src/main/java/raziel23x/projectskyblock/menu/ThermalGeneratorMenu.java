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
import raziel23x.projectskyblock.registry.ModBlocks;
import raziel23x.projectskyblock.registry.ModMenus;

public final class ThermalGeneratorMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 8;
    private final ContainerLevelAccess access;
    private final ContainerData data;

    public ThermalGeneratorMenu(int id, Inventory inventory) {
        this(id, inventory, new ItemStackHandler(1), new SimpleContainerData(DATA_COUNT), ContainerLevelAccess.NULL);
    }

    public ThermalGeneratorMenu(int id, Inventory playerInventory, IItemHandler machineInventory, ContainerData data, ContainerLevelAccess access) {
        super(ModMenus.THERMAL_GENERATOR.get(), id);
        this.access = access;
        this.data = data;
        checkContainerDataCount(data, DATA_COUNT);
        addSlot(new SlotItemHandler(machineInventory, 0, 35, 49));
        addPlayerInventory(playerInventory);
        addDataSlots(data);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        // Match the Cobblestone Crusher player inventory layout exactly.
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

    @Override public boolean stillValid(Player player) { return stillValid(access, player, ModBlocks.THERMAL_GENERATOR.get()); }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (index == 0) {
            if (!moveItemStackTo(stack, 1, 37, true)) return ItemStack.EMPTY;
        } else if (stack.getBurnTime(RecipeType.SMELTING) > 0) {
            if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
        } else if (index < 28) {
            if (!moveItemStackTo(stack, 28, 37, false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 1, 28, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        return copy;
    }

    public int burn() { return data.get(0); }
    public int burnTotal() { return Math.max(1, data.get(1)); }
    public int fluid() { return data.get(2); }
    public int fluidCapacity() { return Math.max(1, data.get(3)); }
    public int energy() { return data.get(4); }
    public int energyCapacity() { return Math.max(1, data.get(5)); }
    public boolean generating() { return data.get(6) != 0; }
    public int generationRate() { return data.get(7); }
}
