package raziel23x.projectskyblock.simulation.transport;

import java.util.Objects;
import java.util.Optional;

/** Immutable request-level route and reservation decision. */
public record TransportDispatchDecision(
        TransportDispatchRequest request,
        Optional<TransportRoute> route,
        TransportDispatchStatus status,
        long plannedUnits,
        long deferredUnits,
        long unroutableUnits) {

    public TransportDispatchDecision {
        Objects.requireNonNull(request, "request");
        route = Objects.requireNonNull(route, "route");
        Objects.requireNonNull(status, "status");
        if (plannedUnits < 0L || deferredUnits < 0L || unroutableUnits < 0L) {
            throw new IllegalArgumentException("transport decision units must be non-negative");
        }
        long accounted = Math.addExact(
                Math.addExact(plannedUnits, deferredUnits),
                unroutableUnits);
        if (accounted != request.requestedUnits()) {
            throw new IllegalArgumentException(
                    "transport decision must account for every requested unit");
        }
        validateStatus(request, route, status, plannedUnits, deferredUnits, unroutableUnits);
    }

    private static void validateStatus(
            TransportDispatchRequest request,
            Optional<TransportRoute> route,
            TransportDispatchStatus status,
            long plannedUnits,
            long deferredUnits,
            long unroutableUnits) {
        switch (status) {
            case ZERO_REQUEST -> {
                if (request.requestedUnits() != 0L
                        || route.isPresent()
                        || plannedUnits != 0L
                        || deferredUnits != 0L
                        || unroutableUnits != 0L) {
                    throw new IllegalArgumentException("invalid zero-request transport decision");
                }
            }
            case PLANNED -> {
                if (route.isEmpty()
                        || request.requestedUnits() == 0L
                        || plannedUnits != request.requestedUnits()
                        || deferredUnits != 0L
                        || unroutableUnits != 0L) {
                    throw new IllegalArgumentException("invalid fully planned transport decision");
                }
            }
            case PARTIAL -> {
                if (route.isEmpty()
                        || plannedUnits <= 0L
                        || deferredUnits <= 0L
                        || unroutableUnits != 0L) {
                    throw new IllegalArgumentException("invalid partial transport decision");
                }
            }
            case CAPACITY_DEFERRED -> {
                if (route.isEmpty()
                        || request.requestedUnits() == 0L
                        || plannedUnits != 0L
                        || deferredUnits != request.requestedUnits()
                        || unroutableUnits != 0L) {
                    throw new IllegalArgumentException("invalid capacity-deferred transport decision");
                }
            }
            case UNROUTABLE -> {
                if (route.isPresent()
                        || request.requestedUnits() == 0L
                        || plannedUnits != 0L
                        || deferredUnits != 0L
                        || unroutableUnits != request.requestedUnits()) {
                    throw new IllegalArgumentException("invalid unroutable transport decision");
                }
            }
        }
    }
}
