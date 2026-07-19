package raziel23x.projectskyblock.simulation.machine.component;

import raziel23x.projectskyblock.simulation.thermal.ThermalCondition;

/** Immutable diagnostic snapshot for a machine-owned thermal component. */
public record MachineThermalDiagnostics(
        MachineThermalAccess access,
        long temperatureMilliKelvin,
        long thermalEnergyMicroJoules,
        long heatCapacityMicroJoulesPerMilliKelvin,
        ThermalCondition condition,
        long totalHeatAddedMicroJoules,
        long totalHeatRemovedMicroJoules,
        long netEnvironmentalHeatMicroJoules,
        long changeCount) {

    public MachineThermalDiagnostics {
        if (access == null) {
            throw new NullPointerException("access");
        }
        if (condition == null) {
            throw new NullPointerException("condition");
        }
        if (temperatureMilliKelvin < 0L || thermalEnergyMicroJoules < 0L
                || heatCapacityMicroJoulesPerMilliKelvin <= 0L
                || totalHeatAddedMicroJoules < 0L || totalHeatRemovedMicroJoules < 0L
                || changeCount < 0L) {
            throw new IllegalArgumentException("thermal diagnostics values are invalid");
        }
    }
}
