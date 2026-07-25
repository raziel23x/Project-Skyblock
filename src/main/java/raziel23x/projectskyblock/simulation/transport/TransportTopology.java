package raziel23x.projectskyblock.simulation.transport;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Event-driven authoritative physical topology shared by typed transport channels.
 *
 * <p>Topology mutation occurs only on load, unload, connection, and disconnection events. Route
 * discovery filters the physical graph by channel support and uses stable node ordering to break
 * equal-hop ties.</p>
 */
public final class TransportTopology {
    private final NavigableMap<TransportNodeId, NavigableSet<TransportConnection>> adjacency
            = new TreeMap<>();
    private long revision;

    public long revision() {
        return revision;
    }

    public int nodeCount() {
        return adjacency.size();
    }

    public int connectionCount() {
        int endpointReferences = adjacency.values().stream().mapToInt(Set::size).sum();
        return endpointReferences / 2;
    }

    public boolean containsNode(TransportNodeId nodeId) {
        return adjacency.containsKey(requireNodeId(nodeId));
    }

    public boolean addNode(TransportNodeId nodeId) {
        requireNodeId(nodeId);
        if (adjacency.putIfAbsent(nodeId, new TreeSet<>()) != null) {
            return false;
        }
        revision++;
        return true;
    }

    public boolean removeNode(TransportNodeId nodeId) {
        NavigableSet<TransportConnection> removed = adjacency.remove(requireNodeId(nodeId));
        if (removed == null) {
            return false;
        }
        for (TransportConnection connection : List.copyOf(removed)) {
            TransportNodeId opposite = connection.opposite(nodeId);
            NavigableSet<TransportConnection> oppositeConnections = adjacency.get(opposite);
            if (oppositeConnections != null) {
                oppositeConnections.remove(connection);
            }
        }
        revision++;
        return true;
    }

    public boolean connect(TransportConnection connection) {
        Objects.requireNonNull(connection, "connection");
        NavigableSet<TransportConnection> firstConnections = adjacency.get(connection.first());
        NavigableSet<TransportConnection> secondConnections = adjacency.get(connection.second());
        if (firstConnections == null || secondConnections == null) {
            throw new IllegalArgumentException("both transport connection endpoints must already exist");
        }
        TransportConnection existing = connectionBetween(
                firstConnections, connection.first(), connection.second());
        if (existing != null) {
            if (existing.equals(connection)) {
                return false;
            }
            throw new IllegalArgumentException(
                    "transport connection already exists with a different profile: "
                            + connection.first() + " <-> " + connection.second());
        }
        boolean firstAdded = firstConnections.add(connection);
        boolean secondAdded = secondConnections.add(connection);
        if (!firstAdded || !secondAdded) {
            firstConnections.remove(connection);
            secondConnections.remove(connection);
            throw new IllegalStateException(
                    "failed to publish transport connection atomically: " + connection);
        }
        revision++;
        return true;
    }

    public boolean disconnect(TransportNodeId first, TransportNodeId second) {
        requireNodeId(first);
        requireNodeId(second);
        if (first.equals(second)) {
            return false;
        }
        NavigableSet<TransportConnection> firstConnections = adjacency.get(first);
        NavigableSet<TransportConnection> secondConnections = adjacency.get(second);
        if (firstConnections == null || secondConnections == null) {
            return false;
        }
        TransportConnection found = connectionBetween(firstConnections, first, second);
        if (found == null) {
            return false;
        }
        firstConnections.remove(found);
        secondConnections.remove(found);
        revision++;
        return true;
    }

    public Set<TransportNodeId> nodes() {
        return Collections.unmodifiableSet(new TreeSet<>(adjacency.navigableKeySet()));
    }

    public Set<TransportConnection> connections() {
        NavigableSet<TransportConnection> connections = new TreeSet<>();
        for (NavigableSet<TransportConnection> nodeConnections : adjacency.values()) {
            connections.addAll(nodeConnections);
        }
        return Collections.unmodifiableSet(connections);
    }

    public Set<TransportNodeId> neighbors(TransportNodeId nodeId) {
        return neighborsForChannel(nodeId, null);
    }

    public Set<TransportNodeId> neighbors(
            TransportNodeId nodeId,
            TransportChannelId channelId) {
        return neighborsForChannel(nodeId, Objects.requireNonNull(channelId, "channelId"));
    }

