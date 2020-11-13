package raziel23x.projectskyblock.utils;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.blocks.BlockItemBase;
import raziel23x.projectskyblock.blocks.BlueReagentBlock;
import raziel23x.projectskyblock.blocks.GreenReagentBlock;
import raziel23x.projectskyblock.blocks.RedReagentBlock;
import raziel23x.projectskyblock.items.ItemBaseSingleStack;
import raziel23x.projectskyblock.items.ItemBase;
import raziel23x.projectskyblock.items.MixingBowl;

public class RegistryHandler {


    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ProjectSkyblock.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ProjectSkyblock.MOD_ID);

    //Items
    public static final RegistryObject<Item> REPAIR_GEM = ITEMS.register("repair_gem", ItemBaseSingleStack::new);
    public static final RegistryObject<Item> RED_REAGENT = ITEMS.register("red_reagent", ItemBase::new);
    public static final RegistryObject<Item> GREEN_REAGENT = ITEMS.register("green_reagent", ItemBase::new);
    public static final RegistryObject<Item> BLUE_REAGENT = ITEMS.register("blue_reagent", ItemBase::new);
    public static final RegistryObject<Item> MIXING_BOWL = ITEMS.register("mixing_bowl", MixingBowl::new);


    //Blocks
    public static final RegistryObject<Block> RED_REAGENT_BLOCK = BLOCKS.register("red_reagent_block", RedReagentBlock::new);
    public static final RegistryObject<Block> GREEN_REAGENT_BLOCK = BLOCKS.register("green_reagent_block", GreenReagentBlock::new);
    public static final RegistryObject<Block> BLUE_REAGENT_BLOCK = BLOCKS.register("blue_reagent_block", BlueReagentBlock::new);

    //Block Items
    public static final RegistryObject<Item> RED_REAGENT_BLOCK_ITEM = ITEMS.register("red_reagent_block", () -> new BlockItemBase(RED_REAGENT_BLOCK.get()));
    public static final RegistryObject<Item> GREEN_REAGENT_BLOCK_ITEM = ITEMS.register("green_reagent_block", () -> new BlockItemBase(GREEN_REAGENT_BLOCK.get()));
    public static final RegistryObject<Item> BLUE_REAGENT_BLOCK_ITEM = ITEMS.register("blue_reagent_block", () -> new BlockItemBase(BLUE_REAGENT_BLOCK.get()));

    public static void init() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());


    }


}
