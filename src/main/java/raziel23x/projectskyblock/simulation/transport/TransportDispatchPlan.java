package raziel23x.projectskyblock.simulation.transport;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Immutable per-channel dispatch plan with exact native-unit accounting. */
public final class TransportDispatchPlan {
    private final TransportChannelId channelId;
    private final long topologyRevision;
    private final long fairnessSequence;
    private final long nextFairnessSequence;
    private final int startIndex;
    private final List<TransportDispatchDecision> decisions;
    private final long requestedUnits;
    private final long plannedUnits;
    private final long deferredUnits;
    private final long unroutableUnits;

    public TransportDispatchPlan(
            TransportChannelId channelId,
            long topologyRevision,
            long fairnessSequence,
            long nextFairnessSequence,
            int startIndex,
            List<TransportDispatchDecision> decisions) {
        this.channelId = Objects.requireNonNull(channelId, "channelId");
        if (topologyRevision < 0L) {
            throw new IllegalArgumentException("topology revision must be non-negative");
        }
        if (fairnessSequence < 0L || nextFairnessSequence < fairnessSequence) {
            throw new IllegalArgumentException("fairness sequence must be monotonic and non-negative");
        }
        this.topologyRevision = topologyRevision;
        this.fairnessSequence = fairnessSequence;
        this.nextFairnessSequence = nextFairnessSequence;
        this.decisions = List.copyOf(decisions);
        if (this.decisions.isEmpty()) {
            if (startIndex != 0) {
                throw new IllegalArgumentException("empty dispatch plan must start at index zero");
            }
        } else if (startIndex < 0 || startIndex >= this.decisions.size()) {
            throw new IllegalArgumentException("dispatch start index is outside the request set");
        }
        this.startIndex = startIndex;

        Set<TransportDispatchRequestId> requestIds = new HashSet<>();
        long requested = 0L;
        long planned = 0L;
        long deferred = 0L;
        long unroutable = 0L;
        for (TransportDispatchDecision decision : this.decisions) {
            if (!requestIds.add(decision.request().id())) {
                throw new IllegalArgumentException(
                        "dispatch plan contains duplicate request id: " + decision.request().id());
            }
            decision.route().ifPresent(route -> {
                if (!channelId.equals(route.channelId())) {
                    throw new IllegalArgumentException(
                            "dispatch route channel does not match plan channel");
                }
            });
            requested = Math.addExact(requested, decision.request().requestedUnits());
            planned = Math.addExact(planned, decision.plannedUnits());
            deferred = Math.addExact(deferred, decision.deferredUnits());
            unroutable = Math.addExact(unroutable, decision.unroutableUnits());
        }
        if (Math.addExact(Math.addExact(planned, deferred), unroutable) != requested) {
            throw new IllegalArgumentException("dispatch plan accounting is inconsistent");
        }
        requestedUnits = requested;
        plannedUnits = planned;
        deferredUnits = deferred;
        unroutableUnits = unroutable;
    }

    public TransportChannelId channelId() {
        return channelId;
    }

    public long topologyRevision() {
        return topologyRevision;
    }

    public long fairnessSequence() {
        return fairnessSequence;
    }

    public long nextFairnessSequence() {
        return nextFairnessSequence;
    }

    public int startIndex() {
        return startIndex;
    }

    public List<TransportDispatchDecision> decisions() {
        return decisions;
    }

    public long requestedUnits() {
        return requestedUnits;
    }

    public long plannedUnits() {
        return plannedUnits;
    }

    public long deferredUnits() {
        return deferredUnits;
    }

    public long unroutableUnits() {
        return unroutableUnits;
    }

    public boolean madeProgress() {
        return plannedUnits > 0L;
    }

    public boolean hasDeferredWork() {
        return deferredUnits > 0L;
    }
}
