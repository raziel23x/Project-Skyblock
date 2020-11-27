package raziel23x.projectskyblock.init;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.gameObjs.blocks.*;
import raziel23x.projectskyblock.gameObjs.blocks.generators.*;

public class ModEntityType {

    public static final DeferredRegister<TileEntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ProjectSkyblock.MOD_ID);

    //Block TileEntities
    //Machines

    public static final RegistryObject<TileEntityType<CobblestoneCrusherTile>> COBBLECRUSHER_TILE = ENTITY_TYPES.register("cobblestone_crusher_block",
            () -> TileEntityType.Builder.create(CobblestoneCrusherTile::new, ModBlocks.COBBLESTONE_CRUSHER_BLOCK.get()).build(null));

    //Liquid Generators

    public static final RegistryObject<TileEntityType<LavaGeneratorTile>> LAVAGENERATOR_TILE = ENTITY_TYPES.register("lava_generator_block",
            () -> TileEntityType.Builder.create(LavaGeneratorTile::new, ModBlocks.LAVA_GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<WaterGeneratorTile>> WATERGENERATOR_TILE = ENTITY_TYPES.register("water_generator_block",
            () -> TileEntityType.Builder.create(WaterGeneratorTile::new, ModBlocks.WATER_GENERATOR_BLOCK.get()).build(null));

    //Block Generators

    public static final RegistryObject<TileEntityType<CobblestoneGeneratorTile>> COBBLEGENERATOR_TILE = ENTITY_TYPES.register("cobblestone_generator_block",
            () -> TileEntityType.Builder.create(CobblestoneGeneratorTile::new, ModBlocks.COBBLESTONE_GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<DirtGeneratorTile>> DIRTGENERATOR_TILE = ENTITY_TYPES.register("dirt_generator_block",
            () -> TileEntityType.Builder.create(DirtGeneratorTile::new, ModBlocks.DIRT_GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<DirtGeneratorTile>> GRASSBLOCKGENERATOR_TILE = ENTITY_TYPES.register("grassblock_generator_block",
            () -> TileEntityType.Builder.create(DirtGeneratorTile::new, ModBlocks.GRASSBLOCK_GENERATOR_BLOCK.get()).build(null));
}