    private Set<TransportNodeId> neighborsForChannel(
            TransportNodeId nodeId,
            TransportChannelId channelId) {
        NavigableSet<TransportConnection> nodeConnections = adjacency.get(requireNodeId(nodeId));
        if (nodeConnections == null) {
            return Set.of();
        }
        NavigableSet<TransportNodeId> neighbors = new TreeSet<>();
        for (TransportConnection connection : nodeConnections) {
            if (channelId == null || connection.supports(channelId)) {
                neighbors.add(connection.opposite(nodeId));
            }
        }
        return Collections.unmodifiableSet(neighbors);
    }

    public List<Set<TransportNodeId>> connectedComponents() {
        return connectedComponentsForChannel(null);
    }

    public List<Set<TransportNodeId>> connectedComponents(TransportChannelId channelId) {
        return connectedComponentsForChannel(Objects.requireNonNull(channelId, "channelId"));
    }

    private List<Set<TransportNodeId>> connectedComponentsForChannel(
            TransportChannelId channelId) {
        List<Set<TransportNodeId>> components = new ArrayList<>();
        Set<TransportNodeId> visited = new TreeSet<>();
        for (TransportNodeId start : adjacency.navigableKeySet()) {
            if (!visited.add(start)) {
                continue;
            }
            LinkedHashSet<TransportNodeId> component = new LinkedHashSet<>();
            ArrayDeque<TransportNodeId> queue = new ArrayDeque<>();
            queue.add(start);
            while (!queue.isEmpty()) {
                TransportNodeId current = queue.removeFirst();
                component.add(current);
                for (TransportNodeId neighbor : neighborsForChannel(current, channelId)) {
                    if (visited.add(neighbor)) {
                        queue.addLast(neighbor);
                    }
                }
            }
            components.add(Collections.unmodifiableSet(component));
        }
        return List.copyOf(components);
    }

    /** Returns the deterministic minimum-hop route for one channel. */
    public Optional<TransportRoute> findRoute(
            TransportNodeId source,
            TransportNodeId target,
            TransportChannelId channelId) {
        requireNodeId(source);
        requireNodeId(target);
        Objects.requireNonNull(channelId, "channelId");
        if (!adjacency.containsKey(source) || !adjacency.containsKey(target)) {
            return Optional.empty();
        }
        if (source.equals(target)) {
            return Optional.of(new TransportRoute(channelId, List.of(source), List.of()));
        }

        ArrayDeque<TransportNodeId> queue = new ArrayDeque<>();
        Map<TransportNodeId, TransportNodeId> previousNode = new HashMap<>();
        Map<TransportNodeId, TransportConnection> previousConnection = new HashMap<>();
        Set<TransportNodeId> visited = new TreeSet<>();
        visited.add(source);
        queue.add(source);

        while (!queue.isEmpty()) {
            TransportNodeId current = queue.removeFirst();
            for (TransportConnection connection : adjacency.get(current)) {
                if (!connection.supports(channelId)) {
                    continue;
                }
                TransportNodeId neighbor = connection.opposite(current);
                if (!visited.add(neighbor)) {
                    continue;
                }
                previousNode.put(neighbor, current);
                previousConnection.put(neighbor, connection);
                if (neighbor.equals(target)) {
                    return Optional.of(buildRoute(
                            source,
                            target,
                            channelId,
                            previousNode,
                            previousConnection));
                }
                queue.addLast(neighbor);
            }
        }
        return Optional.empty();
    }

    private static TransportRoute buildRoute(
            TransportNodeId source,
            TransportNodeId target,
            TransportChannelId channelId,
            Map<TransportNodeId, TransportNodeId> previousNode,
            Map<TransportNodeId, TransportConnection> previousConnection) {
        List<TransportNodeId> reversedNodes = new ArrayList<>();
        List<TransportConnection> reversedConnections = new ArrayList<>();
        TransportNodeId current = target;
        reversedNodes.add(current);
        while (!current.equals(source)) {
            reversedConnections.add(previousConnection.get(current));
            current = previousNode.get(current);
            reversedNodes.add(current);
        }
        Collections.reverse(reversedNodes);
        Collections.reverse(reversedConnections);
        return new TransportRoute(channelId, reversedNodes, reversedConnections);
    }

    private static TransportConnection connectionBetween(
            Iterable<TransportConnection> connections,
            TransportNodeId first,
            TransportNodeId second) {
        for (TransportConnection connection : connections) {
            if (connection.joins(first, second)) {
                return connection;
            }
        }
        return null;
    }

    private static TransportNodeId requireNodeId(TransportNodeId nodeId) {
        return Objects.requireNonNull(nodeId, "nodeId");
    }
}
