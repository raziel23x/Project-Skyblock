package raziel23x.projectskyblock.simulation.thermal;

import java.util.Objects;

/**
 * Stateless authoritative thermal solver.
 *
 * <p>One call applies internally generated heat, exchanges heat with the
 * supplied environment, and derives the participant's operating condition.</p>
 */
public final class ThermalEngine {
    private ThermalEngine() {
    }

    public static ThermalDiagnostics step(
            ThermalState state,
            ThermalProperties properties,
            ThermalEnvironment environment,
            long generatedHeatMicroJoules) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(properties, "properties");
        Objects.requireNonNull(environment, "environment");
        if (generatedHeatMicroJoules < 0) {
            throw new IllegalArgumentException("generated heat must be non-negative");
        }

        long previousTemperature = state.temperatureMilliKelvin();
        state.addHeatMicroJoules(generatedHeatMicroJoules);
        long environmentalHeat = exchangeWithEnvironment(state, properties, environment);
        return new ThermalDiagnostics(
                previousTemperature,
                state.temperatureMilliKelvin(),
                generatedHeatMicroJoules,
                environmentalHeat,
                properties.condition(state));
    }

    /**
     * Exchanges heat with an effectively infinite ambient reservoir.
     * Positive results mean heat entered the participant; negative results mean
     * heat left it.
     */
    public static long exchangeWithEnvironment(
            ThermalState state,
            ThermalProperties properties,
            ThermalEnvironment environment) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(properties, "properties");
        Objects.requireNonNull(environment, "environment");

        long temperatureDifference = environment.ambientTemperatureMilliKelvin()
                - state.temperatureMilliKelvin();
        if (temperatureDifference == 0) {
            return 0;
        }

        long combinedConductance = Math.min(
                properties.conductanceMicroJoulesPerMilliKelvinPerTick(),
                environment.conductanceMicroJoulesPerMilliKelvinPerTick());
        if (combinedConductance == 0 || environment.maximumExchangeMicroJoulesPerTick() == 0) {
            return 0;
        }

        long absoluteDifference = temperatureDifference == Long.MIN_VALUE
                ? Long.MAX_VALUE
                : Math.abs(temperatureDifference);
        long requested = ThermalTransfer.saturatedMultiply(absoluteDifference, combinedConductance);
        long equilibriumLimit = ThermalTransfer.saturatedMultiply(
                absoluteDifference,
                state.heatCapacityMicroJoulesPerMilliKelvin());
        long exchange = Math.min(
                Math.min(requested, equilibriumLimit),
                environment.maximumExchangeMicroJoulesPerTick());

        if (temperatureDifference > 0) {
            return state.addHeatMicroJoules(exchange);
        }
        return -state.removeHeatMicroJoules(exchange);
    }
}
