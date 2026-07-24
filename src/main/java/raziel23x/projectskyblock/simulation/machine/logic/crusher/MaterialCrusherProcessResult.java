package raziel23x.projectskyblock.simulation.machine.logic.crusher;

import java.util.Objects;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;

/** One resolved crusher completion result expressed only in simulation item values. */
public record MaterialCrusherProcessResult(
        SimulationItemStack primary,
        SimulationItemStack byproduct) {

    public static final MaterialCrusherProcessResult EMPTY = new MaterialCrusherProcessResult(
            SimulationItemStack.empty(),
            SimulationItemStack.empty());

    public MaterialCrusherProcessResult {
        Objects.requireNonNull(primary, "primary");
        Objects.requireNonNull(byproduct, "byproduct");
        if (primary.isEmpty() && !byproduct.isEmpty()) {
            throw new IllegalArgumentException("byproduct cannot exist without a primary result");
        }
    }

    public boolean isEmpty() {
        return primary.isEmpty();
    }
}
