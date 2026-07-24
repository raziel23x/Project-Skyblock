package raziel23x.projectskyblock.simulation.machine.logic.crusher;

import java.util.Optional;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;

/** Narrow host boundary for matching and rolling prototype crusher recipes. */
public interface MaterialCrusherRecipePort {
    Optional<MaterialCrusherRecipeMatch> findMatch(SimulationItemStack input);

    Optional<MaterialCrusherProcessResult> rollResult(
            String recipeId,
            SimulationItemStack input);
}
