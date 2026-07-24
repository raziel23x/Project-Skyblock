package raziel23x.projectskyblock.simulation.machine.component;

/** Immutable diagnostics for one engine-owned combustion reservoir. */
public record MachineCombustionDiagnostics(
        long remainingBurnUnits,
        long totalBurnUnits,
        long totalUnitsConsumed,
        long ignitionCount,
        long changeCount) {

    public boolean burning() {
        return remainingBurnUnits > 0L;
    }
}
