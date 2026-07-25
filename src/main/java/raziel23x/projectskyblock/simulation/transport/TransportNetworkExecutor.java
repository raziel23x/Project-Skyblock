package raziel23x.projectskyblock.simulation.transport;

/** Channel-policy boundary invoked by the generic scheduled transport network. */
@FunctionalInterface
public interface TransportNetworkExecutor {
    TransportNetworkStepResult execute(TransportTopology topology, long dispatchSequence);
}
