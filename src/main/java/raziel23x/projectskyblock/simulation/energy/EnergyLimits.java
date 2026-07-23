package raziel23x.projectskyblock.simulation.energy;

/**
 * Immutable capacity and single-operation throughput limits for an energy buffer.
 *
 * <p>These limits apply independently to each receive or extract operation. Aggregate
 * network throughput and shared-edge contention are separate scheduler concerns and must
 * not be inferred from this buffer-local contract.</p>
 */
public record EnergyLimits(
        long capacity,
        long maximumReceivePerOperation,
        long maximumExtractPerOperation) {
    public EnergyLimits {
        if (capacity < 0 || maximumReceivePerOperation < 0 || maximumExtractPerOperation < 0) {
            throw new IllegalArgumentException("energy limits must be non-negative");
        }
    }

    public static EnergyLimits unlimitedThroughput(long capacity) {
        return new EnergyLimits(capacity, Long.MAX_VALUE, Long.MAX_VALUE);
    }
}
