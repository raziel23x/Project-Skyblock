package raziel23x.projectskyblock.simulation.machine.logic.crusher;

import java.util.Objects;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;

/** Host-resolved furnace fuel value and optional container remainder. */
public record MaterialCrusherFuel(
        long baseBurnUnits,
        SimulationItemStack remainder) {

    public MaterialCrusherFuel {
        if (baseBurnUnits <= 0L) {
            throw new IllegalArgumentException("base burn units must be positive");
        }
        Objects.requireNonNull(remainder, "remainder");
    }
}
