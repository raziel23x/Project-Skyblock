package raziel23x.projectskyblock.simulation.transport;

/** Stable Minecraft-independent identifier for one physical transport node. */
public record TransportNodeId(String value) implements Comparable<TransportNodeId> {
    public TransportNodeId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("transport node id must not be blank");
        }
        if (!value.equals(value.trim())) {
            throw new IllegalArgumentException("transport node id must not contain outer whitespace");
        }
    }

    @Override
    public int compareTo(TransportNodeId other) {
        return value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
