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
                        output.accept(ModItems.MIXING_BOWL.get());

                        output.accept(ModItems.RED_REAGENT.get());
                        output.accept(ModItems.GREEN_REAGENT.get());
                        output.accept(ModItems.BLUE_REAGENT.get());

                        output.accept(ModBlocks.RED_REAGENT_BLOCK_ITEM.get());
                        output.accept(ModBlocks.GREEN_REAGENT_BLOCK_ITEM.get());
                        output.accept(ModBlocks.BLUE_REAGENT_BLOCK_ITEM.get());

                        output.accept(ModBlocks.COBBLESTONE_GENERATOR_ITEM.get());
                        output.accept(ModBlocks.WATER_GENERATOR_ITEM.get());
                        output.accept(ModBlocks.LAVA_GENERATOR_ITEM.get());
                        output.accept(ModBlocks.MATERIAL_CRUSHER_ITEM.get());
                        output.accept(ModBlocks.BASIC_ENERGY_CABLE_ITEM.get());
                        output.accept(ModBlocks.STRUCTURAL_ENERGY_FRAME_ITEM.get());
                        output.accept(ModBlocks.CREATIVE_ENERGY_CELL_ITEM.get());
                        output.accept(ModBlocks.THERMAL_GENERATOR_ITEM.get());

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
