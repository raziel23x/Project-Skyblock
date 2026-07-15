package raziel23x.projectskyblock.registry;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.item.MixingBowlItem;
import raziel23x.projectskyblock.item.ModTiers;
import raziel23x.projectskyblock.item.RepairGemItem;

public final class ModItems {
    private static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(ProjectSkyblock.MOD_ID);

    public static final DeferredItem<RepairGemItem> REPAIR_GEM = ITEMS.registerItem(
            "repair_gem",
            RepairGemItem::new,
            new Item.Properties().stacksTo(1)
    );

    public static final DeferredItem<Item> RED_REAGENT = ITEMS.registerSimpleItem(
            "red_reagent",
            new Item.Properties()
    );

    public static final DeferredItem<Item> GREEN_REAGENT = ITEMS.registerSimpleItem(
            "green_reagent",
            new Item.Properties()
    );

    public static final DeferredItem<Item> BLUE_REAGENT = ITEMS.registerSimpleItem(
            "blue_reagent",
            new Item.Properties()
    );

    public static final DeferredItem<MixingBowlItem> MIXING_BOWL = ITEMS.registerItem(
            "mixing_bowl",
            MixingBowlItem::new,
            new Item.Properties().stacksTo(1)
    );

    public static final DeferredItem<SwordItem> FLINT_SWORD = ITEMS.register(
            "flint_sword",
            () -> new SwordItem(ModTiers.FLINT, new Item.Properties()
                    .attributes(SwordItem.createAttributes(ModTiers.FLINT, 3, -2.4F)))
    );

    public static final DeferredItem<PickaxeItem> FLINT_PICKAXE = ITEMS.register(
            "flint_pickaxe",
            () -> new PickaxeItem(ModTiers.FLINT, new Item.Properties()
                    .attributes(PickaxeItem.createAttributes(ModTiers.FLINT, 1.0F, -2.8F)))
    );

    public static final DeferredItem<AxeItem> FLINT_AXE = ITEMS.register(
            "flint_axe",
            () -> new AxeItem(ModTiers.FLINT, new Item.Properties()
                    .attributes(AxeItem.createAttributes(ModTiers.FLINT, 5.5F, -3.2F)))
    );

    public static final DeferredItem<ShovelItem> FLINT_SHOVEL = ITEMS.register(
            "flint_shovel",
            () -> new ShovelItem(ModTiers.FLINT, new Item.Properties()
                    .attributes(ShovelItem.createAttributes(ModTiers.FLINT, 1.5F, -3.0F)))
    );

    public static final DeferredItem<HoeItem> FLINT_HOE = ITEMS.register(
            "flint_hoe",
            () -> new HoeItem(ModTiers.FLINT, new Item.Properties()
                    .attributes(HoeItem.createAttributes(ModTiers.FLINT, -1.0F, -2.0F)))
    );

    public static final DeferredItem<ShearsItem> FLINT_SHEARS = ITEMS.register(
            "flint_shears",
            () -> new ShearsItem(new Item.Properties().durability(89))
    );

    public static final DeferredItem<ShearsItem> WOODEN_SHEARS = ITEMS.register(
            "wooden_shears",
            () -> new ShearsItem(new Item.Properties().durability(59))
    );

    public static final DeferredItem<ArmorItem> FLINT_HELMET =
            armor("flint_helmet", ModArmorMaterials.FLINT, ArmorItem.Type.HELMET, 110);

    public static final DeferredItem<ArmorItem> FLINT_CHESTPLATE =
            armor("flint_chestplate", ModArmorMaterials.FLINT, ArmorItem.Type.CHESTPLATE, 160);

    public static final DeferredItem<ArmorItem> FLINT_LEGGINGS =
            armor("flint_leggings", ModArmorMaterials.FLINT, ArmorItem.Type.LEGGINGS, 150);

    public static final DeferredItem<ArmorItem> FLINT_BOOTS =
            armor("flint_boots", ModArmorMaterials.FLINT, ArmorItem.Type.BOOTS, 130);

    public static final DeferredItem<ArmorItem> WOODEN_HELMET =
            armor("wooden_helmet", ModArmorMaterials.WOODEN, ArmorItem.Type.HELMET, 110);

    public static final DeferredItem<ArmorItem> WOODEN_CHESTPLATE =
            armor("wooden_chestplate", ModArmorMaterials.WOODEN, ArmorItem.Type.CHESTPLATE, 160);

    public static final DeferredItem<ArmorItem> WOODEN_LEGGINGS =
            armor("wooden_leggings", ModArmorMaterials.WOODEN, ArmorItem.Type.LEGGINGS, 150);

    public static final DeferredItem<ArmorItem> WOODEN_BOOTS =
            armor("wooden_boots", ModArmorMaterials.WOODEN, ArmorItem.Type.BOOTS, 130);

    private ModItems() {
    }

    private static DeferredItem<ArmorItem> armor(
            String name,
            net.minecraft.core.Holder<net.minecraft.world.item.ArmorMaterial> material,
            ArmorItem.Type type,
            int durability) {
        return ITEMS.register(
                name,
                () -> new ArmorItem(
                        material,
                        type,
                        new Item.Properties().durability(durability)
                )
        );
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
