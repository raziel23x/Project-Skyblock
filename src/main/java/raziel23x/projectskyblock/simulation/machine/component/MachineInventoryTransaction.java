package raziel23x.projectskyblock.simulation.machine.component;

import java.util.Arrays;
import java.util.Objects;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;

/**
 * Isolated mutable candidate state for one atomic machine-inventory operation.
 *
 * <p>A transaction never changes authoritative state until {@link #commit()} succeeds. The
 * component rejects a commit when any other operation changed the inventory after this candidate
 * was opened. Closing an uncommitted transaction discards it without dirty flags or wake signals.</p>
 */
public final class MachineInventoryTransaction implements AutoCloseable {
    private final MachineInventoryComponent owner;
    private final long baseVersion;
    private final SimulationItemStack[] originalStacks;
    private final SimulationItemStack[] workingStacks;
    private long insertedQuantity;
    private long extractedQuantity;
    private boolean closed;

    MachineInventoryTransaction(
            MachineInventoryComponent owner,
            long baseVersion,
            SimulationItemStack[] sourceStacks) {
        this.owner = Objects.requireNonNull(owner, "owner");
        this.baseVersion = baseVersion;
        this.originalStacks = sourceStacks.clone();
        this.workingStacks = sourceStacks.clone();
    }

    public int slotCount() {
        ensureOpen();
        return workingStacks.length;
    }

    public SimulationItemStack stack(int slot) {
        ensureOpen();
        owner.validateSlotForTransaction(slot);
        return workingStacks[slot];
    }

    public SimulationItemStack insert(int slot, SimulationItemStack offered) {
        return insert(slot, offered, true);
    }

    public SimulationItemStack store(int slot, SimulationItemStack offered) {
        return insert(slot, offered, false);
    }

    public SimulationItemStack extract(int slot, long requestedQuantity) {
        return extract(slot, requestedQuantity, true);
    }

    public SimulationItemStack consume(int slot, long requestedQuantity) {
        return extract(slot, requestedQuantity, false);
    }

    public boolean canInsert(int slot, SimulationItemStack offered) {
        ensureOpen();
        Objects.requireNonNull(offered, "offered");
        owner.validateSlotForTransaction(slot);
        return owner.canInsertCandidate(slot, workingStacks[slot], offered, true);
    }

    public boolean canStore(int slot, SimulationItemStack offered) {
        ensureOpen();
        Objects.requireNonNull(offered, "offered");
        owner.validateSlotForTransaction(slot);
        return owner.canInsertCandidate(slot, workingStacks[slot], offered, false);
    }

    public boolean canExtract(int slot, long requestedQuantity) {
        return canExtract(slot, requestedQuantity, true);
    }

    public boolean canConsume(int slot, long requestedQuantity) {
        return canExtract(slot, requestedQuantity, false);
    }

    public boolean changed() {
        ensureOpen();
        return !Arrays.equals(originalStacks, workingStacks);
    }

    public long insertedQuantity() {
        ensureOpen();
        return insertedQuantity;
    }

    public long extractedQuantity() {
        ensureOpen();
        return extractedQuantity;
    }

    /** Atomically replaces authoritative state when this candidate is still current. */
    public MachineInventoryCommitResult commit() {
        ensureOpen();
        return owner.commitTransaction(this);
    }

    /** Discards the candidate. Repeated calls are harmless. */
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

    SimulationItemStack[] workingStacksCopy() {
        return workingStacks.clone();
    }

    boolean belongsTo(MachineInventoryComponent candidateOwner) {
        return owner == candidateOwner;
    }

    void markCommitted() {
        closed = true;
    }

    private SimulationItemStack insert(
            int slot,
            SimulationItemStack offered,
            boolean requireExternalAccess) {
        ensureOpen();
        Objects.requireNonNull(offered, "offered");
        owner.validateSlotForTransaction(slot);
        if (offered.isEmpty()) {
            return offered;
        }

        SimulationItemStack current = workingStacks[slot];
        long accepted = owner.acceptedQuantity(
                slot,
                current,
                offered,
                requireExternalAccess);
        if (accepted <= 0L) {
            return offered;
        }

        long updatedQuantity = Math.addExact(current.quantity(), accepted);
        workingStacks[slot] = current.isEmpty()
                ? offered.withQuantity(updatedQuantity)
                : current.withQuantity(updatedQuantity);
        insertedQuantity = Math.addExact(insertedQuantity, accepted);
        return offered.withQuantity(offered.quantity() - accepted);
    }

    private SimulationItemStack extract(
            int slot,
            long requestedQuantity,
            boolean requireExternalAccess) {
        ensureOpen();
        owner.requireNonNegativeQuantity(requestedQuantity);
        owner.validateSlotForTransaction(slot);
        if (requestedQuantity == 0L
                || !owner.canExtractCandidate(
                        slot,
                        workingStacks[slot],
                        requestedQuantity,
                        requireExternalAccess)) {
            return SimulationItemStack.empty();
        }

        SimulationItemStack current = workingStacks[slot];
        long extracted = Math.min(requestedQuantity, current.quantity());
        workingStacks[slot] = current.withQuantity(current.quantity() - extracted);
        extractedQuantity = Math.addExact(extractedQuantity, extracted);
        return current.withQuantity(extracted);
    }

    private boolean canExtract(
            int slot,
            long requestedQuantity,
            boolean requireExternalAccess) {
        ensureOpen();
        owner.requireNonNegativeQuantity(requestedQuantity);
        owner.validateSlotForTransaction(slot);
        return owner.canExtractCandidate(
                slot,
                workingStacks[slot],
                requestedQuantity,
                requireExternalAccess);
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("inventory transaction is already closed");
        }
    }
}
