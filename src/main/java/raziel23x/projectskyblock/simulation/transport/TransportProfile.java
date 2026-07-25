package raziel23x.projectskyblock.simulation.transport;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.TreeMap;

/**
 * Immutable per-channel throughput definition for one physical transport connection.
 *
 * <p>Profile identifiers are gameplay/data names, not engine tier semantics. Energy, items,
 * fluids, gases, and later channels retain independent native-unit limits.</p>
 */
public final class TransportProfile implements Comparable<TransportProfile> {
    private final TransportProfileId id;
    private final NavigableMap<TransportChannelId, TransportChannelProfile> channels;

    public TransportProfile(
            TransportProfileId id,
            Collection<TransportChannelProfile> channels) {
        this.id = Objects.requireNonNull(id, "id");
        Objects.requireNonNull(channels, "channels");
        if (channels.isEmpty()) {
            throw new IllegalArgumentException("transport profile must define at least one channel");
        }

        NavigableMap<TransportChannelId, TransportChannelProfile> candidate = new TreeMap<>();
        for (TransportChannelProfile channel : channels) {
            Objects.requireNonNull(channel, "channel");
            if (candidate.putIfAbsent(channel.channelId(), channel) != null) {
                throw new IllegalArgumentException(
                        "transport profile contains duplicate channel: " + channel.channelId());
            }
        }
        this.channels = Collections.unmodifiableNavigableMap(candidate);
    }

    public TransportProfileId id() {
        return id;
    }

    public Map<TransportChannelId, TransportChannelProfile> channels() {
        return channels;
    }

    public boolean supports(TransportChannelId channelId) {
        return channels.containsKey(Objects.requireNonNull(channelId, "channelId"));
    }

    public OptionalLong maximumUnitsPerStep(TransportChannelId channelId) {
        TransportChannelProfile channel = channels.get(Objects.requireNonNull(channelId, "channelId"));
        return channel == null
                ? OptionalLong.empty()
                : OptionalLong.of(channel.maximumUnitsPerStep());
    }

    @Override
    public int compareTo(TransportProfile other) {
        int idComparison = id.compareTo(other.id);
        if (idComparison != 0) {
            return idComparison;
        }
        var left = channels.entrySet().iterator();
        var right = other.channels.entrySet().iterator();
        while (left.hasNext() && right.hasNext()) {
            Map.Entry<TransportChannelId, TransportChannelProfile> leftEntry = left.next();
            Map.Entry<TransportChannelId, TransportChannelProfile> rightEntry = right.next();
            int channelComparison = leftEntry.getKey().compareTo(rightEntry.getKey());
            if (channelComparison != 0) {
                return channelComparison;
            }
            int profileComparison = leftEntry.getValue().compareTo(rightEntry.getValue());
            if (profileComparison != 0) {
                return profileComparison;
            }
        }
        return Integer.compare(channels.size(), other.channels.size());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TransportProfile profile)) {
            return false;
        }
        return id.equals(profile.id) && channels.equals(profile.channels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, channels);
    }

    @Override
    public String toString() {
        return id + channels.toString();
    }
}
