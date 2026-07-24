package raziel23x.projectskyblock.simulation.machine.component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;

/**
 * Explicit restricted slot mapping for an automation or player-menu boundary.
 *
 * <p>The view owns no inventory state. All mutations are staged in a caller-owned transaction so
 * a platform adapter may finish conversion and validation before committing authoritative state.</p>
 */
public final class MachineInventoryView {
    private final MachineInventoryComponent inventory;
    private final MachineInventoryViewKind kind;
    private final int[] slots;
    private final boolean allowInsert;
    private final boolean allowExtract;
    private final String debugName;

    private MachineInventoryView(
            MachineInventoryComponent inventory,
            MachineInventoryViewKind kind,
            int[] slots,
            boolean allowInsert,
            boolean allowExtract,
            String debugName) {
        this.inventory = Objects.requireNonNull(inventory, "inventory");
        this.kind = Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(slots, "slots");
        if (slots.length == 0) {
            throw new IllegalArgumentException("inventory view must expose at least one slot");
        }
        this.slots = slots.clone();
        this.allowInsert = allowInsert;
        this.allowExtract = allowExtract;
        if (!allowInsert && !allowExtract) {
            throw new IllegalArgumentException("inventory view must allow insertion or extraction");
        }
        if (debugName == null || debugName.isBlank()) {
            throw new IllegalArgumentException("inventory view debug name must not be blank");
        }
        this.debugName = debugName;

        Set<Integer> uniqueSlots = new HashSet<>();
        for (int slot : this.slots) {
            inventory.definition(slot);
            if (!uniqueSlots.add(slot)) {
                throw new IllegalArgumentException("inventory view contains duplicate slot " + slot);
            }
        }
    }

    public static MachineInventoryView automation(
            MachineInventoryComponent inventory,
            int[] slots,
            boolean allowInsert,
            boolean allowExtract,
            String debugName) {
        return new MachineInventoryView(
                inventory,
                MachineInventoryViewKind.AUTOMATION,
                slots,
                allowInsert,
                allowExtract,
                debugName);
    }

    public static MachineInventoryView menu(
            MachineInventoryComponent inventory,
            int[] slots,
            boolean allowInsert,
            boolean allowExtract,
            String debugName) {
        return new MachineInventoryView(
                inventory,
                MachineInventoryViewKind.MENU,
                slots,
                allowInsert,
                allowExtract,
                debugName);
    }

    public MachineInventoryComponent inventory() {
        return inventory;
    }

    public MachineInventoryViewKind kind() {
        return kind;
    }

    public int slotCount() {
        return slots.length;
    }

    public SimulationItemStack stack(int viewSlot) {
        return inventory.stack(mapSlot(viewSlot));
    }

    public SimulationItemStack stack(MachineInventoryTransaction transaction, int viewSlot) {
        requireTransaction(transaction);
        return transaction.stack(mapSlot(viewSlot));
    }

    public SimulationItemStack insert(
            MachineInventoryTransaction transaction,
            int viewSlot,
            SimulationItemStack offered) {
        requireTransaction(transaction);
        return allowInsert
                ? transaction.insert(mapSlot(viewSlot), offered)
                : Objects.requireNonNull(offered, "offered");
    }

    public SimulationItemStack extract(
            MachineInventoryTransaction transaction,
            int viewSlot,
            long requestedQuantity) {
        requireTransaction(transaction);
        inventory.requireNonNegativeQuantity(requestedQuantity);
        if (!allowExtract) {
            return SimulationItemStack.empty();
        }
        int mappedSlot = mapSlot(viewSlot);
        return kind == MachineInventoryViewKind.MENU
                ? transaction.consume(mappedSlot, requestedQuantity)
                : transaction.extract(mappedSlot, requestedQuantity);
    }

    public boolean canInsert(int viewSlot, SimulationItemStack offered) {
        Objects.requireNonNull(offered, "offered");
        return allowInsert && inventory.canInsert(mapSlot(viewSlot), offered);
    }

    public boolean isItemValid(int viewSlot, SimulationItemStack offered) {
        Objects.requireNonNull(offered, "offered");
        return allowInsert && inventory.acceptsExternalItem(mapSlot(viewSlot), offered);
    }

    public boolean canExtract(int viewSlot, long requestedQuantity) {
        inventory.requireNonNegativeQuantity(requestedQuantity);
        if (!allowExtract) {
            return false;
        }
        int mappedSlot = mapSlot(viewSlot);
        return kind == MachineInventoryViewKind.MENU
                ? inventory.canConsume(mappedSlot, requestedQuantity)
                : inventory.canExtract(mappedSlot, requestedQuantity);
    }

    public long slotCapacity(int viewSlot) {
        return inventory.definition(mapSlot(viewSlot)).capacity();
    }

    public String debugName() {
        return debugName;
    }

    private int mapSlot(int viewSlot) {
        if (viewSlot < 0 || viewSlot >= slots.length) {
            throw new IndexOutOfBoundsException(
                    debugName + " view slot " + viewSlot
                            + " outside valid range [0," + slots.length + ")");
        }
        return slots[viewSlot];
    }

    private void requireTransaction(MachineInventoryTransaction transaction) {
        Objects.requireNonNull(transaction, "transaction");
        if (!transaction.belongsTo(inventory)) {
            throw new IllegalArgumentException("inventory transaction belongs to another component");
        }
    }
}
