package raziel23x.projectskyblock.simulation.machine.logic.crusher;

import java.util.Objects;

/** Current recipe identity plus its maximum possible output footprint. */
public record MaterialCrusherRecipeMatch(
        String recipeId,
        MaterialCrusherProcessResult maximumResult) {

    public MaterialCrusherRecipeMatch {
        if (recipeId == null || recipeId.isBlank()) {
            throw new IllegalArgumentException("recipe id must not be blank");
        }
        Objects.requireNonNull(maximumResult, "maximumResult");
        if (maximumResult.isEmpty()) {
            throw new IllegalArgumentException("recipe match must have a primary result");
        }
    }
}
