package raziel23x.projectskyblock.simulation.transport;

import java.util.Objects;
import java.util.OptionalLong;

/** Immutable undirected physical connection with independent typed-channel throughput. */
public record TransportConnection(
        TransportNodeId first,
        TransportNodeId second,
        TransportProfile profile) implements Comparable<TransportConnection> {

    public TransportConnection {
        Objects.requireNonNull(first, "first");
        Objects.requireNonNull(second, "second");
        Objects.requireNonNull(profile, "profile");
        if (first.equals(second)) {
            throw new IllegalArgumentException("transport connection endpoints must be different");
        }
        if (first.compareTo(second) > 0) {
            TransportNodeId temporary = first;
            first = second;
            second = temporary;
        }
    }

    public boolean contains(TransportNodeId nodeId) {
        return first.equals(nodeId) || second.equals(nodeId);
    }

    public TransportNodeId opposite(TransportNodeId nodeId) {
        if (first.equals(nodeId)) {
            return second;
        }
        if (second.equals(nodeId)) {
            return first;
        }
        throw new IllegalArgumentException("node is not part of this connection: " + nodeId);
    }

    public boolean joins(TransportNodeId left, TransportNodeId right) {
        return (first.equals(left) && second.equals(right))
                || (first.equals(right) && second.equals(left));
    }

    public boolean supports(TransportChannelId channelId) {
        return profile.supports(channelId);
    }

    public OptionalLong maximumUnitsPerStep(TransportChannelId channelId) {
        return profile.maximumUnitsPerStep(channelId);
    }

    @Override
    public int compareTo(TransportConnection other) {
        int firstComparison = first.compareTo(other.first);
        if (firstComparison != 0) {
            return firstComparison;
        }
        int secondComparison = second.compareTo(other.second);
        if (secondComparison != 0) {
            return secondComparison;
        }
        return profile.compareTo(other.profile);
    }
}
