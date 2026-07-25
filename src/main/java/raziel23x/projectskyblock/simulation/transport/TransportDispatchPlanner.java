package raziel23x.projectskyblock.simulation.transport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** Stateless deterministic planner for shared-edge typed-channel contention. */
public final class TransportDispatchPlanner {
    private TransportDispatchPlanner() {
    }

    /**
     * Plans one bounded channel step without mutating endpoints.
     *
     * <p>Requests are sorted by stable id, then rotated by the fairness sequence. A request may
     * consume the remaining capacity on its route, while the next step rotates first access so a
     * stable lexical id cannot permanently starve later requests.</p>
     */
    public static TransportDispatchPlan plan(
            TransportTopology topology,
            TransportChannelId channelId,
            Collection<TransportDispatchRequest> requests,
            long fairnessSequence) {
        Objects.requireNonNull(topology, "topology");
        Objects.requireNonNull(channelId, "channelId");
        Objects.requireNonNull(requests, "requests");
        if (fairnessSequence < 0L) {
            throw new IllegalArgumentException("fairness sequence must be non-negative");
        }

        List<TransportDispatchRequest> stableRequests = new ArrayList<>(requests.size());
        Set<TransportDispatchRequestId> requestIds = new HashSet<>();
        for (TransportDispatchRequest request : requests) {
            Objects.requireNonNull(request, "request");
            if (!requestIds.add(request.id())) {
                throw new IllegalArgumentException(
                        "duplicate transport dispatch request id: " + request.id());
            }
            stableRequests.add(request);
        }
        stableRequests.sort(Comparator.comparing(TransportDispatchRequest::id));

        if (stableRequests.isEmpty()) {
            return new TransportDispatchPlan(
                    channelId,
                    topology.revision(),
                    fairnessSequence,
                    fairnessSequence,
                    0,
                    List.of());
        }

        int startIndex = (int) (fairnessSequence % stableRequests.size());
        List<TransportDispatchRequest> executionOrder = rotate(stableRequests, startIndex);
        TransportStepReservations reservations = new TransportStepReservations(topology, channelId);
        List<TransportDispatchDecision> decisions = new ArrayList<>(executionOrder.size());

        for (TransportDispatchRequest request : executionOrder) {
            decisions.add(planRequest(topology, channelId, reservations, request));
        }

        return new TransportDispatchPlan(
                channelId,
                topology.revision(),
                fairnessSequence,
                Math.addExact(fairnessSequence, 1L),
                startIndex,
                decisions);
    }

    private static TransportDispatchDecision planRequest(
            TransportTopology topology,
            TransportChannelId channelId,
            TransportStepReservations reservations,
            TransportDispatchRequest request) {
        if (request.requestedUnits() == 0L) {
            return new TransportDispatchDecision(
                    request,
                    Optional.empty(),
                    TransportDispatchStatus.ZERO_REQUEST,
                    0L,
                    0L,
                    0L);
        }

        Optional<TransportRoute> routeResult = topology.findRoute(
                request.source(), request.target(), channelId);
        if (routeResult.isEmpty()) {
            return new TransportDispatchDecision(
                    request,
                    Optional.empty(),
                    TransportDispatchStatus.UNROUTABLE,
                    0L,
                    0L,
                    request.requestedUnits());
        }

        TransportRoute route = routeResult.get();
        long planned = reservations.reserve(route, request.requestedUnits());
        long deferred = request.requestedUnits() - planned;
        TransportDispatchStatus status;
        if (planned == request.requestedUnits()) {
            status = TransportDispatchStatus.PLANNED;
        } else if (planned > 0L) {
            status = TransportDispatchStatus.PARTIAL;
        } else {
            status = TransportDispatchStatus.CAPACITY_DEFERRED;
        }
        return new TransportDispatchDecision(
                request,
                Optional.of(route),
                status,
                planned,
                deferred,
                0L);
    }

    private static List<TransportDispatchRequest> rotate(
            List<TransportDispatchRequest> stableRequests,
            int startIndex) {
        List<TransportDispatchRequest> rotated = new ArrayList<>(stableRequests.size());
        rotated.addAll(stableRequests.subList(startIndex, stableRequests.size()));
        rotated.addAll(stableRequests.subList(0, startIndex));
        return rotated;
    }
}
