package raziel23x.projectskyblock.simulation.energy;

/** Immutable undirected connection between two energy-network nodes. */
public record EnergyNetworkConnection(
        EnergyNetworkNodeId first,
        EnergyNetworkNodeId second,
        long maximumTransferPerTick,
        int lossPartsPerMillion) implements Comparable<EnergyNetworkConnection> {

    public EnergyNetworkConnection {
        if (first == null || second == null) {
            throw new NullPointerException("connection endpoints must not be null");
        }
        if (first.equals(second)) {
            throw new IllegalArgumentException("connection endpoints must be different");
        }
        if (maximumTransferPerTick < 0) {
            throw new IllegalArgumentException("maximum transfer must be non-negative");
        }
        if (lossPartsPerMillion < 0
                || lossPartsPerMillion > EnergyConstants.PARTS_PER_MILLION) {
            throw new IllegalArgumentException("loss must be between 0 and 1,000,000 ppm");
        }
        if (first.compareTo(second) > 0) {
            EnergyNetworkNodeId temporary = first;
            first = second;
            second = temporary;
        }
    }

    public boolean contains(EnergyNetworkNodeId nodeId) {
        return first.equals(nodeId) || second.equals(nodeId);
    }

    public EnergyNetworkNodeId opposite(EnergyNetworkNodeId nodeId) {
        if (first.equals(nodeId)) {
            return second;
        }
        if (second.equals(nodeId)) {
            return first;
        }
        throw new IllegalArgumentException("node is not part of this connection: " + nodeId);
    }

    @Override
    public int compareTo(EnergyNetworkConnection other) {
        int firstComparison = first.compareTo(other.first);
        return firstComparison != 0 ? firstComparison : second.compareTo(other.second);
    }
}
