package raziel23x.projectskyblock.simulation.transport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * One immutable multi-channel network execution result.
 *
 * <p>Native units are never summed across channels. FE, items, fluid volume, gas amount, and later
 * resource units remain separately diagnosable.</p>
 */
public final class TransportNetworkStepResult {
    private final long stateFingerprint;
    private final List<TransportChannelStepResult> channelResults;
    private final boolean madeProgress;
    private final boolean pendingWork;

    public TransportNetworkStepResult(
            long stateFingerprint,
            Collection<TransportChannelStepResult> channelResults) {
        this.stateFingerprint = stateFingerprint;
        Objects.requireNonNull(channelResults, "channelResults");
        List<TransportChannelStepResult> candidate = new ArrayList<>(channelResults.size());
        Set<TransportChannelId> channels = new HashSet<>();
        boolean progress = false;
        boolean pending = false;
        for (TransportChannelStepResult result : channelResults) {
            Objects.requireNonNull(result, "result");
            if (!channels.add(result.channelId())) {
                throw new IllegalArgumentException(
                        "network step contains duplicate channel result: " + result.channelId());
            }
            candidate.add(result);
            progress |= result.madeProgress();
            pending |= result.hasPendingWork();
        }
        candidate.sort(TransportChannelStepResult::compareTo);
        this.channelResults = List.copyOf(candidate);
        madeProgress = progress;
        pendingWork = pending;
    }

    public long stateFingerprint() {
        return stateFingerprint;
    }

    public List<TransportChannelStepResult> channelResults() {
        return channelResults;
    }

    public boolean madeProgress() {
        return madeProgress;
    }

    public boolean hasPendingWork() {
        return pendingWork;
    }

    public boolean isQuiescent() {
        return !madeProgress && !pendingWork;
    }
}
