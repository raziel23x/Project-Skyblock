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
        if (minimumOperatingTemperatureMilliKelvin < ThermalConstants.ABSOLUTE_ZERO_MILLI_KELVIN) {
            throw new IllegalArgumentException("minimum temperature cannot be below absolute zero");
        }
        if (maximumOperatingTemperatureMilliKelvin < minimumOperatingTemperatureMilliKelvin) {
            throw new IllegalArgumentException("maximum temperature must not be below minimum");
        }
        if (shutdownTemperatureMilliKelvin < maximumOperatingTemperatureMilliKelvin) {
            throw new IllegalArgumentException("shutdown temperature must not be below maximum operating temperature");
        }
    }

    public boolean isWithinOperatingRange(ThermalState state) {
        return condition(state) == ThermalCondition.OPERATING;
    }

    public boolean requiresShutdown(ThermalState state) {
        return condition(state) == ThermalCondition.SHUTDOWN;
    }

    public ThermalCondition condition(ThermalState state) {
        long temperature = state.temperatureMilliKelvin();
        if (temperature >= shutdownTemperatureMilliKelvin) {
            return ThermalCondition.SHUTDOWN;
        }
        if (temperature < minimumOperatingTemperatureMilliKelvin) {
            return ThermalCondition.BELOW_OPERATING_RANGE;
        }
        if (temperature > maximumOperatingTemperatureMilliKelvin) {
            return ThermalCondition.ABOVE_OPERATING_RANGE;
        }
        return ThermalCondition.OPERATING;
    }
}
