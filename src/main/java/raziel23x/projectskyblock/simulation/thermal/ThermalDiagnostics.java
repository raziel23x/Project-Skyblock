package raziel23x.projectskyblock.simulation.thermal;

/** Immutable diagnostic result for one thermal-engine step. */
public record ThermalDiagnostics(
        long previousTemperatureMilliKelvin,
        long currentTemperatureMilliKelvin,
        long generatedHeatMicroJoules,
        long environmentalHeatMicroJoules,
        ThermalCondition condition) {

    public boolean temperatureChanged() {
        return previousTemperatureMilliKelvin != currentTemperatureMilliKelvin;
    }

    public long netHeatMicroJoules() {
        return Math.addExact(generatedHeatMicroJoules, environmentalHeatMicroJoules);
    }
}
