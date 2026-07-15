package raziel23x.projectskyblock.registry;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import raziel23x.projectskyblock.ProjectSkyblock;

import java.util.EnumMap;
import java.util.List;

public final class ModArmorMaterials {
    private static final DeferredRegister<ArmorMaterial> MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, ProjectSkyblock.MOD_ID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> FLINT = MATERIALS.register(
            "flint",
            () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), defense -> {
                        defense.put(ArmorItem.Type.BOOTS, 1);
                        defense.put(ArmorItem.Type.LEGGINGS, 3);
                        defense.put(ArmorItem.Type.CHESTPLATE, 4);
                        defense.put(ArmorItem.Type.HELMET, 1);
                        defense.put(ArmorItem.Type.BODY, 4);
                    }),
                    10,
                    SoundEvents.ARMOR_EQUIP_GENERIC,
                    () -> Ingredient.of(Items.FLINT),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ProjectSkyblock.MOD_ID, "flint"))),
                    0.0F,
                    0.0F
            )
    );

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> WOODEN = MATERIALS.register(
            "wooden",
            () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), defense -> {
                        defense.put(ArmorItem.Type.BOOTS, 1);
                        defense.put(ArmorItem.Type.LEGGINGS, 2);
                        defense.put(ArmorItem.Type.CHESTPLATE, 3);
                        defense.put(ArmorItem.Type.HELMET, 1);
                        defense.put(ArmorItem.Type.BODY, 3);
                    }),
                    10,
                    SoundEvents.ARMOR_EQUIP_GENERIC,
                    () -> Ingredient.of(net.minecraft.tags.ItemTags.PLANKS),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ProjectSkyblock.MOD_ID, "wooden"))),
                    0.0F,
                    0.0F
            )
    );

    private ModArmorMaterials() {
    }

    public static void register(IEventBus eventBus) {
        MATERIALS.register(eventBus);
    }
}
