package raziel23x.projectskyblock.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.blockentity.ResourceGeneratorBlockEntity;

public final class ModBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ProjectSkyblock.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ResourceGeneratorBlockEntity>> RESOURCE_GENERATOR =
            BLOCK_ENTITY_TYPES.register("resource_generator", () -> BlockEntityType.Builder.of(
                    ResourceGeneratorBlockEntity::new,
                    ModBlocks.COBBLESTONE_GENERATOR.get(),
                    ModBlocks.WATER_GENERATOR.get(),
                    ModBlocks.LAVA_GENERATOR.get()
            ).build(null));

    private ModBlockEntities() {}

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
