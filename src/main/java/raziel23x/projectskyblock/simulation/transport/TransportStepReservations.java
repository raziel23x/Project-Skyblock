package raziel23x.projectskyblock.simulation.transport;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

/** Per-step atomic shared-edge reservations for one typed transport channel. */
public final class TransportStepReservations {
    private final TransportTopology topology;
    private final TransportChannelId channelId;
    private final long topologyRevision;
    private final NavigableMap<TransportConnection, Long> initialCapacity = new TreeMap<>();
    private final NavigableMap<TransportConnection, Long> remainingCapacity = new TreeMap<>();

    public TransportStepReservations(
            TransportTopology topology,
            TransportChannelId channelId) {
        this.topology = Objects.requireNonNull(topology, "topology");
        this.channelId = Objects.requireNonNull(channelId, "channelId");
        topologyRevision = topology.revision();
        for (TransportConnection connection : topology.connections()) {
            connection.maximumUnitsPerStep(channelId).ifPresent(capacity -> {
                initialCapacity.put(connection, capacity);
                remainingCapacity.put(connection, capacity);
            });
        }
    }

    public TransportChannelId channelId() {
        return channelId;
    }

    public long topologyRevision() {
        return topologyRevision;
    }

    public Map<TransportConnection, Long> remainingCapacities() {
        ensureCurrentTopology();
        return Collections.unmodifiableMap(new TreeMap<>(remainingCapacity));
    }

    public long remainingCapacity(TransportConnection connection) {
        ensureCurrentTopology();
        Objects.requireNonNull(connection, "connection");
        Long remaining = remainingCapacity.get(connection);
        if (remaining == null) {
            throw new IllegalArgumentException(
                    "connection is not reservable for channel " + channelId + ": " + connection);
        }
        return remaining;
    }

    public long reservedCapacity(TransportConnection connection) {
        ensureCurrentTopology();
        Objects.requireNonNull(connection, "connection");
        Long initial = initialCapacity.get(connection);
        if (initial == null) {
            throw new IllegalArgumentException(
                    "connection is not reservable for channel " + channelId + ": " + connection);
        }
        return initial - remainingCapacity.get(connection);
    }

    public long maximumReservable(TransportRoute route) {
        ensureCurrentTopology();
        validateRoute(route);
        long maximum = Long.MAX_VALUE;
        for (TransportConnection connection : route.connections()) {
            maximum = Math.min(maximum, remainingCapacity.get(connection));
        }
        return route.connections().isEmpty() ? 0L : maximum;
    }

    /** Reserves up to the requested amount across every route edge as one mutation. */
    public long reserve(TransportRoute route, long requestedUnits) {
        if (requestedUnits < 0L) {
            throw new IllegalArgumentException("requested transport units must be non-negative");
        }
        ensureCurrentTopology();
        validateRoute(route);
        long reserved = Math.min(requestedUnits, maximumReservable(route));
        if (reserved == 0L) {
            return 0L;
        }
        for (TransportConnection connection : route.connections()) {
            long remaining = remainingCapacity.get(connection);
            remainingCapacity.put(connection, Math.subtractExact(remaining, reserved));
        }
        return reserved;
    }

    private void ensureCurrentTopology() {
        if (topology.revision() != topologyRevision) {
            throw new IllegalStateException(
                    "transport topology changed after step reservations were created");
        }
    }

    private void validateRoute(TransportRoute route) {
        Objects.requireNonNull(route, "route");
        if (!channelId.equals(route.channelId())) {
            throw new IllegalArgumentException(
                    "route channel does not match reservation channel");
        }
        for (TransportConnection connection : route.connections()) {
            if (!remainingCapacity.containsKey(connection)) {
                throw new IllegalArgumentException(
                        "route contains a connection outside the reservation topology");
            }
        }
    }
}
