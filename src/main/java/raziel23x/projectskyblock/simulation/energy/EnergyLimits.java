package raziel23x.projectskyblock.simulation.energy;

/** Immutable capacity and per-tick throughput limits for an energy buffer. */
public record EnergyLimits(long capacity, long maximumReceivePerTick, long maximumExtractPerTick) {
    public EnergyLimits {
        if (capacity < 0 || maximumReceivePerTick < 0 || maximumExtractPerTick < 0) {
            throw new IllegalArgumentException("energy limits must be non-negative");
        }
    }

    public static EnergyLimits unlimitedThroughput(long capacity) {
        return new EnergyLimits(capacity, Long.MAX_VALUE, Long.MAX_VALUE);
    }
}
