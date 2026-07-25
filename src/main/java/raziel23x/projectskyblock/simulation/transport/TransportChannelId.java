package raziel23x.projectskyblock.simulation.transport;

/** Stable identifier for one transport resource type and its native unit. */
public record TransportChannelId(String value) implements Comparable<TransportChannelId> {
    public TransportChannelId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("transport channel id must not be blank");
        }
        if (!value.equals(value.trim())) {
            throw new IllegalArgumentException("transport channel id must not contain outer whitespace");
        }
    }

    @Override
    public int compareTo(TransportChannelId other) {
        return value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
