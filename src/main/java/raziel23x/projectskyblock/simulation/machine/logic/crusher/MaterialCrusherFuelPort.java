package raziel23x.projectskyblock.simulation.machine.logic.crusher;

import java.util.Optional;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;

/** Narrow host boundary for resolving furnace fuel semantics. */
@FunctionalInterface
public interface MaterialCrusherFuelPort {
    Optional<MaterialCrusherFuel> resolveFuel(SimulationItemStack fuel);
}
