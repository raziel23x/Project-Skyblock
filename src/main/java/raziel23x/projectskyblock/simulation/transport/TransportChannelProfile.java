package raziel23x.projectskyblock.simulation.transport;

import java.util.Objects;

/** Native-unit throughput for one channel in one transport profile. */
public record TransportChannelProfile(
        TransportChannelId channelId,
        long maximumUnitsPerStep) implements Comparable<TransportChannelProfile> {

    public TransportChannelProfile {
        Objects.requireNonNull(channelId, "channelId");
        if (maximumUnitsPerStep <= 0L) {
            throw new IllegalArgumentException("channel throughput must be positive");
        }
    }

    @Override
    public int compareTo(TransportChannelProfile other) {
        int channelComparison = channelId.compareTo(other.channelId);
        return channelComparison != 0
                ? channelComparison
                : Long.compare(maximumUnitsPerStep, other.maximumUnitsPerStep);
    }
}
