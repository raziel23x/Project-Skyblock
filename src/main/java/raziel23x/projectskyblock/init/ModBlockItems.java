package raziel23x.projectskyblock.init;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.gameObjs.blocks.blockitembase.*;

public class ModBlockItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ProjectSkyblock.MOD_ID);

    //Machines

    public static final RegistryObject<Item> COBBLESTONE_CRUSHER_BLOCK_ITEM = ITEMS.register("cobblestone_crusher_block",
            () -> new CobblestoneCrusher(ModBlocks.COBBLESTONE_CRUSHER_BLOCK.get()));

    //Reagents

    public static final RegistryObject<Item> RED_REAGENT_BLOCK_ITEM = ITEMS.register("red_reagent_block",
            () -> new BlockItemBase(ModBlocks.RED_REAGENT_BLOCK.get()));

    public static final RegistryObject<Item> GREEN_REAGENT_BLOCK_ITEM = ITEMS.register("green_reagent_block",
            () -> new BlockItemBase(ModBlocks.GREEN_REAGENT_BLOCK.get()));

    public static final RegistryObject<Item> BLUE_REAGENT_BLOCK_ITEM = ITEMS.register("blue_reagent_block",
            () -> new BlockItemBase(ModBlocks.BLUE_REAGENT_BLOCK.get()));

    //Fluid Generators

    public static final RegistryObject<Item> LAVA_GENERATOR_BLOCK_ITEM = ITEMS.register("lava_generator_block",
            () -> new LavaGenerator(ModBlocks.LAVA_GENERATOR_BLOCK.get()));

    public static final RegistryObject<Item> WATER_GENERATOR_BLOCK_ITEM = ITEMS.register("water_generator_block",
            () -> new WaterGenerator(ModBlocks.WATER_GENERATOR_BLOCK.get()));

    //Block Generators
    public static final RegistryObject<Item> COBBLESTONE_GENERATOR_BLOCK_ITEM = ITEMS.register("cobblestone_generator_block",
            () -> new CobblestoneGenerator(ModBlocks.COBBLESTONE_GENERATOR_BLOCK.get()));

    public static final RegistryObject<Item> DIRT_GENERATOR_BLOCK_ITEM = ITEMS.register("dirt_generator_block",
            () -> new DirtGenerator(ModBlocks.DIRT_GENERATOR_BLOCK.get()));

    public static final RegistryObject<Item> GRASSBLOCK_GENERATOR_BLOCK_ITEM = ITEMS.register("grassblock_generator_block",
            () -> new DirtGenerator(ModBlocks.GRASSBLOCK_GENERATOR_BLOCK.get()));

}
