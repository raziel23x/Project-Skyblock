package raziel23x.projectskyblock.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.blockentity.CobblestoneCrusherBlockEntity;
import raziel23x.projectskyblock.blockentity.CreativeEnergyCellBlockEntity;
import raziel23x.projectskyblock.blockentity.ResourceGeneratorBlockEntity;

public final class ModBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ProjectSkyblock.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ResourceGeneratorBlockEntity>>
            RESOURCE_GENERATOR = BLOCK_ENTITY_TYPES.register(
                    "resource_generator",
                    () -> BlockEntityType.Builder.of(
                            ResourceGeneratorBlockEntity::new,
                            ModBlocks.COBBLESTONE_GENERATOR.get(),
                            ModBlocks.WATER_GENERATOR.get(),
                            ModBlocks.LAVA_GENERATOR.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CobblestoneCrusherBlockEntity>>
            COBBLESTONE_CRUSHER = BLOCK_ENTITY_TYPES.register(
                    "cobblestone_crusher",
                    () -> BlockEntityType.Builder.of(
                            CobblestoneCrusherBlockEntity::new,
                            ModBlocks.COBBLESTONE_CRUSHER.get()
                    ).build(null)
            );


    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeEnergyCellBlockEntity>>
            CREATIVE_ENERGY_CELL = BLOCK_ENTITY_TYPES.register(
                    "creative_energy_cell",
                    () -> BlockEntityType.Builder.of(
                            CreativeEnergyCellBlockEntity::new,
                            ModBlocks.CREATIVE_ENERGY_CELL.get()
                    ).build(null)
            );

    private ModBlockEntities() {
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
