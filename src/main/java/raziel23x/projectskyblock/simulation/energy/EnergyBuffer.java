package raziel23x.projectskyblock.simulation.energy;

/**
 * Minecraft-independent contract for an authoritative energy buffer.
 *
 * <p>Implementations must never accept more than their remaining capacity or
 * extract more than their stored energy and configured per-operation limits. Aggregate
 * per-step or per-network throughput is enforced by the scheduler or network solver that
 * owns that operation.</p>
 */
public interface EnergyBuffer {
    long storedEnergy();

    EnergyLimits limits();

    long receive(long requestedEnergy);

    long extract(long requestedEnergy);

    default long capacity() {
        return limits().capacity();
    }

    default long availableCapacity() {
        return capacity() - storedEnergy();
    }

    default boolean isEmpty() {
        return storedEnergy() == 0;
    }

    default boolean isFull() {
        return storedEnergy() == capacity();
    }
}
