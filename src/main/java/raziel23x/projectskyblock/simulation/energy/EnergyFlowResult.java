package raziel23x.projectskyblock.simulation.energy;

/** Complete state transition produced by one energy-engine operation. */
public record EnergyFlowResult(
        long sourceEnergyBefore,
        long sourceEnergyAfter,
        long targetEnergyBefore,
        long targetEnergyAfter,
        EnergyTransfer transfer) {

    public EnergyFlowResult {
        if (sourceEnergyBefore < 0 || sourceEnergyAfter < 0
                || targetEnergyBefore < 0 || targetEnergyAfter < 0) {
            throw new IllegalArgumentException("buffer energy values must be non-negative");
        }
        java.util.Objects.requireNonNull(transfer, "transfer");
    }
}
