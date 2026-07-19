package raziel23x.projectskyblock.simulation.thermal;

import raziel23x.projectskyblock.simulation.core.SimulationState;

/**
 * Fixed-point thermal state.
 *
 * <p>Temperature is stored in milli-kelvin. Heat capacity is stored as
 * microjoules required per milli-kelvin. This avoids floating-point drift in
 * authoritative server simulation.</p>
 */
public final class ThermalState implements SimulationState {
    public static final long ABSOLUTE_ZERO_MK = 0L;

    private final long heatCapacityMicroJoulesPerMilliKelvin;
    private long temperatureMilliKelvin;

    public ThermalState(long temperatureMilliKelvin, long heatCapacityMicroJoulesPerMilliKelvin) {
        if (temperatureMilliKelvin < ABSOLUTE_ZERO_MK) {
            throw new IllegalArgumentException("temperature cannot be below absolute zero");
        }
        if (heatCapacityMicroJoulesPerMilliKelvin <= 0) {
            throw new IllegalArgumentException("heat capacity must be positive");
        }
        this.temperatureMilliKelvin = temperatureMilliKelvin;
        this.heatCapacityMicroJoulesPerMilliKelvin = heatCapacityMicroJoulesPerMilliKelvin;
    }

    public long temperatureMilliKelvin() {
        return temperatureMilliKelvin;
    }

    public long heatCapacityMicroJoulesPerMilliKelvin() {
        return heatCapacityMicroJoulesPerMilliKelvin;
    }

    public long addHeatMicroJoules(long heatMicroJoules) {
        if (heatMicroJoules < 0) {
            throw new IllegalArgumentException("heat must be non-negative");
        }
        long delta = heatMicroJoules / heatCapacityMicroJoulesPerMilliKelvin;
        if (delta == 0) {
            return 0;
        }
        long previous = temperatureMilliKelvin;
        temperatureMilliKelvin = Math.addExact(temperatureMilliKelvin, delta);
        return Math.multiplyExact(temperatureMilliKelvin - previous, heatCapacityMicroJoulesPerMilliKelvin);
    }

    public long removeHeatMicroJoules(long requestedMicroJoules) {
        if (requestedMicroJoules < 0) {
            throw new IllegalArgumentException("heat must be non-negative");
        }
        long available = Math.multiplyExact(temperatureMilliKelvin, heatCapacityMicroJoulesPerMilliKelvin);
        long removable = Math.min(requestedMicroJoules, available);
        long delta = removable / heatCapacityMicroJoulesPerMilliKelvin;
        if (delta == 0) {
            return 0;
        }
        temperatureMilliKelvin -= delta;
        return Math.multiplyExact(delta, heatCapacityMicroJoulesPerMilliKelvin);
    }
}
