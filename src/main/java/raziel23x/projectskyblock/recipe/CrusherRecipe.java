package raziel23x.projectskyblock.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import raziel23x.projectskyblock.machine.crusher.CrusherProcessingResult;
import raziel23x.projectskyblock.registry.ModRecipes;

public final class CrusherRecipe implements Recipe<SingleRecipeInput> {
    public static final MapCodec<CrusherRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CrusherRecipe::ingredient),
            ItemStack.STRICT_CODEC.fieldOf("result").forGetter(CrusherRecipe::result),
            Codec.INT.optionalFieldOf("extra_result_count", 0).forGetter(CrusherRecipe::extraResultCount),
            Codec.DOUBLE.optionalFieldOf("extra_result_chance", 0.0D).forGetter(CrusherRecipe::extraResultChance),
            ItemStack.OPTIONAL_CODEC.optionalFieldOf("byproduct", ItemStack.EMPTY).forGetter(CrusherRecipe::byproduct),
            Codec.DOUBLE.optionalFieldOf("byproduct_chance", 0.0D).forGetter(CrusherRecipe::byproductChance)
    ).apply(instance, CrusherRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrusherRecipe> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    private final Ingredient ingredient;
    private final ItemStack result;
    private final int extraResultCount;
    private final double extraResultChance;
    private final ItemStack byproduct;
    private final double byproductChance;

    public CrusherRecipe(
            Ingredient ingredient,
            ItemStack result,
            int extraResultCount,
            double extraResultChance,
            ItemStack byproduct,
            double byproductChance) {
        this.ingredient = ingredient;
        this.result = result;
        this.extraResultCount = Math.max(0, extraResultCount);
        this.extraResultChance = clampChance(extraResultChance);
        this.byproduct = byproduct;
        this.byproductChance = clampChance(byproductChance);
    }

    private static double clampChance(double chance) {
        return Math.max(0.0D, Math.min(1.0D, chance));
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CRUSHING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CRUSHING_TYPE.get();
    }

    public CrusherProcessingResult rollResult(RandomSource random) {
        ItemStack primary = result.copy();
        if (extraResultCount > 0 && random.nextDouble() < extraResultChance) {
            primary.grow(extraResultCount);
        }

        ItemStack rolledByproduct = ItemStack.EMPTY;
        if (!byproduct.isEmpty() && random.nextDouble() < byproductChance) {
            rolledByproduct = byproduct.copy();
        }

        return new CrusherProcessingResult(primary, rolledByproduct);
    }

    public CrusherProcessingResult maximumResult() {
        ItemStack primary = result.copy();
        if (extraResultCount > 0 && extraResultChance > 0.0D) {
            primary.grow(extraResultCount);
        }

        ItemStack maximumByproduct = byproductChance > 0.0D
                ? byproduct.copy()
                : ItemStack.EMPTY;
        return new CrusherProcessingResult(primary, maximumByproduct);
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public ItemStack result() {
        return result;
    }

    public int extraResultCount() {
        return extraResultCount;
    }

    public double extraResultChance() {
        return extraResultChance;
    }

    public ItemStack byproduct() {
        return byproduct;
    }

    public double byproductChance() {
        return byproductChance;
    }
}
