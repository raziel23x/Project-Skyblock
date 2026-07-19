package raziel23x.projectskyblock.simulation.core;

import java.util.Objects;

/**
 * Allocation-free dirty tracking for the small, fixed set of integration side effects.
 *
 * <p>This type records required work; it never performs that work.</p>
 */
public final class DirtyStateTracker {
    private int mask;

    public void mark(DirtyFlag flag) {
        mask |= bit(flag);
    }

    public void mark(DirtyFlag first, DirtyFlag... remaining) {
        mark(first);
        Objects.requireNonNull(remaining, "remaining");
        for (DirtyFlag flag : remaining) {
            mark(flag);
        }
    }

    public boolean isDirty(DirtyFlag flag) {
        return (mask & bit(flag)) != 0;
    }

    public boolean isClean() {
        return mask == 0;
    }

    public int snapshot() {
        return mask;
    }

    public void clear(DirtyFlag flag) {
        mask &= ~bit(flag);
    }

    public void clearMask(int handledMask) {
        mask &= ~handledMask;
    }

    public void clearAll() {
        mask = 0;
    }

    private static int bit(DirtyFlag flag) {
        Objects.requireNonNull(flag, "flag");
        return 1 << flag.ordinal();
    }
}
