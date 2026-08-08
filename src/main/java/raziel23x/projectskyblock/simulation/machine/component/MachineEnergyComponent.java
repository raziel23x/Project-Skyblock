package raziel23x.projectskyblock.simulation.machine.component;

import java.util.ConcurrentModificationException;
import java.util.Objects;
import raziel23x.projectskyblock.simulation.core.DirtyFlag;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;
import raziel23x.projectskyblock.simulation.energy.EnergyBuffer;
import raziel23x.projectskyblock.simulation.energy.EnergyLimits;
import raziel23x.projectskyblock.simulation.energy.SimulationEnergyState;

/**
 * Reusable Minecraft-independent energy component owned by a simulated machine.
 *
 * <p>The component delegates authoritative storage and throughput enforcement to
 * {@link SimulationEnergyState}. It adds machine-facing access rules, optimistic endpoint
 * transactions, dirty-state signaling, wake signaling, and diagnostics without introducing
 * NeoForge Energy or BlockEntity dependencies.</p>
 */
public final class MachineEnergyComponent implements EnergyBuffer {
    private final SimulationEnergyState energy;
    private final DirtyStateTracker dirtyState;
    private final Runnable wakeSignal;
    private MachineEnergyAccess access;
    private long totalReceived;
    private long totalExtracted;
    private long changeCount;
    private long stateVersion;
    private long transactionCommitCount;
    private long transactionConflictCount;

    public MachineEnergyComponent(EnergyLimits limits, MachineEnergyAccess access) {
        this(new SimulationEnergyState(limits), access, new DirtyStateTracker(), () -> { });
    }

    public MachineEnergyComponent(
            SimulationEnergyState energy,
            MachineEnergyAccess access,
            DirtyStateTracker dirtyState,
            Runnable wakeSignal) {
        this.energy = Objects.requireNonNull(energy, "energy");
        this.access = Objects.requireNonNull(access, "access");
        this.dirtyState = Objects.requireNonNull(dirtyState, "dirtyState");
        this.wakeSignal = Objects.requireNonNull(wakeSignal, "wakeSignal");
    }

    public MachineEnergyAccess access() {
        return access;
    }

    /**
     * Changes the external endpoint contract and invalidates any transaction opened beforehand.
     */
    public synchronized void setAccess(MachineEnergyAccess access) {
        MachineEnergyAccess requested = Objects.requireNonNull(access, "access");
        if (this.access == requested) {
            return;
        }
        long nextChangeCount = Math.addExact(changeCount, 1L);
        long nextStateVersion = Math.addExact(stateVersion, 1L);
        this.access = requested;
        changeCount = nextChangeCount;
        stateVersion = nextStateVersion;
        signalChanged();
    }

    public DirtyStateTracker dirtyState() {
        return dirtyState;
    }

    /** Monotonic identity for authoritative endpoint energy/access state. */
    public long stateVersion() {
        return stateVersion;
    }

    /** Opens an isolated candidate over the current authoritative endpoint state. */
    public synchronized MachineEnergyTransaction beginTransaction() {
        return new MachineEnergyTransaction(
                this,
                stateVersion,
                access,
                energy.limits(),
                energy.storedEnergy());
    }

    @Override
    public long storedEnergy() {
        return energy.storedEnergy();
    }

    @Override
    public EnergyLimits limits() {
        return energy.limits();
    }

    /** Receives energy from an external network or adapter when input is permitted. */
    @Override
    public long receive(long requestedEnergy) {
        requireNonNegative(requestedEnergy);
        try (MachineEnergyTransaction transaction = beginTransaction()) {
            long accepted = transaction.receive(requestedEnergy);
            if (transaction.changed()) {
                transaction.commit();
            }
            return accepted;
        }
    }

    /** Extracts energy to an external network or adapter when output is permitted. */
    @Override
    public long extract(long requestedEnergy) {
        requireNonNegative(requestedEnergy);
        try (MachineEnergyTransaction transaction = beginTransaction()) {
            long extracted = transaction.extract(requestedEnergy);
            if (transaction.changed()) {
                transaction.commit();
            }
            return extracted;
        }
    }

    /**
     * Consumes stored energy for the owning machine's internal processing.
     * External output access is intentionally not required.
     */
    public long consume(long requestedEnergy) {
        requireNonNegative(requestedEnergy);
        try (MachineEnergyTransaction transaction = beginTransaction()) {
            long consumed = transaction.consume(requestedEnergy);
            if (transaction.changed()) {
                transaction.commit();
            }
            return consumed;
        }
    }

