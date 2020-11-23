package raziel23x.projectskyblock.init;

import net.minecraft.item.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.blocks.*;
import raziel23x.projectskyblock.items.ItemBase;
import raziel23x.projectskyblock.items.MixingBowl;
import raziel23x.projectskyblock.items.RepairGem;
import raziel23x.projectskyblock.utils.enums.PSItemTier;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ProjectSkyblock.MOD_ID);

    //Items

    public static final RegistryObject<Item> REPAIR_GEM = ITEMS.register("repair_gem", RepairGem::new);
    public static final RegistryObject<Item> RED_REAGENT = ITEMS.register("red_reagent", ItemBase::new);
    public static final RegistryObject<Item> GREEN_REAGENT = ITEMS.register("green_reagent", ItemBase::new);
    public static final RegistryObject<Item> BLUE_REAGENT = ITEMS.register("blue_reagent", ItemBase::new);
    public static final RegistryObject<Item> MIXING_BOWL = ITEMS.register("mixing_bowl", MixingBowl::new);

    //Tools

    public static final RegistryObject<ShovelItem> FLINT_SHOVEL = ITEMS.register("flint_shovel",
            () -> new ShovelItem(PSItemTier.FLINT, 0, 0, new Item.Properties().group(ProjectSkyblock.TAB)));

    public static final RegistryObject<SwordItem> FLINT_SWORD = ITEMS.register("flint_sword",
            () -> new SwordItem(PSItemTier.FLINT, 0, 0, new Item.Properties().group(ProjectSkyblock.TAB)));

    public static final RegistryObject<PickaxeItem> FLINT_PICKAXE = ITEMS.register("flint_pickaxe",
            () -> new PickaxeItem(PSItemTier.FLINT, 0, 0, new Item.Properties().group(ProjectSkyblock.TAB)));

    public static final RegistryObject<AxeItem> FLINT_AXE = ITEMS.register("flint_axe",
            () -> new AxeItem(PSItemTier.FLINT, 0, 0, new Item.Properties().group(ProjectSkyblock.TAB)));

    public static final RegistryObject<HoeItem> FLINT_HOE = ITEMS.register("flint_hoe",
            () -> new HoeItem(PSItemTier.FLINT, 0, 0, new Item.Properties().group(ProjectSkyblock.TAB)));

    public static final RegistryObject<ShearsItem> FLINT_SHEARS = ITEMS.register("flint_shears",
            () -> new ShearsItem(new Item.Properties().group(ProjectSkyblock.TAB).maxDamage(100)));

    //Block Items

    public static final RegistryObject<Item> RED_REAGENT_BLOCK_ITEM = ITEMS.register("red_reagent_block",
            () -> new BlockItemBase(ModBlocks.RED_REAGENT_BLOCK.get()));

    public static final RegistryObject<Item> GREEN_REAGENT_BLOCK_ITEM = ITEMS.register("green_reagent_block",
            () -> new BlockItemBase(ModBlocks.GREEN_REAGENT_BLOCK.get()));

    public static final RegistryObject<Item> BLUE_REAGENT_BLOCK_ITEM = ITEMS.register("blue_reagent_block",
            () -> new BlockItemBase(ModBlocks.BLUE_REAGENT_BLOCK.get()));

    public static final RegistryObject<Item> COBBLESTONE_GENERATOR_BLOCK_ITEM = ITEMS.register("cobblestone_generator_block",
            () -> new BlockItemBaseCobblestoneGenerator(ModBlocks.COBBLESTONE_GENERATOR_BLOCK.get()));

    public static final RegistryObject<Item> LAVA_GENERATOR_BLOCK_ITEM = ITEMS.register("lava_generator_block",
            () -> new BlockItemBaseLavaGenerator(ModBlocks.LAVA_GENERATOR_BLOCK.get()));

    public static final RegistryObject<Item> WATER_GENERATOR_BLOCK_ITEM = ITEMS.register("water_generator_block",
            () -> new BlockItemBaseWaterGenerator(ModBlocks.WATER_GENERATOR_BLOCK.get()));

    public static final RegistryObject<Item> COBBLESTONE_CRUSHER_BLOCK_ITEM = ITEMS.register("cobblestone_crusher_block",
            () -> new BlockItemBaseCobblestoneCrusher(ModBlocks.COBBLESTONE_CRUSHER_BLOCK.get()));
}
