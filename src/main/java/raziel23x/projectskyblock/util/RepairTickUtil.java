package raziel23x.projectskyblock.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.config.ConfigBuilder;

@EventBusSubscriber(modid = ProjectSkyblock.MOD_ID, bus = EventBusSubscriber.Bus.FORGE )
public class RepairTickUtil
{
    public static int repairTickRate = ConfigBuilder.REPAIR_GEM_DELAY.get();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        PlayerEntity player = event.player;
        PlayerInventory inv = event.player.inventory;
        EnderChestInventory end_inv = player.getInventoryEnderChest();

        for (int slot = 0; slot < inv.getSizeInventory(); slot++)
        {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.getItem() == RegistryHandler.REPAIR_GEM.get())
            {
                if (player.ticksExisted % repairTickRate == 0)
                {
                    repair(player, inv);
                }
            }
        }

        for (int slot = 0; slot < end_inv.getSizeInventory(); slot++)
        {
            ItemStack stack = end_inv.getStackInSlot(slot);
            if (stack.getItem() == RegistryHandler.REPAIR_GEM.get())
            {
                if (player.ticksExisted % repairTickRate == 0)
                {
                    repair(player, inv);
                }
            }
        }

        if (CuriosModCheck.CURIOS.isLoaded())
        {
            if (CuriosUtil.findItem(RegistryHandler.REPAIR_GEM.get(), player) != ItemStack.EMPTY)
            {
                if (player.ticksExisted % repairTickRate == 0)
                {
                    repair(player, inv);
                }
            }
        }
    }

    private static void repair(PlayerEntity player, PlayerInventory inv)
    {
        for(int slot = 0; slot < inv.getSizeInventory(); slot++)
        {
            ItemStack target = inv.getStackInSlot(slot);
            if (!target.isEmpty())
            {
                if (!(target == player.getHeldItemMainhand()))
                {
                    if (target.isDamaged() && target.getItem().isRepairable(target))
                    {
                        target.setDamage(target.getDamage() - 1);
                        return;
                    }
                }
            }
        }
    }
}