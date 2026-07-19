package raziel23x.projectskyblock.simulation.core;

/** Runtime scheduling state for one registered simulation participant. */
public enum SimulationLifecycle {
    SLEEPING,
    READY,
    SCHEDULED,
    ACTIVE,
    BLOCKED,
    INVALID
}
