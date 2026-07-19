package raziel23x.projectskyblock.simulation.machine;

import raziel23x.projectskyblock.simulation.core.SimulationBudget;
import raziel23x.projectskyblock.simulation.core.SimulationContext;
import raziel23x.projectskyblock.simulation.core.SimulationResult;
import raziel23x.projectskyblock.simulation.core.SimulationState;

/** Machine-specific behavior executed through the common scheduler participant. */
@FunctionalInterface
public interface MachineLogic<S extends SimulationState> {
    SimulationResult execute(
            MachineState<S> state,
            SimulationContext context,
            SimulationBudget budget);
}
