package raziel23x.projectskyblock.simulation.core;

/** Boundary at which one isolated scheduler execution failed. */
public enum SimulationFailureStage {
    CONTEXT_CREATION,
    PARTICIPANT_EXECUTION,
    RESULT_APPLICATION,
    EXECUTION_OBSERVER,
    PLATFORM_INTEGRATION
}
