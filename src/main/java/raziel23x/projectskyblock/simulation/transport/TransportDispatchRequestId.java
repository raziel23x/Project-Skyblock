package raziel23x.projectskyblock.simulation.transport;

/** Stable identifier used to make dispatch order deterministic and fair across steps. */
public record TransportDispatchRequestId(String value)
        implements Comparable<TransportDispatchRequestId> {

    public TransportDispatchRequestId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("transport dispatch request id must not be blank");
        }
        if (!value.equals(value.trim())) {
            throw new IllegalArgumentException(
                    "transport dispatch request id must not contain outer whitespace");
        }
    }

    @Override
    public int compareTo(TransportDispatchRequestId other) {
        return value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
