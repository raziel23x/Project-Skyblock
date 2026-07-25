package raziel23x.projectskyblock.simulation.transport;

import java.util.Objects;

/** Exact native-unit accounting for one channel during one network execution. */
public record TransportChannelStepResult(
        TransportChannelId channelId,
        long requestedUnits,
        long committedUnits,
        long capacityDeferredUnits,
        long endpointBlockedUnits,
        long unroutableUnits) implements Comparable<TransportChannelStepResult> {

    public TransportChannelStepResult {
        Objects.requireNonNull(channelId, "channelId");
        if (requestedUnits < 0L
                || committedUnits < 0L
                || capacityDeferredUnits < 0L
                || endpointBlockedUnits < 0L
                || unroutableUnits < 0L) {
            throw new IllegalArgumentException("transport step units must be non-negative");
        }
        long accounted = Math.addExact(
                Math.addExact(committedUnits, capacityDeferredUnits),
                Math.addExact(endpointBlockedUnits, unroutableUnits));
        if (accounted != requestedUnits) {
            throw new IllegalArgumentException(
                    "transport channel result must account for every requested unit");
        }
    }

    public static TransportChannelStepResult fromPlan(TransportDispatchPlan plan) {
        Objects.requireNonNull(plan, "plan");
        return new TransportChannelStepResult(
                plan.channelId(),
                plan.requestedUnits(),
                plan.plannedUnits(),
                plan.deferredUnits(),
                0L,
                plan.unroutableUnits());
    }

    public boolean madeProgress() {
        return committedUnits > 0L;
    }

    public boolean hasPendingWork() {
        return capacityDeferredUnits > 0L || endpointBlockedUnits > 0L;
    }

    @Override
    public int compareTo(TransportChannelStepResult other) {
        return channelId.compareTo(other.channelId);
    }
}
