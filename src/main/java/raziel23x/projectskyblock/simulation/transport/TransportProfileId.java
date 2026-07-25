package raziel23x.projectskyblock.simulation.transport;

/** Stable data-facing identifier for one transport throughput profile. */
public record TransportProfileId(String value) implements Comparable<TransportProfileId> {
    public TransportProfileId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("transport profile id must not be blank");
        }
        if (!value.equals(value.trim())) {
            throw new IllegalArgumentException("transport profile id must not contain outer whitespace");
        }
    }

    @Override
    public int compareTo(TransportProfileId other) {
        return value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
