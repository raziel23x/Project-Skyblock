package raziel23x.projectskyblock.simulation.machine.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;
import raziel23x.projectskyblock.simulation.core.DirtyFlag;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;

/**
 * Reusable Minecraft-independent inventory component owned by a simulated machine.
 *
 * <p>The component owns authoritative slot state, enforces slot capacity, item stack limits,
 * access modes, and insertion rules, and reports immutable snapshots suitable for persistence
 * adapters and diagnostics. Multi-slot changes may be staged and committed atomically through
 * {@link MachineInventoryTransaction}.</p>
 */
public final class MachineInventoryComponent {
    private final List<MachineInventorySlotDefinition> definitions;
    private final SimulationItemStack[] stacks;
    private final DirtyStateTracker dirtyState;
    private final Runnable wakeSignal;
    private long totalInserted;
    private long totalExtracted;
    private long changeCount;
    private long stateVersion;
    private long transactionCommitCount;
    private long transactionConflictCount;

    public MachineInventoryComponent(List<MachineInventorySlotDefinition> definitions) {
        this(definitions, new DirtyStateTracker(), () -> { });
    }

    public MachineInventoryComponent(
            List<MachineInventorySlotDefinition> definitions,
            DirtyStateTracker dirtyState,
            Runnable wakeSignal) {
        Objects.requireNonNull(definitions, "definitions");
        if (definitions.isEmpty()) {
            throw new IllegalArgumentException("inventory must contain at least one slot");
        }
        this.definitions = List.copyOf(definitions);
        this.definitions.forEach(definition -> Objects.requireNonNull(definition, "slot definition"));
        this.stacks = new SimulationItemStack[definitions.size()];
        Arrays.fill(this.stacks, SimulationItemStack.empty());
        this.dirtyState = Objects.requireNonNull(dirtyState, "dirtyState");
        this.wakeSignal = Objects.requireNonNull(wakeSignal, "wakeSignal");
    }

    public int slotCount() {
        return stacks.length;
    }

    public MachineInventorySlotDefinition definition(int slot) {
        validateSlot(slot);
        return definitions.get(slot);
    }

    public SimulationItemStack stack(int slot) {
        validateSlot(slot);
        return stacks[slot];
    }

    public DirtyStateTracker dirtyState() {
        return dirtyState;
    }

    /** Opens an isolated candidate over the current authoritative slot state. */
    public synchronized MachineInventoryTransaction beginTransaction() {
        return new MachineInventoryTransaction(this, stateVersion, stacks);
    }

    public SimulationItemStack insert(int slot, SimulationItemStack offered) {
        try (MachineInventoryTransaction transaction = beginTransaction()) {
            SimulationItemStack remainder = transaction.insert(slot, offered);
            if (transaction.changed()) {
                transaction.commit();
            }
            return remainder;
        }
    }

    public SimulationItemStack store(int slot, SimulationItemStack offered) {
        try (MachineInventoryTransaction transaction = beginTransaction()) {
            SimulationItemStack remainder = transaction.store(slot, offered);
            if (transaction.changed()) {
                transaction.commit();
            }
            return remainder;
        }
    }

    public SimulationItemStack extract(int slot, long requestedQuantity) {
        try (MachineInventoryTransaction transaction = beginTransaction()) {
            SimulationItemStack extracted = transaction.extract(slot, requestedQuantity);
            if (transaction.changed()) {
                transaction.commit();
            }
            return extracted;
        }
    }

    public SimulationItemStack consume(int slot, long requestedQuantity) {
        try (MachineInventoryTransaction transaction = beginTransaction()) {
            SimulationItemStack extracted = transaction.consume(slot, requestedQuantity);
            if (transaction.changed()) {
                transaction.commit();
            }
            return extracted;
        }
    }

    public boolean canInsert(int slot, SimulationItemStack offered) {
        Objects.requireNonNull(offered, "offered");
        validateSlot(slot);
        return canInsertCandidate(slot, stacks[slot], offered, true);
    }

    public boolean acceptsExternalItem(int slot, SimulationItemStack offered) {
        Objects.requireNonNull(offered, "offered");
        validateSlot(slot);
        if (offered.isEmpty()) {
            return false;
        }
        MachineInventorySlotDefinition definition = definitions.get(slot);
        return definition.access().acceptsItems() && definition.accepts(offered.item());
    }

