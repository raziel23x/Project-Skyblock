package raziel23x.projectskyblock.simulation.energy;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Event-driven authoritative topology for one or more energy-network components.
 *
 * <p>The Minecraft adapter mutates this topology only when blocks load, unload,
 * connect, or disconnect. Route discovery is deterministic because node and
 * connection traversal is sorted by stable node identifier.</p>
 */
public final class EnergyNetworkTopology {
    private final NavigableMap<EnergyNetworkNodeId, NavigableSet<EnergyNetworkConnection>> adjacency
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

    public boolean containsNode(EnergyNetworkNodeId nodeId) {
        return adjacency.containsKey(requireNodeId(nodeId));
    }

    public boolean addNode(EnergyNetworkNodeId nodeId) {
        requireNodeId(nodeId);
        if (adjacency.putIfAbsent(nodeId, new TreeSet<>()) != null) {
            return false;
        }
        revision++;
        return true;
    }

    public boolean removeNode(EnergyNetworkNodeId nodeId) {
        NavigableSet<EnergyNetworkConnection> removed = adjacency.remove(requireNodeId(nodeId));
        if (removed == null) {
            return false;
        }
        for (EnergyNetworkConnection connection : List.copyOf(removed)) {
            EnergyNetworkNodeId opposite = connection.opposite(nodeId);
            NavigableSet<EnergyNetworkConnection> oppositeConnections = adjacency.get(opposite);
            if (oppositeConnections != null) {
                oppositeConnections.remove(connection);
            }
        }
        revision++;
        return true;
    }

    public boolean connect(EnergyNetworkConnection connection) {
        if (connection == null) {
            throw new NullPointerException("connection");
        }
        NavigableSet<EnergyNetworkConnection> firstConnections = adjacency.get(connection.first());
        NavigableSet<EnergyNetworkConnection> secondConnections = adjacency.get(connection.second());
        if (firstConnections == null || secondConnections == null) {
            throw new IllegalArgumentException("both connection endpoints must already exist");
        }
        EnergyNetworkConnection existing = connectionBetween(firstConnections, connection.first(), connection.second());
        if (existing != null) {
            if (existing.equals(connection)) {
                return false;
            }
            throw new IllegalArgumentException(
                    "connection already exists with different transfer properties: "
                            + connection.first() + " <-> " + connection.second());
        }
        boolean firstAdded = firstConnections.add(connection);
        boolean secondAdded = secondConnections.add(connection);
        if (!firstAdded || !secondAdded) {
            firstConnections.remove(connection);
            secondConnections.remove(connection);
            throw new IllegalStateException("failed to publish connection atomically: " + connection);
        }
        revision++;
        return true;
    }

    public boolean disconnect(EnergyNetworkNodeId first, EnergyNetworkNodeId second) {
        requireNodeId(first);
        requireNodeId(second);
        if (first.equals(second)) {
            return false;
        }
        NavigableSet<EnergyNetworkConnection> firstConnections = adjacency.get(first);
        NavigableSet<EnergyNetworkConnection> secondConnections = adjacency.get(second);
        if (firstConnections == null || secondConnections == null) {
            return false;
        }
        EnergyNetworkConnection found = connectionBetween(firstConnections, first, second);
        if (found == null) {
            return false;
        }
        firstConnections.remove(found);
        secondConnections.remove(found);
        revision++;
        return true;
    }

    public Set<EnergyNetworkNodeId> nodes() {
        return Collections.unmodifiableSet(new TreeSet<>(adjacency.navigableKeySet()));
    }

    public Set<EnergyNetworkConnection> connections() {
        NavigableSet<EnergyNetworkConnection> connections = new TreeSet<>();
        for (NavigableSet<EnergyNetworkConnection> nodeConnections : adjacency.values()) {
            connections.addAll(nodeConnections);
        }
        return Collections.unmodifiableSet(connections);
    }

    public Set<EnergyNetworkNodeId> neighbors(EnergyNetworkNodeId nodeId) {
        NavigableSet<EnergyNetworkConnection> nodeConnections = adjacency.get(requireNodeId(nodeId));
        if (nodeConnections == null) {
            return Set.of();
        }
        NavigableSet<EnergyNetworkNodeId> neighbors = new TreeSet<>();
        for (EnergyNetworkConnection connection : nodeConnections) {
            neighbors.add(connection.opposite(nodeId));
        }
        return Collections.unmodifiableSet(neighbors);
    }

