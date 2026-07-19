package raziel23x.projectskyblock.simulation.machine.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import raziel23x.projectskyblock.simulation.core.DirtyFlag;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;

/**
 * Reusable Minecraft-independent inventory component owned by a simulated machine.
 *
 * <p>The component owns authoritative slot state, enforces slot capacity, item stack
 * limits, access modes, and insertion rules, and reports immutable snapshots suitable
 * for persistence adapters and diagnostics.</p>
 */
public final class MachineInventoryComponent {
    private final List<MachineInventorySlotDefinition> definitions;
    private final SimulationItemStack[] stacks;
    private final DirtyStateTracker dirtyState;
    private final Runnable wakeSignal;
    private long totalInserted;
    private long totalExtracted;
    private long changeCount;

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
        Objects.requireNonNull(offered, "offered");
        validateSlot(slot);
        if (offered.isEmpty()) {
            return true;
        }
        MachineInventorySlotDefinition definition = definitions.get(slot);
        if (!definition.access().acceptsItems() || !definition.accepts(offered.item())) {
            return false;
        }
        SimulationItemStack current = stacks[slot];
        if (!current.isEmpty() && !current.canMerge(offered)) {
            return false;
        }
        return current.quantity() < effectiveCapacity(definition, current, offered);
    }

    public boolean canExtract(int slot, long requestedQuantity) {
        requireNonNegative(requestedQuantity);
        validateSlot(slot);
        return requestedQuantity == 0L
                || (definitions.get(slot).access().providesItems() && !stacks[slot].isEmpty());
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

    /** Restores authoritative slot contents from validated adapter data. */
    public void restore(List<SimulationItemStack> restoredStacks) {
        Objects.requireNonNull(restoredStacks, "restoredStacks");
        if (restoredStacks.size() != stacks.length) {
            throw new IllegalArgumentException("restored slot count must match inventory slot count");
        }
        SimulationItemStack[] validated = new SimulationItemStack[stacks.length];
        for (int slot = 0; slot < stacks.length; slot++) {
            SimulationItemStack restored = Objects.requireNonNull(restoredStacks.get(slot), "restored stack");
            validateRestoredStack(slot, restored);
            validated[slot] = restored;
        }
        if (Arrays.equals(stacks, validated)) {
            return;
        }
        System.arraycopy(validated, 0, stacks, 0, stacks.length);
        markChanged();
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
                snapshot());
    }

    private SimulationItemStack insert(int slot, SimulationItemStack offered, boolean requireExternalAccess) {
        Objects.requireNonNull(offered, "offered");
        validateSlot(slot);
        if (offered.isEmpty()) {
            return offered;
        }
        MachineInventorySlotDefinition definition = definitions.get(slot);
        if ((requireExternalAccess && !definition.access().acceptsItems())
                || !definition.accepts(offered.item())) {
            return offered;
        }
        SimulationItemStack current = stacks[slot];
        if (!current.isEmpty() && !current.canMerge(offered)) {
            return offered;
        }
        long capacity = effectiveCapacity(definition, current, offered);
        long accepted = Math.min(offered.quantity(), capacity - current.quantity());
        if (accepted <= 0L) {
            return offered;
        }
        long updatedQuantity = Math.addExact(current.quantity(), accepted);
        stacks[slot] = current.isEmpty()
                ? offered.withQuantity(updatedQuantity)
                : current.withQuantity(updatedQuantity);
        totalInserted = Math.addExact(totalInserted, accepted);
        markChanged();
        return offered.withQuantity(offered.quantity() - accepted);
    }

    private SimulationItemStack extract(int slot, long requestedQuantity, boolean requireExternalAccess) {
        requireNonNegative(requestedQuantity);
        validateSlot(slot);
        if (requestedQuantity == 0L) {
            return SimulationItemStack.empty();
        }
        MachineInventorySlotDefinition definition = definitions.get(slot);
        if (requireExternalAccess && !definition.access().providesItems()) {
            return SimulationItemStack.empty();
        }
        SimulationItemStack current = stacks[slot];
        if (current.isEmpty()) {
            return SimulationItemStack.empty();
        }
        long extracted = Math.min(requestedQuantity, current.quantity());
        stacks[slot] = current.withQuantity(current.quantity() - extracted);
        totalExtracted = Math.addExact(totalExtracted, extracted);
        markChanged();
        return current.withQuantity(extracted);
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
        changeCount = Math.addExact(changeCount, 1L);
        dirtyState.mark(DirtyFlag.PERSISTENCE, DirtyFlag.CLIENT_SYNC, DirtyFlag.SCHEDULER);
        wakeSignal.run();
    }

    private static void requireNonNegative(long value) {
        if (value < 0L) {
            throw new IllegalArgumentException("requested quantity must be non-negative");
        }
    }
}
