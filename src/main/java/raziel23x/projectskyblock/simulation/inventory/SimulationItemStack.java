package raziel23x.projectskyblock.simulation.inventory;

import java.util.Objects;

/** Immutable Minecraft-independent item quantity. */
public record SimulationItemStack(SimulationItemKey item, long quantity, long maximumStackSize) {
    private static final SimulationItemStack EMPTY = new SimulationItemStack(null, 0L, 0L);

    public SimulationItemStack {
        validate(item, quantity, maximumStackSize);
    }

    public static SimulationItemStack empty() {
        return EMPTY;
    }

    public static SimulationItemStack of(SimulationItemKey item, long quantity, long maximumStackSize) {
        return new SimulationItemStack(item, quantity, maximumStackSize);
    }

    public boolean isEmpty() {
        return quantity == 0L;
    }

    public SimulationItemStack withQuantity(long requestedQuantity) {
        if (requestedQuantity < 0 || requestedQuantity > maximumStackSize) {
            throw new IllegalArgumentException("quantity must be between zero and maximum stack size");
        }
        return requestedQuantity == 0L ? empty() : new SimulationItemStack(item, requestedQuantity, maximumStackSize);
    }

    public boolean canMerge(SimulationItemStack other) {
        Objects.requireNonNull(other, "other");
        return !isEmpty() && !other.isEmpty() && item.equals(other.item);
    }

    private static void validate(SimulationItemKey item, long quantity, long maximumStackSize) {
        if (quantity < 0 || maximumStackSize < 0) {
            throw new IllegalArgumentException("stack values must be non-negative");
        }
        if (quantity == 0L) {
            if (item != null || maximumStackSize != 0L) {
                throw new IllegalArgumentException("empty stacks must use null item and zero maximum stack size");
            }
            return;
        }
        Objects.requireNonNull(item, "item");
        if (maximumStackSize == 0L || quantity > maximumStackSize) {
            throw new IllegalArgumentException("non-empty stack quantity must not exceed maximum stack size");
        }
    }
}
