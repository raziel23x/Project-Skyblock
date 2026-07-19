package raziel23x.projectskyblock.simulation.energy;

import raziel23x.projectskyblock.simulation.core.SimulationState;

import java.util.Objects;

/** Authoritative Minecraft-independent energy storage state. */
public final class SimulationEnergyState implements SimulationState, EnergyBuffer {
    private final EnergyLimits limits;
    private long storedEnergy;

    public SimulationEnergyState(EnergyLimits limits) {
        this(limits, 0);
    }

    public SimulationEnergyState(EnergyLimits limits, long storedEnergy) {
        this.limits = Objects.requireNonNull(limits, "limits");
        if (storedEnergy < 0 || storedEnergy > limits.capacity()) {
            throw new IllegalArgumentException("stored energy must fit inside capacity");
        }
        this.storedEnergy = storedEnergy;
    }

    @Override
    public long storedEnergy() {
        return storedEnergy;
    }

    @Override
    public EnergyLimits limits() {
        return limits;
    }

    @Override
    public long receive(long requestedEnergy) {
        requireNonNegative(requestedEnergy);
        long accepted = Math.min(requestedEnergy,
                Math.min(limits.maximumReceivePerTick(), availableCapacity()));
        storedEnergy += accepted;
        return accepted;
    }

    @Override
    public long extract(long requestedEnergy) {
        requireNonNegative(requestedEnergy);
        long extracted = Math.min(requestedEnergy,
                Math.min(limits.maximumExtractPerTick(), storedEnergy));
        storedEnergy -= extracted;
        return extracted;
    }

    /** Restores validated persisted state without exposing any NBT dependency. */
    public void restoreStoredEnergy(long storedEnergy) {
        if (storedEnergy < 0 || storedEnergy > limits.capacity()) {
            throw new IllegalArgumentException("stored energy must fit inside capacity");
        }
        this.storedEnergy = storedEnergy;
    }

    private static void requireNonNegative(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("requested energy must be non-negative");
        }
    }
}
