package raziel23x.projectskyblock.simulation.transport;

import java.util.Objects;

/** Immutable source-to-target demand expressed in one channel's native units. */
public record TransportDispatchRequest(
        TransportDispatchRequestId id,
        TransportNodeId source,
        TransportNodeId target,
        long requestedUnits) {

    public TransportDispatchRequest {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(target, "target");
        if (source.equals(target)) {
            throw new IllegalArgumentException("transport request source and target must differ");
        }
        if (requestedUnits < 0L) {
            throw new IllegalArgumentException("requested transport units must be non-negative");
        }
    }
}