    public boolean canStore(int slot, SimulationItemStack offered) {
        Objects.requireNonNull(offered, "offered");
        validateSlot(slot);
        return canInsertCandidate(slot, stacks[slot], offered, false);
    }

    public boolean canExtract(int slot, long requestedQuantity) {
        requireNonNegativeQuantity(requestedQuantity);
        validateSlot(slot);
        return canExtractCandidate(slot, stacks[slot], requestedQuantity, true);
    }

    public boolean canConsume(int slot, long requestedQuantity) {
        requireNonNegativeQuantity(requestedQuantity);
        validateSlot(slot);
        return canExtractCandidate(slot, stacks[slot], requestedQuantity, false);
    }

    public List<MachineInventorySlotSnapshot> snapshot() {
        List<MachineInventorySlotSnapshot> snapshots = new ArrayList<>(stacks.length);
        for (int slot = 0; slot < stacks.length; slot++) {
            MachineInventorySlotDefinition definition = definitions.get(slot);
            snapshots.add(new MachineInventorySlotSnapshot(
                    slot,
                    definition.capacity(),
                    definition.access(),
                    stacks[slot]));
        }
        return List.copyOf(snapshots);
    }

    /** Validates a complete persistence candidate without mutating authoritative state. */
    public void validateRestore(List<SimulationItemStack> restoredStacks) {
        Objects.requireNonNull(restoredStacks, "restoredStacks");
        if (restoredStacks.size() != stacks.length) {
            throw new IllegalArgumentException("restored slot count must match inventory slot count");
        }
        for (int slot = 0; slot < stacks.length; slot++) {
            SimulationItemStack restored = Objects.requireNonNull(restoredStacks.get(slot), "restored stack");
            validateRestoredStack(slot, restored);
        }
    }

    /** Restores authoritative slot contents after complete transaction validation. */
    public synchronized void restore(List<SimulationItemStack> restoredStacks) {
        validateRestore(restoredStacks);
        SimulationItemStack[] restored = restoredStacks.toArray(SimulationItemStack[]::new);
        if (Arrays.equals(stacks, restored)) {
            return;
        }
        long restoredVersion = Math.addExact(stateVersion, 1L);
        System.arraycopy(restored, 0, stacks, 0, stacks.length);
        stateVersion = restoredVersion;
        dirtyState.mark(DirtyFlag.CLIENT_SYNC);
    }

