package raziel23x.projectskyblock.simulation.energy;

/** Stable Minecraft-independent identifier for one node in an energy topology. */
public record EnergyNetworkNodeId(String value) implements Comparable<EnergyNetworkNodeId> {
    public EnergyNetworkNodeId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("energy network node id must not be blank");
        }
    }

    @Override
    public int compareTo(EnergyNetworkNodeId other) {
        return value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
