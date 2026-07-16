package raziel23x.projectskyblock.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.menu.CobblestoneCrusherMenu;
import raziel23x.projectskyblock.menu.ThermalGeneratorMenu;

public final class ModMenus {
    private static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, ProjectSkyblock.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<CobblestoneCrusherMenu>>
            COBBLESTONE_CRUSHER = MENU_TYPES.register(
                    "cobblestone_crusher",
                    () -> new MenuType<>(
                            CobblestoneCrusherMenu::new,
                            FeatureFlags.DEFAULT_FLAGS
                    )
            );

    public static final DeferredHolder<MenuType<?>, MenuType<ThermalGeneratorMenu>>
            THERMAL_GENERATOR = MENU_TYPES.register(
                    "thermal_generator",
                    () -> new MenuType<>(ThermalGeneratorMenu::new, FeatureFlags.DEFAULT_FLAGS)
            );

    private ModMenus() {
    }

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
