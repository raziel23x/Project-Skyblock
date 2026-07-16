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
import org.joml.Vector3f;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.block.CobblestoneCrusherBlock;
import raziel23x.projectskyblock.block.CreativeEnergyCellBlock;
import raziel23x.projectskyblock.block.ReagentBlock;
import raziel23x.projectskyblock.block.ResourceGeneratorBlock;
import raziel23x.projectskyblock.block.ThermalGeneratorBlock;
import raziel23x.projectskyblock.item.GeneratorBlockItem;
import raziel23x.projectskyblock.item.ReagentBlockItem;

public final class ModBlocks {
    private static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(ProjectSkyblock.MOD_ID);
    private static final DeferredRegister.Items BLOCK_ITEMS =
            DeferredRegister.createItems(ProjectSkyblock.MOD_ID);

    private static final Vector3f RED = new Vector3f(1.0F, 0.12F, 0.08F);
    private static final Vector3f GREEN = new Vector3f(0.12F, 1.0F, 0.20F);
    private static final Vector3f BLUE = new Vector3f(0.15F, 0.35F, 1.0F);

    public static final DeferredBlock<Block> RED_REAGENT_BLOCK =
            reagentBlock("red_reagent_block", MapColor.COLOR_RED, RED);

    public static final DeferredBlock<Block> GREEN_REAGENT_BLOCK =
            reagentBlock("green_reagent_block", MapColor.COLOR_GREEN, GREEN);

    public static final DeferredBlock<Block> BLUE_REAGENT_BLOCK =
            reagentBlock("blue_reagent_block", MapColor.COLOR_BLUE, BLUE);

    public static final DeferredBlock<Block> COBBLESTONE_GENERATOR =
            generator("cobblestone_generator", MapColor.STONE,
                    ResourceGeneratorBlock.Output.COBBLESTONE);

    public static final DeferredBlock<Block> WATER_GENERATOR =
            generator("water_generator", MapColor.WATER,
                    ResourceGeneratorBlock.Output.WATER);

    public static final DeferredBlock<Block> LAVA_GENERATOR =
            generator("lava_generator", MapColor.FIRE,
                    ResourceGeneratorBlock.Output.LAVA);

    public static final DeferredBlock<Block> COBBLESTONE_CRUSHER =
            BLOCKS.register(
                    "cobblestone_crusher",
                    () -> new CobblestoneCrusherBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.STONE)
                                    .strength(3.0F, 6.0F)
                                    .sound(SoundType.STONE)
                                    .requiresCorrectToolForDrops()
                    )
            );


    public static final DeferredBlock<Block> THERMAL_GENERATOR =
            BLOCKS.register(
                    "thermal_generator",
                    () -> new ThermalGeneratorBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.METAL)
                                    .strength(4.0F, 8.0F)
                                    .sound(SoundType.METAL)
                                    .requiresCorrectToolForDrops()
                    )
            );

    public static final DeferredBlock<Block> CREATIVE_ENERGY_CELL =
            BLOCKS.register(
                    "creative_energy_cell",
                    () -> new CreativeEnergyCellBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.COLOR_CYAN)
                                    .strength(4.0F, 10.0F)
                                    .sound(SoundType.METAL)
                                    .lightLevel(state -> 10)
                    )
            );

    public static final DeferredItem<BlockItem> RED_REAGENT_BLOCK_ITEM =
            reagentBlockItem("red_reagent_block", RED_REAGENT_BLOCK, RED);

    public static final DeferredItem<BlockItem> GREEN_REAGENT_BLOCK_ITEM =
            reagentBlockItem("green_reagent_block", GREEN_REAGENT_BLOCK, GREEN);

    public static final DeferredItem<BlockItem> BLUE_REAGENT_BLOCK_ITEM =
            reagentBlockItem("blue_reagent_block", BLUE_REAGENT_BLOCK, BLUE);

    public static final DeferredItem<BlockItem> COBBLESTONE_GENERATOR_ITEM =
            generatorBlockItem("cobblestone_generator", COBBLESTONE_GENERATOR,
                    ResourceGeneratorBlock.Output.COBBLESTONE);

    public static final DeferredItem<BlockItem> WATER_GENERATOR_ITEM =
            generatorBlockItem("water_generator", WATER_GENERATOR,
                    ResourceGeneratorBlock.Output.WATER);

    public static final DeferredItem<BlockItem> LAVA_GENERATOR_ITEM =
            generatorBlockItem("lava_generator", LAVA_GENERATOR,
                    ResourceGeneratorBlock.Output.LAVA);

    public static final DeferredItem<BlockItem> COBBLESTONE_CRUSHER_ITEM =
            blockItem("cobblestone_crusher", COBBLESTONE_CRUSHER);


    public static final DeferredItem<BlockItem> THERMAL_GENERATOR_ITEM =
            blockItem("thermal_generator", THERMAL_GENERATOR);

    public static final DeferredItem<BlockItem> CREATIVE_ENERGY_CELL_ITEM =
            blockItem("creative_energy_cell", CREATIVE_ENERGY_CELL);

    private static DeferredBlock<Block> reagentBlock(
            String name,
            MapColor color,
            Vector3f particleColor) {
        return BLOCKS.register(
                name,
                () -> new ReagentBlock(
                        BlockBehaviour.Properties.of()
                                .mapColor(color)
                                .strength(2.0F, 3.0F)
                                .sound(SoundType.AMETHYST)
                                .requiresCorrectToolForDrops(),
                        particleColor
                )
        );
    }

    private static DeferredBlock<Block> generator(
            String name,
            MapColor color,
            ResourceGeneratorBlock.Output output) {
        return BLOCKS.register(
                name,
                () -> new ResourceGeneratorBlock(
                        BlockBehaviour.Properties.of()
                                .mapColor(color)
                                .strength(2.0F, 3.0F)
                                .sound(SoundType.STONE)
                                .requiresCorrectToolForDrops()
                                .noOcclusion(),
                        output
                )
        );
    }

    private static DeferredItem<BlockItem> reagentBlockItem(
            String name,
            DeferredBlock<Block> block,
            Vector3f particleColor) {
        return BLOCK_ITEMS.register(
                name,
                () -> new ReagentBlockItem(
                        block.get(),
                        new Item.Properties(),
                        particleColor
                )
        );
    }

    private static DeferredItem<BlockItem> generatorBlockItem(
            String name,
            DeferredBlock<Block> block,
            ResourceGeneratorBlock.Output output) {
        return BLOCK_ITEMS.register(
                name,
                () -> new GeneratorBlockItem(
                        block.get(),
                        new Item.Properties(),
                        output
                )
        );
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
