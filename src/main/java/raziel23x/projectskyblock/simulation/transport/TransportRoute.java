package raziel23x.projectskyblock.simulation.transport;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Deterministic simple route for one typed transport channel. */
public record TransportRoute(
        TransportChannelId channelId,
        List<TransportNodeId> nodes,
        List<TransportConnection> connections) {

    public TransportRoute {
        Objects.requireNonNull(channelId, "channelId");
        nodes = List.copyOf(nodes);
        connections = List.copyOf(connections);
        if (nodes.isEmpty()) {
            throw new IllegalArgumentException("transport route must contain at least one node");
        }
        if (connections.size() != nodes.size() - 1) {
            throw new IllegalArgumentException(
                    "transport route connection count must be one less than node count");
        }
        Set<TransportNodeId> uniqueNodes = new HashSet<>(nodes);
        if (uniqueNodes.size() != nodes.size()) {
            throw new IllegalArgumentException("transport route must not contain a cycle");
        }
        for (int index = 0; index < connections.size(); index++) {
            TransportConnection connection = connections.get(index);
            TransportNodeId current = nodes.get(index);
            TransportNodeId next = nodes.get(index + 1);
            if (!connection.joins(current, next)) {
                throw new IllegalArgumentException(
                        "transport route connection does not join adjacent nodes");
            }
            if (!connection.supports(channelId)) {
                throw new IllegalArgumentException(
                        "transport route connection does not support channel: " + channelId);
            }
        }
    }

    public TransportNodeId source() {
        return nodes.getFirst();
    }

    public TransportNodeId target() {
        return nodes.getLast();
    }

    public int hopCount() {
        return connections.size();
    }

    public long maximumUnitsPerStep() {
        long limit = Long.MAX_VALUE;
        for (TransportConnection connection : connections) {
            limit = Math.min(limit, connection.maximumUnitsPerStep(channelId).orElseThrow());
        }
        return connections.isEmpty() ? 0L : limit;
    }
}
