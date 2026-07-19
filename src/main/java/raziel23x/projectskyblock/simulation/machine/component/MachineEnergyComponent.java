package raziel23x.projectskyblock.simulation.machine.component;

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
 * {@link SimulationEnergyState}. It adds machine-facing access rules, dirty-state
 * signaling, wake signaling, and diagnostics without introducing Forge Energy or
 * BlockEntity dependencies.</p>
 */
public final class MachineEnergyComponent implements EnergyBuffer {
    private final SimulationEnergyState energy;
    private final DirtyStateTracker dirtyState;
    private final Runnable wakeSignal;
    private MachineEnergyAccess access;
    private long totalReceived;
    private long totalExtracted;
    private long changeCount;

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

    public void setAccess(MachineEnergyAccess access) {
        MachineEnergyAccess requested = Objects.requireNonNull(access, "access");
        if (this.access == requested) {
            return;
        }
        this.access = requested;
        markChanged();
    }

    public DirtyStateTracker dirtyState() {
        return dirtyState;
    }

    public SimulationEnergyState authoritativeState() {
        return energy;
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
        if (!access.acceptsEnergy()) {
            return 0;
        }
        long accepted = energy.receive(requestedEnergy);
        if (accepted > 0) {
            totalReceived = Math.addExact(totalReceived, accepted);
            markChanged();
        }
        return accepted;
    }

    /** Extracts energy to an external network or adapter when output is permitted. */
    @Override
    public long extract(long requestedEnergy) {
        requireNonNegative(requestedEnergy);
        if (!access.providesEnergy()) {
            return 0;
        }
        long extracted = energy.extract(requestedEnergy);
        if (extracted > 0) {
            totalExtracted = Math.addExact(totalExtracted, extracted);
            markChanged();
        }
        return extracted;
    }

    /**
     * Consumes stored energy for the owning machine's internal processing.
     * External output access is intentionally not required.
     */
    public long consume(long requestedEnergy) {
        requireNonNegative(requestedEnergy);
        long consumed = energy.extract(requestedEnergy);
        if (consumed > 0) {
            totalExtracted = Math.addExact(totalExtracted, consumed);
            markChanged();
        }
        return consumed;
    }

    /**
     * Produces energy internally for the owning machine.
     * External input access is intentionally not required.
     */
    public long produce(long requestedEnergy) {
        requireNonNegative(requestedEnergy);
        long produced = energy.receive(requestedEnergy);
        if (produced > 0) {
            totalReceived = Math.addExact(totalReceived, produced);
            markChanged();
        }
        return produced;
    }

    public boolean canConsume(long requestedEnergy) {
        requireNonNegative(requestedEnergy);
        return requestedEnergy <= storedEnergy()
                && requestedEnergy <= limits().maximumExtractPerTick();
    }

    public MachineEnergyDiagnostics diagnostics() {
        return new MachineEnergyDiagnostics(
                access,
                storedEnergy(),
                capacity(),
                availableCapacity(),
                limits().maximumReceivePerTick(),
                limits().maximumExtractPerTick(),
                totalReceived,
                totalExtracted,
                changeCount);
    }

    private void markChanged() {
        changeCount = Math.addExact(changeCount, 1L);
        dirtyState.mark(DirtyFlag.PERSISTENCE, DirtyFlag.CLIENT_SYNC, DirtyFlag.SCHEDULER);
        wakeSignal.run();
    }

    private static void requireNonNegative(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("requested energy must be non-negative");
        }
    }
}
