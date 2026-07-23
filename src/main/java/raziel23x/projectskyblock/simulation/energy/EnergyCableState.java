package raziel23x.projectskyblock.simulation.energy;

import raziel23x.projectskyblock.simulation.core.SimulationState;
import raziel23x.projectskyblock.simulation.thermal.ThermalState;

/**
 * Authoritative electrical and thermal state for one prototype cable segment.
 *
 * <p>The transfer limit is a single-operation bound. This state does not claim aggregate
 * per-tick ownership or fairness across competing transfers.</p>
 */
public final class EnergyCableState implements SimulationState {
    private final long capacity;
    private final long maximumTransferPerOperation;
    private final int lossPartsPerMillion;
    private final ThermalState thermalState;
    private long storedEnergy;

    public EnergyCableState(
            long capacity,
            long maximumTransferPerOperation,
            int lossPartsPerMillion,
            long storedEnergy,
            ThermalState thermalState) {
        if (capacity < 0 || maximumTransferPerOperation < 0) {
            throw new IllegalArgumentException("capacity and transfer limit must be non-negative");
        }
        if (lossPartsPerMillion < 0 || lossPartsPerMillion > 1_000_000) {
            throw new IllegalArgumentException("loss must be between 0 and 1,000,000 ppm");
        }
        if (storedEnergy < 0 || storedEnergy > capacity) {
            throw new IllegalArgumentException("stored energy must fit inside capacity");
        }
        this.capacity = capacity;
        this.maximumTransferPerOperation = maximumTransferPerOperation;
        this.lossPartsPerMillion = lossPartsPerMillion;
        this.storedEnergy = storedEnergy;
        this.thermalState = java.util.Objects.requireNonNull(thermalState, "thermalState");
    }

    public long capacity() { return capacity; }
    public long maximumTransferPerOperation() { return maximumTransferPerOperation; }
    public int lossPartsPerMillion() { return lossPartsPerMillion; }
    public long storedEnergy() { return storedEnergy; }
    public ThermalState thermalState() { return thermalState; }

    public long receive(long requested) {
        if (requested < 0) throw new IllegalArgumentException("requested must be non-negative");
        long accepted = Math.min(requested, capacity - storedEnergy);
        storedEnergy += accepted;
        return accepted;
    }

    public long extract(long requested) {
        if (requested < 0) throw new IllegalArgumentException("requested must be non-negative");
        long extracted = Math.min(requested, storedEnergy);
        storedEnergy -= extracted;
        return extracted;
    }
}
