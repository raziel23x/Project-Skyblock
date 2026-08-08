package raziel23x.projectskyblock.simulation.machine.component;

import java.util.Objects;
import raziel23x.projectskyblock.simulation.energy.EnergyLimits;

/**
 * Isolated candidate state for one atomic machine-energy endpoint operation.
 *
 * <p>The transaction captures one component version and access mode. Staging never changes
 * authoritative energy, dirty flags, or scheduler state. Commit succeeds only while the captured
 * endpoint version is current. One transaction is directional: it may receive/produce or
 * extract/consume, but it may not mix both directions.</p>
 */
public final class MachineEnergyTransaction implements AutoCloseable {
    private final MachineEnergyComponent owner;
    private final long baseVersion;
    private final MachineEnergyAccess access;
    private final EnergyLimits limits;
    private final long originalStoredEnergy;
    private long workingStoredEnergy;
    private long receivedEnergy;
    private long extractedEnergy;
    private Direction direction = Direction.NONE;
    private boolean closed;

    MachineEnergyTransaction(
            MachineEnergyComponent owner,
            long baseVersion,
            MachineEnergyAccess access,
            EnergyLimits limits,
            long storedEnergy) {
        this.owner = Objects.requireNonNull(owner, "owner");
        if (baseVersion < 0L) {
            throw new IllegalArgumentException("base version must be non-negative");
        }
        this.baseVersion = baseVersion;
        this.access = Objects.requireNonNull(access, "access");
        this.limits = Objects.requireNonNull(limits, "limits");
        if (storedEnergy < 0L || storedEnergy > limits.capacity()) {
            throw new IllegalArgumentException("stored energy must fit inside capacity");
        }
        originalStoredEnergy = storedEnergy;
        workingStoredEnergy = storedEnergy;
    }

    public long storedEnergy() {
        ensureOpen();
        return workingStoredEnergy;
    }

    public long availableCapacity() {
        ensureOpen();
        return limits.capacity() - workingStoredEnergy;
    }

    public EnergyLimits limits() {
        ensureOpen();
        return limits;
    }

    public MachineEnergyAccess access() {
        ensureOpen();
        return access;
    }

    public long receive(long requestedEnergy) {
        ensureOpen();
        requireNonNegative(requestedEnergy);
        if (!access.acceptsEnergy()) {
            return 0L;
        }
        return stageReceive(requestedEnergy);
    }

    public long extract(long requestedEnergy) {
        ensureOpen();
        requireNonNegative(requestedEnergy);
        if (!access.providesEnergy()) {
            return 0L;
        }
        return stageExtract(requestedEnergy);
    }

    /** Stages internally produced energy without requiring external input access. */
    public long produce(long requestedEnergy) {
        ensureOpen();
        requireNonNegative(requestedEnergy);
        return stageReceive(requestedEnergy);
    }

    /** Stages internally consumed energy without requiring external output access. */
    public long consume(long requestedEnergy) {
        ensureOpen();
        requireNonNegative(requestedEnergy);
        return stageExtract(requestedEnergy);
    }

    public boolean changed() {
        ensureOpen();
        return workingStoredEnergy != originalStoredEnergy;
    }

    public long receivedEnergy() {
        ensureOpen();
        return receivedEnergy;
    }

    public long extractedEnergy() {
        ensureOpen();
        return extractedEnergy;
    }

    public MachineEnergyCommitResult commit() {
        ensureOpen();
        return owner.commitTransaction(this);
    }

    public void rollback() {
        closed = true;
    }

    @Override
    public void close() {
        rollback();
    }

    long baseVersion() {
        return baseVersion;
    }

    long workingStoredEnergy() {
        return workingStoredEnergy;
    }

    boolean belongsTo(MachineEnergyComponent candidateOwner) {
        return owner == candidateOwner;
    }

    void markCommitted() {
        closed = true;
    }

    private long stageReceive(long requestedEnergy) {
        if (requestedEnergy == 0L) {
            return 0L;
        }
        requireDirection(Direction.RECEIVE);
        long remainingOperationCapacity = limits.maximumReceivePerOperation() - receivedEnergy;
        long accepted = Math.min(
                requestedEnergy,
                Math.min(remainingOperationCapacity, availableCapacity()));
        if (accepted <= 0L) {
            return 0L;
        }
        workingStoredEnergy = Math.addExact(workingStoredEnergy, accepted);
        receivedEnergy = Math.addExact(receivedEnergy, accepted);
        return accepted;
    }

    private long stageExtract(long requestedEnergy) {
        if (requestedEnergy == 0L) {
            return 0L;
        }
        requireDirection(Direction.EXTRACT);
        long remainingOperationCapacity = limits.maximumExtractPerOperation() - extractedEnergy;
        long extracted = Math.min(
                requestedEnergy,
                Math.min(remainingOperationCapacity, workingStoredEnergy));
        if (extracted <= 0L) {
            return 0L;
        }
        workingStoredEnergy = Math.subtractExact(workingStoredEnergy, extracted);
        extractedEnergy = Math.addExact(extractedEnergy, extracted);
        return extracted;
    }

    private void requireDirection(Direction requestedDirection) {
        if (direction == Direction.NONE) {
            direction = requestedDirection;
            return;
        }
        if (direction != requestedDirection) {
            throw new IllegalStateException(
                    "machine-energy transaction cannot mix receive and extract operations");
        }
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("energy transaction is already closed");
        }
    }

    private static void requireNonNegative(long value) {
        if (value < 0L) {
            throw new IllegalArgumentException("requested energy must be non-negative");
        }
    }

    private enum Direction {
        NONE,
        RECEIVE,
        EXTRACT
    }
}
