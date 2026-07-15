package raziel23x.projectskyblock.repair;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import raziel23x.projectskyblock.compat.curios.CuriosRepairCompat;
import raziel23x.projectskyblock.config.CommonConfig;
import raziel23x.projectskyblock.config.IntegrationConfig;
import raziel23x.projectskyblock.registry.ModItems;

/**
 * Server-side behavior for the Repair Gem.
 *
 * <p>The gem must be carried in the player's inventory/equipment or equipped
 * in a Curios slot. Ender Chests are deliberately never inspected.</p>
 */
public final class RepairGemHandler {
    private RepairGemHandler() {
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !CommonConfig.REPAIR_GEM_ENABLED.get()) {
            return;
        }

        int interval = CommonConfig.REPAIR_GEM_INTERVAL_TICKS.get();
        if (player.tickCount % interval != 0) {
            return;
        }

        Inventory inventory = player.getInventory();
        boolean hasRepairGem = containsRepairGem(inventory)
                || (IntegrationConfig.CURIOS_REPAIR_GEM.get()
                && CuriosRepairCompat.hasEquippedRepairGem(player));

        if (!hasRepairGem) {
            return;
        }

        repairOneItem(inventory, CommonConfig.REPAIR_GEM_AMOUNT.get());
    }

    private static boolean containsRepairGem(Inventory inventory) {
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (inventory.getItem(slot).is(ModItems.REPAIR_GEM.get())) {
                return true;
            }
        }
        return false;
    }

    private static void repairOneItem(Inventory inventory, int repairAmount) {
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack target = inventory.getItem(slot);

            if (target.isEmpty()
                    || target.is(ModItems.REPAIR_GEM.get())
                    || !target.isDamageableItem()
                    || !target.isDamaged()) {
                continue;
            }

            target.setDamageValue(Math.max(0, target.getDamageValue() - repairAmount));
            return;
        }
    }
}
