package raziel23x.projectskyblock.machine.crusher;

import java.util.Optional;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import raziel23x.projectskyblock.recipe.CrusherRecipe;
import raziel23x.projectskyblock.registry.ModRecipes;

public final class CrusherProcessing {
    private CrusherProcessing() {
    }

    public static Optional<CrusherRecipe> findRecipe(Level level, ItemStack stack) {
        if (level == null || stack.isEmpty()) {
            return Optional.empty();
        }

        return level.getRecipeManager()
                .getRecipeFor(
                        ModRecipes.CRUSHING_TYPE.get(),
                        new SingleRecipeInput(stack),
                        level
                )
                .map(holder -> holder.value());
    }

    public static boolean isValidInput(Level level, ItemStack stack) {
        return findRecipe(level, stack).isPresent();
    }

    public static CrusherProcessingResult createResult(
            Level level,
            ItemStack input,
            RandomSource random) {
        return findRecipe(level, input)
                .map(recipe -> recipe.rollResult(random))
                .orElse(CrusherProcessingResult.EMPTY);
    }

    public static CrusherProcessingResult maximumResult(
            Level level,
            ItemStack input) {
        return findRecipe(level, input)
                .map(CrusherRecipe::maximumResult)
                .orElse(CrusherProcessingResult.EMPTY);
    }
}
