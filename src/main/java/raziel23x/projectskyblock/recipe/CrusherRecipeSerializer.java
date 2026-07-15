package raziel23x.projectskyblock.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class CrusherRecipeSerializer implements RecipeSerializer<CrusherRecipe> {
    @Override
    public MapCodec<CrusherRecipe> codec() {
        return CrusherRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CrusherRecipe> streamCodec() {
        return CrusherRecipe.STREAM_CODEC;
    }
}
