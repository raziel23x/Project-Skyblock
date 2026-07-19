package raziel23x.projectskyblock.simulation.machine;

/** Stable backend identity for a simulated machine. */
public record MachineId(String value) implements Comparable<MachineId> {
    public MachineId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("machine id must not be blank");
        }
    }

    @Override
    public int compareTo(MachineId other) {
        return value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
