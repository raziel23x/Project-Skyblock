package raziel23x.projectskyblock.init;

import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.blocks.*;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ProjectSkyblock.MOD_ID);
    //Blocks
    public static final RegistryObject<Block> RED_REAGENT_BLOCK = BLOCKS.register("red_reagent_block", RedReagentBlock::new);
    public static final RegistryObject<Block> GREEN_REAGENT_BLOCK = BLOCKS.register("green_reagent_block", GreenReagentBlock::new);
    public static final RegistryObject<Block> BLUE_REAGENT_BLOCK = BLOCKS.register("blue_reagent_block", BlueReagentBlock::new);
    public static final RegistryObject<Block> COBBLESTONE_GENERATOR_BLOCK = BLOCKS.register("cobblestone_generator_block", CobblestoneGeneratorBlock::new);
    public static final RegistryObject<Block> LAVA_GENERATOR_BLOCK = BLOCKS.register("lava_generator_block", LavaGeneratorBlock::new);
    public static final RegistryObject<Block> WATER_GENERATOR_BLOCK = BLOCKS.register("water_generator_block", WaterGeneratorBlock::new);
    public static final RegistryObject<Block> COBBLESTONE_CRUSHER_BLOCK = BLOCKS.register("cobblestone_crusher_block", CobblestoneCrusherBlock::new);

    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
