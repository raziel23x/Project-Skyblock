package raziel23x.projectskyblock.simulation.core;

/** Deferred integration work caused by a backend state change. */
public enum DirtyFlag {
    PERSISTENCE,
    CLIENT_SYNC,
    SCHEDULER,
    NEIGHBOR_NOTIFICATION,
    COMPARATOR,
    VISUAL_STATE
}
