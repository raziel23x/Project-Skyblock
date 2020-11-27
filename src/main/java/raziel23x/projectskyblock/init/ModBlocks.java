package raziel23x.projectskyblock.init;

import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.gameObjs.blocks.*;
import raziel23x.projectskyblock.gameObjs.blocks.generators.*;
import raziel23x.projectskyblock.gameObjs.blocks.reagents.*;



public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ProjectSkyblock.MOD_ID);
    //Blocks

    //Machines
    public static final RegistryObject<Block> COBBLESTONE_CRUSHER_BLOCK = BLOCKS.register("cobblestone_crusher_block", CobblestoneCrusherBlock::new);
    //Reagents
    public static final RegistryObject<Block> RED_REAGENT_BLOCK = BLOCKS.register("red_reagent_block", RedReagentBlock::new);
    public static final RegistryObject<Block> GREEN_REAGENT_BLOCK = BLOCKS.register("green_reagent_block", GreenReagentBlock::new);
    public static final RegistryObject<Block> BLUE_REAGENT_BLOCK = BLOCKS.register("blue_reagent_block", BlueReagentBlock::new);
    //Fluid Generators
    public static final RegistryObject<Block> LAVA_GENERATOR_BLOCK = BLOCKS.register("lava_generator_block", LavaGeneratorBlock::new);
    public static final RegistryObject<Block> WATER_GENERATOR_BLOCK = BLOCKS.register("water_generator_block", WaterGeneratorBlock::new);
    //Block Generators
    public static final RegistryObject<Block> COBBLESTONE_GENERATOR_BLOCK = BLOCKS.register("cobblestone_generator_block", CobblestoneGeneratorBlock::new);
    public static final RegistryObject<Block> DIRT_GENERATOR_BLOCK = BLOCKS.register("dirt_generator_block", DirtGeneratorBlock::new);
    // public static final RegistryObject<Block> GRAVEL_GENERATOR_BLOCK = BLOCKS.register("gravel_generator_block", GravelGeneratorBlock::new);
    // public static final RegistryObject<Block> CLAY_GENERATOR_BLOCK = BLOCKS.register("clay_generator_block", ClayGeneratorBlock::new);
    // public static final RegistryObject<Block> SAND_GENERATOR_BLOCK = BLOCKS.register("sand_generator_block", SandGeneratorBlock::new);
    public static final RegistryObject<Block> GRASSBLOCK_GENERATOR_BLOCK = BLOCKS.register("grassblock_generator_block", GrassblockGeneratorBlock::new);
    // public static final RegistryObject<Block> ENDSTONE_GENERATOR_BLOCK = BLOCKS.register("endstone_generator_block", EndstoneGeneratorBlock::new);
    // public static final RegistryObject<Block> OBSIDIAN_GENERATOR_BLOCK = BLOCKS.register("obsidian_generator_block", ObsidianGeneratorBlock::new);
    // public static final RegistryObject<Block> SOULSAND_GENERATOR_BLOCK = BLOCKS.register("soulsand_generator_block", SoulsandGeneratorBlock::new);
    // public static final RegistryObject<Block> QUARTZ_GENERATOR_BLOCK = BLOCKS.register("quartz_generator_block", QuartzGeneratorBlock::new);
    // public static final RegistryObject<Block> NETHERRACK_GENERATOR_BLOCK = BLOCKS.register("netherrack_generator_block", NetherrackGeneratorBlock::new);

}
