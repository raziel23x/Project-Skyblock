package raziel23x.projectskyblock.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.config.PSConfigBuilder;

@EventBusSubscriber(modid = ProjectSkyblock.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class PSRepairTick {
    public static int repairTickRate = PSConfigBuilder.REPAIR_GEM_DELAY.get();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        PlayerInventory inv = event.player.inventory;
        EnderChestInventory end_inv = player.getInventoryEnderChest();

        boolean doIt = false;

        // if its time for a repair then check inventory, find Gem, make repair and exit
        // if RepairGem is found the doIt is set to true, exit For loop
        if (player.ticksExisted % repairTickRate == 0) {
            for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
                ItemStack stack = inv.getStackInSlot(slot);
                if (stack.getItem() == RegistryHandler.REPAIR_GEM.get()) {
                    doIt = true;
                    break;
                }
            }

            // if doIt is false then RepairGem is not yet found, so check EnderChest
            // if RepairGem is found the doIt is set to true, exit For loop
            if (!doIt) {
                for (int slot = 0; slot < end_inv.getSizeInventory(); slot++) {
                    ItemStack stack = end_inv.getStackInSlot(slot);
                    if (stack.getItem() == RegistryHandler.REPAIR_GEM.get()) {
                        doIt = true;
                        break;
                    }
                }
            }

            // if doIt is false then RepairGem is not yet found, so check Curios
            // if RepairGem is found the doIt is set to true
            if (!doIt) {
                if (CuriosUtil.findMod()) {
                    if (CuriosUtil.findItem(RegistryHandler.REPAIR_GEM.get(), player) != ItemStack.EMPTY) {
                        doIt = true;
                    }
                }
            }

            // if doIt is true then RepairGem was found, so call Repair
            if (doIt) repair(player, inv);
        }
    }

    private static void repair(PlayerEntity player, PlayerInventory inv) {
        for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
            ItemStack target = inv.getStackInSlot(slot);
            if (target.isEmpty() || !target.isDamaged() || !target.getItem().isRepairable(target)) {
                continue;
            }
            target.setDamage(target.getDamage() - 1);
            // comment this out if you want to repair everything one time per tick
            return; // return here won't repair any more items this tick, stops at 1.
        }
    }
}