    public List<Set<EnergyNetworkNodeId>> connectedComponents() {
        List<Set<EnergyNetworkNodeId>> components = new ArrayList<>();
        Set<EnergyNetworkNodeId> visited = new TreeSet<>();
        for (EnergyNetworkNodeId start : adjacency.navigableKeySet()) {
            if (!visited.add(start)) {
                continue;
            }
            LinkedHashSet<EnergyNetworkNodeId> component = new LinkedHashSet<>();
            ArrayDeque<EnergyNetworkNodeId> queue = new ArrayDeque<>();
            queue.add(start);
            while (!queue.isEmpty()) {
                EnergyNetworkNodeId current = queue.removeFirst();
                component.add(current);
                for (EnergyNetworkNodeId neighbor : neighbors(current)) {
                    if (visited.add(neighbor)) {
                        queue.addLast(neighbor);
                    }
                }
            }
            components.add(Collections.unmodifiableSet(component));
        }
        return List.copyOf(components);
    }

    /** Returns the deterministic minimum-hop route, using node-id order to break ties. */
    public Optional<EnergyRoute> findRoute(
            EnergyNetworkNodeId source,
            EnergyNetworkNodeId target) {
        requireNodeId(source);
        requireNodeId(target);
        if (!adjacency.containsKey(source) || !adjacency.containsKey(target)) {
            return Optional.empty();
        }
        if (source.equals(target)) {
            return Optional.of(new EnergyRoute(List.of(source), List.of()));
        }

        ArrayDeque<EnergyNetworkNodeId> queue = new ArrayDeque<>();
        Map<EnergyNetworkNodeId, EnergyNetworkNodeId> previousNode = new HashMap<>();
        Map<EnergyNetworkNodeId, EnergyNetworkConnection> previousConnection = new HashMap<>();
        Set<EnergyNetworkNodeId> visited = new TreeSet<>();
        visited.add(source);
        queue.add(source);

        while (!queue.isEmpty()) {
            EnergyNetworkNodeId current = queue.removeFirst();
            for (EnergyNetworkConnection connection : adjacency.get(current)) {
                EnergyNetworkNodeId neighbor = connection.opposite(current);
                if (!visited.add(neighbor)) {
                    continue;
                }
                previousNode.put(neighbor, current);
                previousConnection.put(neighbor, connection);
                if (neighbor.equals(target)) {
                    return Optional.of(buildRoute(
                            source, target, previousNode, previousConnection));
                }
                queue.addLast(neighbor);
            }
        }
        return Optional.empty();
    }

    private static EnergyRoute buildRoute(
            EnergyNetworkNodeId source,
            EnergyNetworkNodeId target,
            Map<EnergyNetworkNodeId, EnergyNetworkNodeId> previousNode,
            Map<EnergyNetworkNodeId, EnergyNetworkConnection> previousConnection) {
        List<EnergyNetworkNodeId> reversedNodes = new ArrayList<>();
        List<EnergyNetworkConnection> reversedConnections = new ArrayList<>();
        EnergyNetworkNodeId current = target;
        reversedNodes.add(current);
        while (!current.equals(source)) {
            reversedConnections.add(previousConnection.get(current));
            current = previousNode.get(current);
            reversedNodes.add(current);
        }
        Collections.reverse(reversedNodes);
        Collections.reverse(reversedConnections);
        return new EnergyRoute(reversedNodes, reversedConnections);
    }

    private static EnergyNetworkConnection connectionBetween(
            Iterable<EnergyNetworkConnection> connections,
            EnergyNetworkNodeId first,
            EnergyNetworkNodeId second) {
        for (EnergyNetworkConnection connection : connections) {
            if (connection.joins(first, second)) {
                return connection;
            }
        }
        return null;
    }

    private static EnergyNetworkNodeId requireNodeId(EnergyNetworkNodeId nodeId) {
        if (nodeId == null) {
            throw new NullPointerException("nodeId");
        }
        return nodeId;
    }
}
