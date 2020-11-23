package raziel23x.projectskyblock.init;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.blocks.CobblestoneCrusherTile;
import raziel23x.projectskyblock.blocks.CobblestoneGeneratorTile;
import raziel23x.projectskyblock.blocks.LavaGeneratorTile;
import raziel23x.projectskyblock.blocks.WaterGeneratorTile;

public class ModEntityType {

    private static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ProjectSkyblock.MOD_ID);
    //Block TileEntities
    public static final RegistryObject<TileEntityType<CobblestoneGeneratorTile>> COBBLEGENERATOR_TILE = TILES.register("cobblestone_generator_block",
            () -> TileEntityType.Builder.create(CobblestoneGeneratorTile::new, ModBlocks.COBBLESTONE_GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<LavaGeneratorTile>> LAVAGENERATOR_TILE = TILES.register("lava_generator_block",
            () -> TileEntityType.Builder.create(LavaGeneratorTile::new, ModBlocks.LAVA_GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<WaterGeneratorTile>> WATERGENERATOR_TILE = TILES.register("water_generator_block",
            () -> TileEntityType.Builder.create(WaterGeneratorTile::new, ModBlocks.WATER_GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<CobblestoneCrusherTile>> COBBLECRUSHER_TILE = TILES.register("cobblestone_crusher_block",
            () -> TileEntityType.Builder.create(CobblestoneCrusherTile::new, ModBlocks.COBBLESTONE_CRUSHER_BLOCK.get()).build(null));

    public static void init() {
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