    /**
     * Produces energy internally for the owning machine.
     * External input access is intentionally not required.
     */
    public long produce(long requestedEnergy) {
        requireNonNegative(requestedEnergy);
        try (MachineEnergyTransaction transaction = beginTransaction()) {
            long produced = transaction.produce(requestedEnergy);
            if (transaction.changed()) {
                transaction.commit();
            }
            return produced;
        }
    }

    public boolean canConsume(long requestedEnergy) {
        requireNonNegative(requestedEnergy);
        return requestedEnergy <= storedEnergy()
                && requestedEnergy <= limits().maximumExtractPerOperation();
    }

    /** Validates a persisted energy candidate without mutating authoritative state. */
    public void validateStoredEnergy(long storedEnergy) {
        energy.validateStoredEnergy(storedEnergy);
    }

    /**
     * Restores prevalidated durable energy while invalidating stale endpoint candidates.
     *
     * <p>Loading does not create persistence or scheduler dirtiness. A changed restore advances the
     * endpoint state version and requests client synchronization, matching the machine inventory
     * restoration boundary.</p>
     */
    public synchronized void restoreStoredEnergy(long storedEnergy) {
        energy.validateStoredEnergy(storedEnergy);
        if (energy.storedEnergy() == storedEnergy) {
            return;
        }
        long nextStateVersion = Math.addExact(stateVersion, 1L);
        energy.restoreStoredEnergy(storedEnergy);
        stateVersion = nextStateVersion;
        dirtyState.mark(DirtyFlag.CLIENT_SYNC);
    }

    public MachineEnergyDiagnostics diagnostics() {
        return new MachineEnergyDiagnostics(
                access,
                storedEnergy(),
                capacity(),
                availableCapacity(),
                limits().maximumReceivePerOperation(),
                limits().maximumExtractPerOperation(),
                totalReceived,
                totalExtracted,
                changeCount,
                stateVersion,
                transactionCommitCount,
                transactionConflictCount);
    }

    synchronized MachineEnergyCommitResult commitTransaction(
            MachineEnergyTransaction transaction) {
        Objects.requireNonNull(transaction, "transaction");
        if (!transaction.belongsTo(this)) {
            throw new IllegalArgumentException("energy transaction belongs to another component");
        }
        if (transaction.baseVersion() != stateVersion) {
            transactionConflictCount = Math.addExact(transactionConflictCount, 1L);
            transaction.markCommitted();
            throw new ConcurrentModificationException(
                    "energy transaction is stale: expected version "
                            + transaction.baseVersion() + " but found " + stateVersion);
        }

        long candidateStoredEnergy = transaction.workingStoredEnergy();
        energy.validateStoredEnergy(candidateStoredEnergy);
        if (candidateStoredEnergy == energy.storedEnergy()) {
            transaction.markCommitted();
            return new MachineEnergyCommitResult(
                    false,
                    0L,
                    0L,
                    energy.storedEnergy(),
                    stateVersion);
        }

        long receivedEnergy = transaction.receivedEnergy();
        long extractedEnergy = transaction.extractedEnergy();
        long committedTotalReceived = Math.addExact(totalReceived, receivedEnergy);
        long committedTotalExtracted = Math.addExact(totalExtracted, extractedEnergy);
        long committedChangeCount = Math.addExact(changeCount, 1L);
        long committedStateVersion = Math.addExact(stateVersion, 1L);
        long committedTransactionCount = Math.addExact(transactionCommitCount, 1L);

        energy.restoreStoredEnergy(candidateStoredEnergy);
        totalReceived = committedTotalReceived;
        totalExtracted = committedTotalExtracted;
        changeCount = committedChangeCount;
        stateVersion = committedStateVersion;
        transactionCommitCount = committedTransactionCount;
        transaction.markCommitted();
        signalChanged();
        return new MachineEnergyCommitResult(
                true,
                receivedEnergy,
                extractedEnergy,
                candidateStoredEnergy,
                stateVersion);
    }

    private void signalChanged() {
        dirtyState.mark(DirtyFlag.PERSISTENCE, DirtyFlag.CLIENT_SYNC, DirtyFlag.SCHEDULER);
        wakeSignal.run();
    }

    private static void requireNonNegative(long value) {
        if (value < 0L) {
            throw new IllegalArgumentException("requested energy must be non-negative");
        }
    }
}
