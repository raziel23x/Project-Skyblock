package raziel23x.projectskyblock.simulation.machine.component;

import java.util.Objects;
import raziel23x.projectskyblock.simulation.core.DirtyFlag;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;

/**
 * Minecraft-independent durable burn reservoir owned by a simulated machine.
 *
 * <p>The component stores only normalized burn work units. Minecraft fuel lookup, item
 * consumption, container remainders, and power-source selection remain machine-logic or platform
 * adapter concerns. Combustion changes occur as part of an already-running machine execution, so
 * they dirty persistence and client synchronization without issuing redundant scheduler wakes.</p>
 */
public final class MachineCombustionComponent {
    private final DirtyStateTracker dirtyState;
    private long remainingBurnUnits;
    private long totalBurnUnits;
    private long totalUnitsConsumed;
    private long ignitionCount;
    private long changeCount;

    public MachineCombustionComponent() {
        this(new DirtyStateTracker());
    }

    public MachineCombustionComponent(DirtyStateTracker dirtyState) {
        this.dirtyState = Objects.requireNonNull(dirtyState, "dirtyState");
    }

    public DirtyStateTracker dirtyState() {
        return dirtyState;
    }

    public long remainingBurnUnits() {
        return remainingBurnUnits;
    }

    public long totalBurnUnits() {
        return totalBurnUnits;
    }

    public boolean burning() {
        return remainingBurnUnits > 0L;
    }

    /** Starts a new burn cycle only when the current reservoir is empty. */
    public void ignite(long burnUnits) {
        if (burnUnits <= 0L) {
            throw new IllegalArgumentException("burn units must be positive");
        }
        if (burning()) {
            throw new IllegalStateException("combustion reservoir is already burning");
        }
        remainingBurnUnits = burnUnits;
        totalBurnUnits = burnUnits;
        ignitionCount = Math.addExact(ignitionCount, 1L);
        markChanged();
    }

    /** Consumes up to the requested burn units and returns the amount actually consumed. */
    public long consume(long requestedUnits) {
        if (requestedUnits < 0L) {
            throw new IllegalArgumentException("requested burn units must be non-negative");
        }
        if (requestedUnits == 0L || remainingBurnUnits == 0L) {
            return 0L;
        }
        long consumed = Math.min(requestedUnits, remainingBurnUnits);
        remainingBurnUnits -= consumed;
        totalUnitsConsumed = Math.addExact(totalUnitsConsumed, consumed);
        markChanged();
        return consumed;
    }

    /** Clears an active burn cycle without changing historical diagnostics. */
    public void clear() {
        if (remainingBurnUnits == 0L && totalBurnUnits == 0L) {
            return;
        }
        remainingBurnUnits = 0L;
        totalBurnUnits = 0L;
        markChanged();
    }

    /** Restores validated durable state without replaying runtime work. */
    public void restore(long remainingBurnUnits, long totalBurnUnits) {
        validateRestore(remainingBurnUnits, totalBurnUnits);
        if (this.remainingBurnUnits == remainingBurnUnits && this.totalBurnUnits == totalBurnUnits) {
            return;
        }
        this.remainingBurnUnits = remainingBurnUnits;
        this.totalBurnUnits = totalBurnUnits;
        dirtyState.mark(DirtyFlag.CLIENT_SYNC);
    }

    public MachineCombustionDiagnostics diagnostics() {
        return new MachineCombustionDiagnostics(
                remainingBurnUnits,
                totalBurnUnits,
                totalUnitsConsumed,
                ignitionCount,
                changeCount);
    }

    public static void validateRestore(long remainingBurnUnits, long totalBurnUnits) {
        if (remainingBurnUnits < 0L || totalBurnUnits < 0L) {
            throw new IllegalArgumentException("combustion values must be non-negative");
        }
        if (remainingBurnUnits > totalBurnUnits) {
            throw new IllegalArgumentException("remaining burn units cannot exceed total burn units");
        }
        if (totalBurnUnits == 0L && remainingBurnUnits != 0L) {
            throw new IllegalArgumentException("empty combustion state cannot retain burn units");
        }
    }

    private void markChanged() {
        changeCount = Math.addExact(changeCount, 1L);
        dirtyState.mark(DirtyFlag.PERSISTENCE, DirtyFlag.CLIENT_SYNC);
    }
}
