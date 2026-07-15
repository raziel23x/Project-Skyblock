package raziel23x.projectskyblock.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.block.ResourceGeneratorBlock;

public final class ModBlocks {
    private static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(ProjectSkyblock.MOD_ID);
    private static final DeferredRegister.Items BLOCK_ITEMS =
            DeferredRegister.createItems(ProjectSkyblock.MOD_ID);

    public static final DeferredBlock<Block> COBBLESTONE_GENERATOR =
            generator("cobblestone_generator", MapColor.STONE,
                    ResourceGeneratorBlock.Output.COBBLESTONE);

    public static final DeferredBlock<Block> WATER_GENERATOR =
            generator("water_generator", MapColor.WATER,
                    ResourceGeneratorBlock.Output.WATER);

    public static final DeferredBlock<Block> LAVA_GENERATOR =
            generator("lava_generator", MapColor.FIRE,
                    ResourceGeneratorBlock.Output.LAVA);

    public static final DeferredItem<BlockItem> COBBLESTONE_GENERATOR_ITEM =
            blockItem("cobblestone_generator", COBBLESTONE_GENERATOR);

    public static final DeferredItem<BlockItem> WATER_GENERATOR_ITEM =
            blockItem("water_generator", WATER_GENERATOR);

    public static final DeferredItem<BlockItem> LAVA_GENERATOR_ITEM =
            blockItem("lava_generator", LAVA_GENERATOR);

    private static DeferredBlock<Block> generator(
            String name,
            MapColor color,
            ResourceGeneratorBlock.Output output) {

        return BLOCKS.register(name, () -> new ResourceGeneratorBlock(
                BlockBehaviour.Properties.of()
                        .mapColor(color)
                        .strength(2.0F, 3.0F)
                        .sound(SoundType.STONE)
                        .requiresCorrectToolForDrops()
                        .noOcclusion(),
                output
        ));
    }

    private static DeferredItem<BlockItem> blockItem(
            String name,
            DeferredBlock<Block> block) {

        return BLOCK_ITEMS.register(
                name,
                () -> new BlockItem(block.get(), new Item.Properties())
        );
    }

    private ModBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ITEMS.register(eventBus);
    }
}
