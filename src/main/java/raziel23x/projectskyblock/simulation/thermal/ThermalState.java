package raziel23x.projectskyblock.simulation.thermal;

import raziel23x.projectskyblock.simulation.core.SimulationState;

/**
 * Authoritative fixed-point thermal state.
 *
 * <p>Heat is retained internally as microjoules, including amounts too small to
 * change the exposed milli-kelvin temperature. This prevents repeated small
 * transfers from disappearing through integer rounding.</p>
 */
public final class ThermalState implements SimulationState {
    public static final long ABSOLUTE_ZERO_MK = ThermalConstants.ABSOLUTE_ZERO_MILLI_KELVIN;

    private final long heatCapacityMicroJoulesPerMilliKelvin;
    private long thermalEnergyMicroJoules;

    public ThermalState(long temperatureMilliKelvin, long heatCapacityMicroJoulesPerMilliKelvin) {
        if (temperatureMilliKelvin < ABSOLUTE_ZERO_MK) {
            throw new IllegalArgumentException("temperature cannot be below absolute zero");
        }
        if (heatCapacityMicroJoulesPerMilliKelvin <= 0) {
            throw new IllegalArgumentException("heat capacity must be positive");
        }
        this.heatCapacityMicroJoulesPerMilliKelvin = heatCapacityMicroJoulesPerMilliKelvin;
        this.thermalEnergyMicroJoules = Math.multiplyExact(
                temperatureMilliKelvin,
                heatCapacityMicroJoulesPerMilliKelvin);
    }

    public long temperatureMilliKelvin() {
        return thermalEnergyMicroJoules / heatCapacityMicroJoulesPerMilliKelvin;
    }

    public long heatCapacityMicroJoulesPerMilliKelvin() {
        return heatCapacityMicroJoulesPerMilliKelvin;
    }

    public long thermalEnergyMicroJoules() {
        return thermalEnergyMicroJoules;
    }

    /** Adds all requested heat and returns the accepted amount. */
    public long addHeatMicroJoules(long heatMicroJoules) {
        if (heatMicroJoules < 0) {
            throw new IllegalArgumentException("heat must be non-negative");
        }
        thermalEnergyMicroJoules = Math.addExact(thermalEnergyMicroJoules, heatMicroJoules);
        return heatMicroJoules;
    }

    /** Removes up to the requested heat without crossing absolute zero. */
    public long removeHeatMicroJoules(long requestedMicroJoules) {
        if (requestedMicroJoules < 0) {
            throw new IllegalArgumentException("heat must be non-negative");
        }
        long removed = Math.min(requestedMicroJoules, thermalEnergyMicroJoules);
        thermalEnergyMicroJoules -= removed;
        return removed;
    }
}
