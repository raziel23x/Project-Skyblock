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

    public static final DeferredBlock<Block> RED_REAGENT_BLOCK =
            reagentBlock("red_reagent_block", MapColor.COLOR_RED);

    public static final DeferredBlock<Block> GREEN_REAGENT_BLOCK =
            reagentBlock("green_reagent_block", MapColor.COLOR_GREEN);

    public static final DeferredBlock<Block> BLUE_REAGENT_BLOCK =
            reagentBlock("blue_reagent_block", MapColor.COLOR_BLUE);

    public static final DeferredBlock<Block> COBBLESTONE_GENERATOR =
            generator(
                    "cobblestone_generator",
                    MapColor.STONE,
                    ResourceGeneratorBlock.Output.COBBLESTONE
            );

    public static final DeferredBlock<Block> WATER_GENERATOR =
            generator(
                    "water_generator",
                    MapColor.WATER,
                    ResourceGeneratorBlock.Output.WATER
            );

    public static final DeferredBlock<Block> LAVA_GENERATOR =
            generator(
                    "lava_generator",
                    MapColor.FIRE,
                    ResourceGeneratorBlock.Output.LAVA
            );

    public static final DeferredItem<BlockItem> RED_REAGENT_BLOCK_ITEM =
            blockItem("red_reagent_block", RED_REAGENT_BLOCK);

    public static final DeferredItem<BlockItem> GREEN_REAGENT_BLOCK_ITEM =
            blockItem("green_reagent_block", GREEN_REAGENT_BLOCK);

    public static final DeferredItem<BlockItem> BLUE_REAGENT_BLOCK_ITEM =
            blockItem("blue_reagent_block", BLUE_REAGENT_BLOCK);

    public static final DeferredItem<BlockItem> COBBLESTONE_GENERATOR_ITEM =
            blockItem("cobblestone_generator", COBBLESTONE_GENERATOR);

    public static final DeferredItem<BlockItem> WATER_GENERATOR_ITEM =
            blockItem("water_generator", WATER_GENERATOR);

    public static final DeferredItem<BlockItem> LAVA_GENERATOR_ITEM =
            blockItem("lava_generator", LAVA_GENERATOR);

    private static DeferredBlock<Block> reagentBlock(String name, MapColor color) {
        return BLOCKS.register(
                name,
                () -> new Block(
                        BlockBehaviour.Properties.of()
                                .mapColor(color)
                                .strength(2.0F, 3.0F)
                                .sound(SoundType.AMETHYST)
                                .requiresCorrectToolForDrops()
                )
        );
    }

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
