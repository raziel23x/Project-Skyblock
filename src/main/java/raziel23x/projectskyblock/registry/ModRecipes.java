package raziel23x.projectskyblock.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.recipe.CrusherRecipe;
import raziel23x.projectskyblock.recipe.CrusherRecipeSerializer;

public final class ModRecipes {
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, ProjectSkyblock.MOD_ID);
    private static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, ProjectSkyblock.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrusherRecipe>> CRUSHING_SERIALIZER =
            SERIALIZERS.register("crushing", CrusherRecipeSerializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CrusherRecipe>> CRUSHING_TYPE =
            TYPES.register("crushing", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return ProjectSkyblock.MOD_ID + ":crushing";
                }
            });

    private ModRecipes() {
    }

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