    public MachineInventoryDiagnostics diagnostics() {
        int occupied = 0;
        long totalQuantity = 0L;
        for (SimulationItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                occupied++;
                totalQuantity = Math.addExact(totalQuantity, stack.quantity());
            }
        }
        return new MachineInventoryDiagnostics(
                stacks.length,
                occupied,
                totalQuantity,
                totalInserted,
                totalExtracted,
                changeCount,
                stateVersion,
                transactionCommitCount,
                transactionConflictCount,
                snapshot());
    }

    synchronized MachineInventoryCommitResult commitTransaction(
            MachineInventoryTransaction transaction) {
        Objects.requireNonNull(transaction, "transaction");
        if (!transaction.belongsTo(this)) {
            throw new IllegalArgumentException("inventory transaction belongs to another component");
        }
        if (transaction.baseVersion() != stateVersion) {
            transactionConflictCount = Math.addExact(transactionConflictCount, 1L);
            transaction.markCommitted();
            throw new ConcurrentModificationException(
                    "inventory transaction is stale: expected version "
                            + transaction.baseVersion() + " but found " + stateVersion);
        }

        SimulationItemStack[] candidate = transaction.workingStacksCopy();
        validateCandidate(candidate);
        if (Arrays.equals(stacks, candidate)) {
            transaction.markCommitted();
            return new MachineInventoryCommitResult(false, 0L, 0L, stateVersion);
        }

        long insertedQuantity = transaction.insertedQuantity();
        long extractedQuantity = transaction.extractedQuantity();
        long committedTotalInserted = Math.addExact(totalInserted, insertedQuantity);
        long committedTotalExtracted = Math.addExact(totalExtracted, extractedQuantity);
        long committedChangeCount = Math.addExact(changeCount, 1L);
        long committedStateVersion = Math.addExact(stateVersion, 1L);
        long committedTransactionCount = Math.addExact(transactionCommitCount, 1L);

        System.arraycopy(candidate, 0, stacks, 0, stacks.length);
        totalInserted = committedTotalInserted;
        totalExtracted = committedTotalExtracted;
        changeCount = committedChangeCount;
        stateVersion = committedStateVersion;
        transactionCommitCount = committedTransactionCount;
        transaction.markCommitted();
        markChanged();
        return new MachineInventoryCommitResult(
                true,
                insertedQuantity,
                extractedQuantity,
                stateVersion);
    }

    boolean canInsertCandidate(
            int slot,
            SimulationItemStack current,
            SimulationItemStack offered,
            boolean requireExternalAccess) {
        Objects.requireNonNull(current, "current");
        Objects.requireNonNull(offered, "offered");
        if (offered.isEmpty()) {
            return true;
        }
        MachineInventorySlotDefinition definition = definitions.get(slot);
        if ((requireExternalAccess && !definition.access().acceptsItems())
                || !definition.accepts(offered.item())) {
            return false;
        }
        if (!current.isEmpty() && !current.canMerge(offered)) {
            return false;
        }
        return current.quantity() < effectiveCapacity(definition, current, offered);
    }

    long acceptedQuantity(
            int slot,
            SimulationItemStack current,
            SimulationItemStack offered,
            boolean requireExternalAccess) {
        if (!canInsertCandidate(slot, current, offered, requireExternalAccess)) {
            return 0L;
        }
        long capacity = effectiveCapacity(definitions.get(slot), current, offered);
        return Math.min(offered.quantity(), capacity - current.quantity());
    }

    boolean canExtractCandidate(
            int slot,
            SimulationItemStack current,
            long requestedQuantity,
            boolean requireExternalAccess) {
        Objects.requireNonNull(current, "current");
        requireNonNegativeQuantity(requestedQuantity);
        return requestedQuantity == 0L
                || ((!requireExternalAccess || definitions.get(slot).access().providesItems())
                && !current.isEmpty());
    }

    void validateSlotForTransaction(int slot) {
        validateSlot(slot);
    }

    void requireNonNegativeQuantity(long value) {
        if (value < 0L) {
            throw new IllegalArgumentException("requested quantity must be non-negative");
        }
    }

    private void validateCandidate(SimulationItemStack[] candidate) {
        if (candidate.length != stacks.length) {
            throw new IllegalArgumentException("candidate slot count must match inventory slot count");
        }
        for (int slot = 0; slot < candidate.length; slot++) {
            validateRestoredStack(slot, Objects.requireNonNull(candidate[slot], "candidate stack"));
        }
    }

    private void validateRestoredStack(int slot, SimulationItemStack restored) {
        if (restored.isEmpty()) {
            return;
        }
        MachineInventorySlotDefinition definition = definitions.get(slot);
        if (!definition.accepts(restored.item())) {
            throw new IllegalArgumentException("restored stack is rejected by slot " + slot);
        }
        if (restored.quantity() > effectiveCapacity(definition, SimulationItemStack.empty(), restored)) {
            throw new IllegalArgumentException("restored stack exceeds slot " + slot + " capacity");
        }
    }

    private static long effectiveCapacity(
            MachineInventorySlotDefinition definition,
            SimulationItemStack current,
            SimulationItemStack offered) {
        long capacity = Math.min(definition.capacity(), offered.maximumStackSize());
        return current.isEmpty() ? capacity : Math.min(capacity, current.maximumStackSize());
    }

    private void validateSlot(int slot) {
        if (slot < 0 || slot >= stacks.length) {
            throw new IndexOutOfBoundsException("slot " + slot + " outside inventory size " + stacks.length);
        }
    }

    private void markChanged() {
        dirtyState.mark(DirtyFlag.PERSISTENCE, DirtyFlag.CLIENT_SYNC, DirtyFlag.SCHEDULER);
        wakeSignal.run();
    }
}
