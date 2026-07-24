package raziel23x.projectskyblock.simulation.machine.component;

/** Immutable result from atomically committing one machine-inventory transaction. */
public record MachineInventoryCommitResult(
        boolean changed,
        long insertedQuantity,
        long extractedQuantity,
        long stateVersion) {

    public MachineInventoryCommitResult {
        if (insertedQuantity < 0L || extractedQuantity < 0L || stateVersion < 0L) {
            throw new IllegalArgumentException("inventory commit values must be non-negative");
        }
        if (!changed && (insertedQuantity != 0L || extractedQuantity != 0L)) {
            throw new IllegalArgumentException("unchanged inventory commit must not report movement");
        }
    }
}
