package raziel23x.projectskyblock.simulation.machine.component;

/** Immutable result of one committed machine-energy transaction. */
public record MachineEnergyCommitResult(
        boolean changed,
        long receivedEnergy,
        long extractedEnergy,
        long storedEnergy,
        long stateVersion) {

    public MachineEnergyCommitResult {
        if (receivedEnergy < 0L || extractedEnergy < 0L || storedEnergy < 0L || stateVersion < 0L) {
            throw new IllegalArgumentException("energy commit result values must be non-negative");
        }
        if (receivedEnergy > 0L && extractedEnergy > 0L) {
            throw new IllegalArgumentException(
                    "one machine-energy transaction cannot both receive and extract energy");
        }
        if (!changed && (receivedEnergy != 0L || extractedEnergy != 0L)) {
            throw new IllegalArgumentException(
                    "unchanged energy commit cannot report transferred energy");
        }
        if (changed && receivedEnergy == 0L && extractedEnergy == 0L) {
            throw new IllegalArgumentException(
                    "changed energy commit must report received or extracted energy");
        }
    }
}
