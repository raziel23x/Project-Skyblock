package raziel23x.projectskyblock.simulation.core;

/** A backend participant that executes only when explicitly woken or scheduled. */
public interface SimulationParticipant<S extends SimulationState> {
    S state();

    SimulationResult execute(SimulationContext context, SimulationBudget budget);
}
