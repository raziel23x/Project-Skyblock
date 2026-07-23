package raziel23x.projectskyblock.simulation.energy;

import java.util.List;

/** Deterministic ordered route through an energy topology. */
public record EnergyRoute(
        List<EnergyNetworkNodeId> nodes,
        List<EnergyNetworkConnection> connections) {

    public EnergyRoute {
        nodes = List.copyOf(nodes);
        connections = List.copyOf(connections);
        if (nodes.isEmpty()) {
            throw new IllegalArgumentException("route must contain at least one node");
        }
        if (connections.size() != nodes.size() - 1) {
            throw new IllegalArgumentException("route connection count must be one less than node count");
        }
        for (int index = 0; index < connections.size(); index++) {
            EnergyNetworkConnection connection = connections.get(index);
            EnergyNetworkNodeId current = nodes.get(index);
            EnergyNetworkNodeId next = nodes.get(index + 1);
            if (!connection.contains(current) || !connection.contains(next)) {
                throw new IllegalArgumentException("route connection does not join adjacent nodes");
            }
        }
    }

    public EnergyNetworkNodeId source() {
        return nodes.getFirst();
    }

    public EnergyNetworkNodeId target() {
        return nodes.getLast();
    }

    public int hopCount() {
        return connections.size();
    }

    public long maximumTransferPerOperation() {
        long limit = Long.MAX_VALUE;
        for (EnergyNetworkConnection connection : connections) {
            limit = Math.min(limit, connection.maximumTransferPerOperation());
        }
        return connections.isEmpty() ? 0 : limit;
    }
}
