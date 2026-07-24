package raziel23x.projectskyblock.simulation.machine.persistence;

import raziel23x.projectskyblock.simulation.machine.component.MachineCombustionComponent;

/** Durable combustion state included in one machine runtime snapshot. */
public record MachineCombustionSnapshot(long remainingBurnUnits, long totalBurnUnits) {
    public static final MachineCombustionSnapshot EMPTY = new MachineCombustionSnapshot(0L, 0L);

    public MachineCombustionSnapshot {
        MachineCombustionComponent.validateRestore(remainingBurnUnits, totalBurnUnits);
    }
}
