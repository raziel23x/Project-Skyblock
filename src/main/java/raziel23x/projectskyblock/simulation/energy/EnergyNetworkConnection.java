package raziel23x.projectskyblock.simulation.energy;

/**
 * Immutable undirected connection between two energy-network nodes.
 *
 * <p>The transfer limit applies to one solver operation. Shared-edge reservations and cumulative
 * simulation-step budgets belong to the future network scheduler, not this topology value.</p>
 */
public record EnergyNetworkConnection(
        EnergyNetworkNodeId first,
        EnergyNetworkNodeId second,
        long maximumTransferPerOperation,
        int lossPartsPerMillion) implements Comparable<EnergyNetworkConnection> {

    public EnergyNetworkConnection {
        if (first == null || second == null) {
            throw new NullPointerException("connection endpoints must not be null");
        }
        if (first.equals(second)) {
            throw new IllegalArgumentException("connection endpoints must be different");
        }
        if (maximumTransferPerOperation < 0) {
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

    public boolean joins(EnergyNetworkNodeId left, EnergyNetworkNodeId right) {
        return (first.equals(left) && second.equals(right))
                || (first.equals(right) && second.equals(left));
    }

    @Override
    public int compareTo(EnergyNetworkConnection other) {
        int firstComparison = first.compareTo(other.first);
        if (firstComparison != 0) {
            return firstComparison;
        }
        int secondComparison = second.compareTo(other.second);
        if (secondComparison != 0) {
            return secondComparison;
        }
        int transferComparison = Long.compare(maximumTransferPerOperation, other.maximumTransferPerOperation);
        return transferComparison != 0
                ? transferComparison
                : Integer.compare(lossPartsPerMillion, other.lossPartsPerMillion);
    }
}
