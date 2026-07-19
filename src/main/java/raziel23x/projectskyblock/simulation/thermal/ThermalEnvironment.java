package raziel23x.projectskyblock.simulation.thermal;

/**
 * Immutable environmental conditions supplied by the platform integration layer.
 *
 * <p>The thermal core deliberately knows nothing about dimensions, biomes,
 * weather, fluids, or blocks. Minecraft-facing code converts those facts into
 * this narrow value.</p>
 */
public record ThermalEnvironment(
        long ambientTemperatureMilliKelvin,
        long conductanceMicroJoulesPerMilliKelvinPerTick,
        long maximumExchangeMicroJoulesPerTick) {

    public ThermalEnvironment {
        if (ambientTemperatureMilliKelvin < ThermalConstants.ABSOLUTE_ZERO_MILLI_KELVIN) {
            throw new IllegalArgumentException("ambient temperature cannot be below absolute zero");
        }
        if (conductanceMicroJoulesPerMilliKelvinPerTick < 0) {
            throw new IllegalArgumentException("environmental conductance must be non-negative");
        }
        if (maximumExchangeMicroJoulesPerTick < 0) {
            throw new IllegalArgumentException("maximum exchange must be non-negative");
        }
    }

    public static ThermalEnvironment standardAmbient(
            long conductanceMicroJoulesPerMilliKelvinPerTick,
            long maximumExchangeMicroJoulesPerTick) {
        return new ThermalEnvironment(
                ThermalConstants.STANDARD_AMBIENT_MILLI_KELVIN,
                conductanceMicroJoulesPerMilliKelvinPerTick,
                maximumExchangeMicroJoulesPerTick);
    }
}
