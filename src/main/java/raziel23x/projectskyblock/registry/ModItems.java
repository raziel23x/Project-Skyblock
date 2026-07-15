package raziel23x.projectskyblock.registry;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.item.RepairGemItem;

public final class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ProjectSkyblock.MOD_ID);

    public static final DeferredItem<RepairGemItem> REPAIR_GEM = ITEMS.registerItem(
            "repair_gem",
            RepairGemItem::new,
            new Item.Properties().stacksTo(1)
    );

    private ModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
