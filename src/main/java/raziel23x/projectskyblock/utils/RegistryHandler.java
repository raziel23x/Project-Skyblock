package raziel23x.projectskyblock.utils;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.blocks.*;
import raziel23x.projectskyblock.items.gems.RepairGem;
import raziel23x.projectskyblock.items.ItemBase;
import raziel23x.projectskyblock.items.mixingbowl.MixingBowl;

public class RegistryHandler {


    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ProjectSkyblock.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ProjectSkyblock.MOD_ID);
    private static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ProjectSkyblock.MOD_ID);

    //Items
    public static final RegistryObject<Item> REPAIR_GEM = ITEMS.register("repair_gem", RepairGem::new);
    public static final RegistryObject<Item> RED_REAGENT = ITEMS.register("red_reagent", ItemBase::new);
    public static final RegistryObject<Item> GREEN_REAGENT = ITEMS.register("green_reagent", ItemBase::new);
    public static final RegistryObject<Item> BLUE_REAGENT = ITEMS.register("blue_reagent", ItemBase::new);
    public static final RegistryObject<Item> MIXING_BOWL = ITEMS.register("mixing_bowl", MixingBowl::new);


    //Blocks
    public static final RegistryObject<Block> RED_REAGENT_BLOCK = BLOCKS.register("red_reagent_block", RedReagentBlock::new);
    public static final RegistryObject<Block> GREEN_REAGENT_BLOCK = BLOCKS.register("green_reagent_block", GreenReagentBlock::new);
    public static final RegistryObject<Block> BLUE_REAGENT_BLOCK = BLOCKS.register("blue_reagent_block", BlueReagentBlock::new);
    public static final RegistryObject<Block> COBBLESTONE_GENERATOR_BLOCK  = BLOCKS.register("cobblestone_generator_block", CobblestoneGeneratorBlock::new);
    public static final RegistryObject<Block> LAVA_GENERATOR_BLOCK  = BLOCKS.register("lava_generator_block", LavaGeneratorBlock::new);
    public static final RegistryObject<Block> WATER_GENERATOR_BLOCK  = BLOCKS.register("water_generator_block", WaterGeneratorBlock::new);

    //Block Items
    public static final RegistryObject<Item> RED_REAGENT_BLOCK_ITEM = ITEMS.register("red_reagent_block", () -> new BlockItemBase(RED_REAGENT_BLOCK.get()));
    public static final RegistryObject<Item> GREEN_REAGENT_BLOCK_ITEM = ITEMS.register("green_reagent_block", () -> new BlockItemBase(GREEN_REAGENT_BLOCK.get()));
    public static final RegistryObject<Item> BLUE_REAGENT_BLOCK_ITEM = ITEMS.register("blue_reagent_block", () -> new BlockItemBase(BLUE_REAGENT_BLOCK.get()));
    public static final RegistryObject<Item> COBBLESTONE_GENERATOR_BLOCK_ITEM = ITEMS.register("cobblestone_generator_block", () -> new BlockItemBaseCobblestoneGenerator(COBBLESTONE_GENERATOR_BLOCK.get()));
    public static final RegistryObject<Item> LAVA_GENERATOR_BLOCK_ITEM = ITEMS.register("lava_generator_block", () -> new BlockItemBaseLavaGenerator(LAVA_GENERATOR_BLOCK.get()));
    public static final RegistryObject<Item> WATER_GENERATOR_BLOCK_ITEM = ITEMS.register("water_generator_block", () -> new BlockItemBaseWaterGenerator(WATER_GENERATOR_BLOCK.get()));
    //
    public static final RegistryObject<TileEntityType<CobblestoneGeneratorTile>> COBBLEGENERATOR_TILE = TILES.register("cobblestone_generator_block", () -> TileEntityType.Builder.create(CobblestoneGeneratorTile::new, COBBLESTONE_GENERATOR_BLOCK.get()).build(null));
    public static final RegistryObject<TileEntityType<LavaGeneratorTile>> LAVAGENERATOR_TILE = TILES.register("lava_generator_block", () -> TileEntityType.Builder.create(LavaGeneratorTile::new, LAVA_GENERATOR_BLOCK.get()).build(null));
    public static final RegistryObject<TileEntityType<WaterGeneratorTile>> WATERGENERATOR_TILE = TILES.register("water_generator_block", () -> TileEntityType.Builder.create(WaterGeneratorTile::new, WATER_GENERATOR_BLOCK.get()).build(null));

    public static void init() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        //CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        //ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());


    }



}
