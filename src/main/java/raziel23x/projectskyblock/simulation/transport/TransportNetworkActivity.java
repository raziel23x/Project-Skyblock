package raziel23x.projectskyblock.simulation.transport;

/** Scheduler-facing state for one typed transport network. */
public enum TransportNetworkActivity {
    ACTIVE,
    CONFIRMING_STALL,
    SLEEPING
}
