package raziel23x.projectskyblock.simulation.machine.component;

import raziel23x.projectskyblock.simulation.thermal.ThermalCondition;
import raziel23x.projectskyblock.simulation.thermal.ThermalProperties;

/** Immutable persistence and synchronization view of a machine thermal component. */
public record MachineThermalSnapshot(
        MachineThermalAccess access,
        long temperatureMilliKelvin,
        long thermalEnergyMicroJoules,
        long heatCapacityMicroJoulesPerMilliKelvin,
        ThermalProperties properties,
        ThermalCondition condition) {

    public MachineThermalSnapshot {
        if (access == null) {
            throw new NullPointerException("access");
        }
        if (properties == null) {
            throw new NullPointerException("properties");
        }
        if (condition == null) {
            throw new NullPointerException("condition");
        }
        if (temperatureMilliKelvin < 0L || thermalEnergyMicroJoules < 0L
                || heatCapacityMicroJoulesPerMilliKelvin <= 0L) {
            throw new IllegalArgumentException("thermal snapshot values are invalid");
        }
    }
}
