package raziel23x.projectskyblock.utils;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.blocks.BlockItemBase;
import raziel23x.projectskyblock.blocks.RepairGemBlock;
import raziel23x.projectskyblock.items.ItemBaseSingleStack;
import raziel23x.projectskyblock.items.ItemBase;
import raziel23x.projectskyblock.items.MixingBowl;

public class RegistryHandler {


    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ProjectSkyblock.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ProjectSkyblock.MOD_ID);
    //Items
    public static final RegistryObject<Item> REPAIR_GEM = ITEMS.register("repair_gem", ItemBaseSingleStack::new);
    public static final RegistryObject<Item> REAGENT_RED = ITEMS.register("red_reagent", ItemBase::new);
    public static final RegistryObject<Item> REAGENT_GREEN = ITEMS.register("green_reagent", ItemBase::new);
    public static final RegistryObject<Item> REAGENT_BLUE = ITEMS.register("blue_reagent", ItemBase::new);
    public static final RegistryObject<Item> MIXING_BOWL = ITEMS.register("mixing_bowl", MixingBowl::new);


    //Blocks
    public static final RegistryObject<Block> REPAIR_GEM_Block = BLOCKS.register("repair_gem_block", RepairGemBlock::new);

    //Block Items
    public static final RegistryObject<Item> REPAIR_GEM_Block_ITEM = ITEMS.register("repair_gem_block", () -> new BlockItemBase(REPAIR_GEM_Block.get()));

    public static void init() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());


    }


}
