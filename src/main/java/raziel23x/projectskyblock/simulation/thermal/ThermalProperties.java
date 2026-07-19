package raziel23x.projectskyblock.simulation.thermal;

/** Immutable thermal behavior shared by machines, cables, and other participants. */
public record ThermalProperties(
        long conductanceMicroJoulesPerMilliKelvinPerTick,
        long minimumOperatingTemperatureMilliKelvin,
        long maximumOperatingTemperatureMilliKelvin,
        long shutdownTemperatureMilliKelvin) {

    public ThermalProperties {
        if (conductanceMicroJoulesPerMilliKelvinPerTick < 0) {
            throw new IllegalArgumentException("conductance must be non-negative");
        }
        if (minimumOperatingTemperatureMilliKelvin < 0) {
            throw new IllegalArgumentException("minimum temperature cannot be negative");
        }
        if (maximumOperatingTemperatureMilliKelvin < minimumOperatingTemperatureMilliKelvin) {
            throw new IllegalArgumentException("maximum temperature must not be below minimum");
        }
        if (shutdownTemperatureMilliKelvin < maximumOperatingTemperatureMilliKelvin) {
            throw new IllegalArgumentException("shutdown temperature must not be below maximum operating temperature");
        }
    }

    public boolean isWithinOperatingRange(ThermalState state) {
        long temperature = state.temperatureMilliKelvin();
        return temperature >= minimumOperatingTemperatureMilliKelvin
                && temperature <= maximumOperatingTemperatureMilliKelvin;
    }

    public boolean requiresShutdown(ThermalState state) {
        return state.temperatureMilliKelvin() >= shutdownTemperatureMilliKelvin;
    }
}
