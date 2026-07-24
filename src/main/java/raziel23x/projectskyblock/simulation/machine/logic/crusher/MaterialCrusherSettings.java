package raziel23x.projectskyblock.simulation.machine.logic.crusher;

/** Immutable runtime settings consumed by the Minecraft-independent crusher logic. */
public record MaterialCrusherSettings(
        boolean fuelPowerEnabled,
        boolean fePowerEnabled,
        boolean preferFe,
        long processTimeUnits,
        long energyPerUnit,
        double fuelBurnMultiplier) {

    public MaterialCrusherSettings {
        if (processTimeUnits <= 0L) {
            throw new IllegalArgumentException("process time units must be positive");
        }
        if (energyPerUnit < 0L) {
            throw new IllegalArgumentException("energy per unit must be non-negative");
        }
        if (!Double.isFinite(fuelBurnMultiplier) || fuelBurnMultiplier <= 0.0D) {
            throw new IllegalArgumentException("fuel burn multiplier must be finite and positive");
        }
    }
}
