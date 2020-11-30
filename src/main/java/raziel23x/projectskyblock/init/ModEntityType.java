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

    public static final RegistryObject<TileEntityType<ClayGeneratorTile>> CLAYGENERATOR_TILE = ENTITY_TYPES.register("clay_generator_block",
            () -> TileEntityType.Builder.create(ClayGeneratorTile::new, ModBlocks.CLAY_GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<CobblestoneGeneratorTile>> COBBLEGENERATOR_TILE = ENTITY_TYPES.register("cobblestone_generator_block",
            () -> TileEntityType.Builder.create(CobblestoneGeneratorTile::new, ModBlocks.COBBLESTONE_GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<DirtGeneratorTile>> DIRTGENERATOR_TILE = ENTITY_TYPES.register("dirt_generator_block",
            () -> TileEntityType.Builder.create(DirtGeneratorTile::new, ModBlocks.DIRT_GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<EndstoneGeneratorTile>> ENDSTONEGENERATOR_TILE = ENTITY_TYPES.register("endstone_generator_block",
            () -> TileEntityType.Builder.create(EndstoneGeneratorTile::new, ModBlocks.ENDSTONE_GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<GrassblockGeneratorTile>> GRASSBLOCKGENERATOR_TILE = ENTITY_TYPES.register("grassblock_generator_block",
            () -> TileEntityType.Builder.create(GrassblockGeneratorTile::new, ModBlocks.GRASSBLOCK_GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<GravelGeneratorTile>> GRAVELGENERATOR_TILE = ENTITY_TYPES.register("gravel_generator_block",
            () -> TileEntityType.Builder.create(GravelGeneratorTile::new, ModBlocks.GRAVEL_GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<NetherrackGeneratorTile>> NETHEREACKGENERATOR_TILE = ENTITY_TYPES.register("netherrack_generator_block",
            () -> TileEntityType.Builder.create(NetherrackGeneratorTile::new, ModBlocks.NETHERRACK_GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<ObsidianGeneratorTile>> OBSIDIANGENERATOR_TILE = ENTITY_TYPES.register("obsdian_generator_block",
            () -> TileEntityType.Builder.create(ObsidianGeneratorTile::new, ModBlocks.OBSIDIAN_GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<QuartzGeneratorTile>> QUARTZGENERATOR_TILE = ENTITY_TYPES.register("quartz_generator_block",
            () -> TileEntityType.Builder.create(QuartzGeneratorTile::new, ModBlocks.QUARTZ_GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<RedsandGeneratorTile>> REDSANDGENERATOR_TILE = ENTITY_TYPES.register("redsand_generator_block",
            () -> TileEntityType.Builder.create(RedsandGeneratorTile::new, ModBlocks.REDSAND_GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<SandGeneratorTile>> SANDGENERATOR_TILE = ENTITY_TYPES.register("sand_generator_block",
            () -> TileEntityType.Builder.create(SandGeneratorTile::new, ModBlocks.SAND_GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<SoulsandGeneratorTile>> SOULSANDGENERATOR_TILE = ENTITY_TYPES.register("soulsand_generator_block",
            () -> TileEntityType.Builder.create(SoulsandGeneratorTile::new, ModBlocks.SOULSAND_GENERATOR_BLOCK.get()).build(null));
}
