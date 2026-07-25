package raziel23x.projectskyblock.simulation.transport;

/** Deterministic planning outcome for one typed transport request. */
public enum TransportDispatchStatus {
    ZERO_REQUEST,
    PLANNED,
    PARTIAL,
    CAPACITY_DEFERRED,
    UNROUTABLE
}
