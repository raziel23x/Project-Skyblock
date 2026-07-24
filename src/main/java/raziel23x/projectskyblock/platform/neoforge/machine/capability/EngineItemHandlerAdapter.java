package raziel23x.projectskyblock.platform.neoforge.machine.capability;

import java.util.ConcurrentModificationException;
import java.util.Objects;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import raziel23x.projectskyblock.platform.neoforge.inventory.ItemStackBoundaryException;
import raziel23x.projectskyblock.platform.neoforge.inventory.MinecraftItemStackCodec;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventoryTransaction;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventoryView;

/** NeoForge item capability view over backend-owned machine inventory state. */
public final class EngineItemHandlerAdapter implements IItemHandler {
    private final MachineInventoryView view;
    private final MinecraftItemStackCodec codec;
    private long encodeFailures;
    private long decodeFailures;
    private long transactionConflicts;

    public EngineItemHandlerAdapter(
            MachineInventoryView view,
            MinecraftItemStackCodec codec) {
        this.view = Objects.requireNonNull(view, "view");
        this.codec = Objects.requireNonNull(codec, "codec");
    }

    @Override
    public int getSlots() {
        return view.slotCount();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        try {
            return codec.decode(view.stack(slot));
        } catch (ItemStackBoundaryException exception) {
            decodeFailures = incrementSaturated(decodeFailures);
            return ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        Objects.requireNonNull(stack, "stack");
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        final SimulationItemStack offered;
        try {
            offered = codec.encode(stack);
        } catch (ItemStackBoundaryException exception) {
            encodeFailures = incrementSaturated(encodeFailures);
            return stack;
        }

        try (MachineInventoryTransaction transaction = view.inventory().beginTransaction()) {
            SimulationItemStack remainder = view.insert(transaction, slot, offered);
            if (!simulate && transaction.changed()) {
                transaction.commit();
            }
            return remainder.isEmpty()
                    ? ItemStack.EMPTY
                    : stack.copyWithCount(Math.toIntExact(remainder.quantity()));
        } catch (ConcurrentModificationException exception) {
            transactionConflicts = incrementSaturated(transactionConflicts);
            return stack;
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }

        try (MachineInventoryTransaction transaction = view.inventory().beginTransaction()) {
            SimulationItemStack extracted = view.extract(transaction, slot, amount);
            if (extracted.isEmpty()) {
                return ItemStack.EMPTY;
            }

            ItemStack decoded;
            try {
                decoded = codec.decode(extracted);
            } catch (ItemStackBoundaryException exception) {
                decodeFailures = incrementSaturated(decodeFailures);
                return ItemStack.EMPTY;
            }
            if (!simulate && transaction.changed()) {
                transaction.commit();
            }
            return decoded;
        } catch (ConcurrentModificationException exception) {
            transactionConflicts = incrementSaturated(transactionConflicts);
            return ItemStack.EMPTY;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return (int) Math.min(Integer.MAX_VALUE, view.slotCapacity(slot));
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        Objects.requireNonNull(stack, "stack");
        if (stack.isEmpty()) {
            return false;
        }
        try {
            return view.isItemValid(slot, codec.encode(stack));
        } catch (ItemStackBoundaryException exception) {
            encodeFailures = incrementSaturated(encodeFailures);
            return false;
        }
    }

    public EngineItemHandlerAdapterDiagnostics diagnostics() {
        return new EngineItemHandlerAdapterDiagnostics(
                view.debugName(),
                view.kind(),
                encodeFailures,
                decodeFailures,
                transactionConflicts);
    }

    private static long incrementSaturated(long value) {
        return value == Long.MAX_VALUE ? value : value + 1L;
    }
}
