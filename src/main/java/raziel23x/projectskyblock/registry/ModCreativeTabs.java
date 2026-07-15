package raziel23x.projectskyblock.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import raziel23x.projectskyblock.ProjectSkyblock;

public final class ModCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ProjectSkyblock.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register(
            "main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.projectskyblock.main"))
                    .icon(() -> ModItems.REPAIR_GEM.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.REPAIR_GEM.get());
                        output.accept(ModItems.FLINT_SWORD.get());
                        output.accept(ModItems.FLINT_PICKAXE.get());
                        output.accept(ModItems.FLINT_AXE.get());
                        output.accept(ModItems.FLINT_SHOVEL.get());
                        output.accept(ModItems.FLINT_HOE.get());
                        output.accept(ModItems.FLINT_SHEARS.get());
                        output.accept(ModItems.WOODEN_SHEARS.get());
                        output.accept(ModItems.FLINT_HELMET.get());
                        output.accept(ModItems.FLINT_CHESTPLATE.get());
                        output.accept(ModItems.FLINT_LEGGINGS.get());
                        output.accept(ModItems.FLINT_BOOTS.get());
                        output.accept(ModItems.WOODEN_HELMET.get());
                        output.accept(ModItems.WOODEN_CHESTPLATE.get());
                        output.accept(ModItems.WOODEN_LEGGINGS.get());
                        output.accept(ModItems.WOODEN_BOOTS.get());
                    })
                    .build()
    );

    private ModCreativeTabs() {
    }

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }
}
