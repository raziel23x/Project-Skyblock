package raziel23x.projectskyblock.simulation.machine.component;

import java.util.Objects;
import raziel23x.projectskyblock.simulation.core.DirtyFlag;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;
import raziel23x.projectskyblock.simulation.thermal.ThermalCondition;
import raziel23x.projectskyblock.simulation.thermal.ThermalDiagnostics;
import raziel23x.projectskyblock.simulation.thermal.ThermalEngine;
import raziel23x.projectskyblock.simulation.thermal.ThermalEnvironment;
import raziel23x.projectskyblock.simulation.thermal.ThermalProperties;
import raziel23x.projectskyblock.simulation.thermal.ThermalState;

/**
 * Reusable Minecraft-independent thermal component owned by a simulated machine.
 *
 * <p>The component delegates authoritative energy and temperature storage to
 * {@link ThermalState}, operating-range interpretation to {@link ThermalProperties},
 * and ambient exchange to {@link ThermalEngine}. It adds machine-facing access rules,
 * restoration, diagnostics, dirty-state signaling, and wake signaling.</p>
 */
public final class MachineThermalComponent {
    private final ThermalState thermalState;
    private final ThermalProperties properties;
    private final DirtyStateTracker dirtyState;
    private final Runnable wakeSignal;
    private MachineThermalAccess access;
    private long totalHeatAddedMicroJoules;
    private long totalHeatRemovedMicroJoules;
    private long netEnvironmentalHeatMicroJoules;
    private long changeCount;

    public MachineThermalComponent(
            ThermalState thermalState,
            ThermalProperties properties,
            MachineThermalAccess access) {
        this(thermalState, properties, access, new DirtyStateTracker(), () -> { });
    }

    public MachineThermalComponent(
            ThermalState thermalState,
            ThermalProperties properties,
            MachineThermalAccess access,
            DirtyStateTracker dirtyState,
            Runnable wakeSignal) {
        this.thermalState = Objects.requireNonNull(thermalState, "thermalState");
        this.properties = Objects.requireNonNull(properties, "properties");
        this.access = Objects.requireNonNull(access, "access");
        this.dirtyState = Objects.requireNonNull(dirtyState, "dirtyState");
        this.wakeSignal = Objects.requireNonNull(wakeSignal, "wakeSignal");
    }

    public ThermalState authoritativeState() {
        return thermalState;
    }

    public ThermalProperties properties() {
        return properties;
    }

    public MachineThermalAccess access() {
        return access;
    }

    public void setAccess(MachineThermalAccess access) {
        MachineThermalAccess requested = Objects.requireNonNull(access, "access");
        if (this.access == requested) {
            return;
        }
        this.access = requested;
        markChanged();
    }

    public DirtyStateTracker dirtyState() {
        return dirtyState;
    }

    public long temperatureMilliKelvin() {
        return thermalState.temperatureMilliKelvin();
    }

    public long thermalEnergyMicroJoules() {
        return thermalState.thermalEnergyMicroJoules();
    }

    public long heatCapacityMicroJoulesPerMilliKelvin() {
        return thermalState.heatCapacityMicroJoulesPerMilliKelvin();
    }

    /** Restores exact durable thermal energy without replaying runtime heat transfer. */
    public void restoreThermalEnergyMicroJoules(long thermalEnergyMicroJoules) {
        if (thermalEnergyMicroJoules < 0L) {
            throw new IllegalArgumentException("thermal energy must be non-negative");
        }
        long current = thermalState.thermalEnergyMicroJoules();
        if (thermalEnergyMicroJoules > current) {
            thermalState.addHeatMicroJoules(thermalEnergyMicroJoules - current);
        } else if (thermalEnergyMicroJoules < current) {
            thermalState.removeHeatMicroJoules(current - thermalEnergyMicroJoules);
        }
        if (thermalEnergyMicroJoules != current) {
            changeCount = Math.addExact(changeCount, 1L);
            dirtyState.mark(DirtyFlag.CLIENT_SYNC);
        }
    }

    public ThermalCondition condition() {
        return properties.condition(thermalState);
    }

    public boolean isWithinOperatingRange() {
        return properties.isWithinOperatingRange(thermalState);
    }

    public boolean requiresShutdown() {
        return properties.requiresShutdown(thermalState);
    }

    /** Accepts heat from an external network or adapter when input is permitted. */
    public long receiveHeat(long requestedMicroJoules) {
        requireNonNegative(requestedMicroJoules);
        if (!access.acceptsHeat()) {
            return 0L;
        }
        return addHeat(requestedMicroJoules);
    }

    /** Provides heat to an external network or adapter when output is permitted. */
    public long extractHeat(long requestedMicroJoules) {
        requireNonNegative(requestedMicroJoules);
        if (!access.providesHeat()) {
            return 0L;
        }
        return removeHeat(requestedMicroJoules);
    }

    /** Adds internally generated process heat without requiring external input access. */
    public long generateHeat(long requestedMicroJoules) {
        requireNonNegative(requestedMicroJoules);
        return addHeat(requestedMicroJoules);
    }

    /** Removes internally consumed or actively cooled heat without requiring external output access. */
    public long consumeHeat(long requestedMicroJoules) {
        requireNonNegative(requestedMicroJoules);
        return removeHeat(requestedMicroJoules);
    }

    /**
     * Applies generated process heat and one bounded ambient exchange step.
     * A single meaningful step produces one dirty/wake signal.
     */
    public ThermalDiagnostics step(ThermalEnvironment environment, long generatedHeatMicroJoules) {
        Objects.requireNonNull(environment, "environment");
        requireNonNegative(generatedHeatMicroJoules);

        long beforeEnergy = thermalEnergyMicroJoules();
        ThermalDiagnostics result = ThermalEngine.step(
                thermalState,
                properties,
                environment,
                generatedHeatMicroJoules);

        if (generatedHeatMicroJoules > 0L) {
            totalHeatAddedMicroJoules = Math.addExact(totalHeatAddedMicroJoules, generatedHeatMicroJoules);
        }
        long environmentalHeat = result.environmentalHeatMicroJoules();
        netEnvironmentalHeatMicroJoules = Math.addExact(netEnvironmentalHeatMicroJoules, environmentalHeat);
        if (environmentalHeat > 0L) {
            totalHeatAddedMicroJoules = Math.addExact(totalHeatAddedMicroJoules, environmentalHeat);
        } else if (environmentalHeat < 0L) {
            totalHeatRemovedMicroJoules = Math.addExact(totalHeatRemovedMicroJoules, -environmentalHeat);
        }

        if (thermalEnergyMicroJoules() != beforeEnergy) {
            markChanged();
        }
        return result;
    }

    /** Applies one bounded ambient exchange step with no internally generated heat. */
    public long exchangeWithEnvironment(ThermalEnvironment environment) {
        Objects.requireNonNull(environment, "environment");
        long exchanged = ThermalEngine.exchangeWithEnvironment(thermalState, properties, environment);
        if (exchanged == 0L) {
            return 0L;
        }
        netEnvironmentalHeatMicroJoules = Math.addExact(netEnvironmentalHeatMicroJoules, exchanged);
        if (exchanged > 0L) {
            totalHeatAddedMicroJoules = Math.addExact(totalHeatAddedMicroJoules, exchanged);
        } else {
            totalHeatRemovedMicroJoules = Math.addExact(totalHeatRemovedMicroJoules, -exchanged);
        }
        markChanged();
        return exchanged;
    }

    public MachineThermalSnapshot snapshot() {
        return new MachineThermalSnapshot(
                access,
                temperatureMilliKelvin(),
                thermalEnergyMicroJoules(),
                heatCapacityMicroJoulesPerMilliKelvin(),
                properties,
                condition());
    }

    /** Restores validated authoritative thermal energy from a persistence adapter. */
    public void restoreThermalEnergy(long restoredThermalEnergyMicroJoules) {
        requireNonNegative(restoredThermalEnergyMicroJoules);
        long current = thermalEnergyMicroJoules();
        if (current == restoredThermalEnergyMicroJoules) {
            return;
        }
        if (restoredThermalEnergyMicroJoules > current) {
            thermalState.addHeatMicroJoules(restoredThermalEnergyMicroJoules - current);
        } else {
            thermalState.removeHeatMicroJoules(current - restoredThermalEnergyMicroJoules);
        }
        markChanged();
    }

    public MachineThermalDiagnostics diagnostics() {
        return new MachineThermalDiagnostics(
                access,
                temperatureMilliKelvin(),
                thermalEnergyMicroJoules(),
                heatCapacityMicroJoulesPerMilliKelvin(),
                condition(),
                totalHeatAddedMicroJoules,
                totalHeatRemovedMicroJoules,
                netEnvironmentalHeatMicroJoules,
                changeCount);
    }

    private long addHeat(long requestedMicroJoules) {
        if (requestedMicroJoules == 0L) {
            return 0L;
        }
        long accepted = thermalState.addHeatMicroJoules(requestedMicroJoules);
        totalHeatAddedMicroJoules = Math.addExact(totalHeatAddedMicroJoules, accepted);
        markChanged();
        return accepted;
    }

    private long removeHeat(long requestedMicroJoules) {
        if (requestedMicroJoules == 0L) {
            return 0L;
        }
        long removed = thermalState.removeHeatMicroJoules(requestedMicroJoules);
        if (removed > 0L) {
            totalHeatRemovedMicroJoules = Math.addExact(totalHeatRemovedMicroJoules, removed);
            markChanged();
        }
        return removed;
    }

    private void markChanged() {
        changeCount = Math.addExact(changeCount, 1L);
        dirtyState.mark(DirtyFlag.PERSISTENCE, DirtyFlag.CLIENT_SYNC, DirtyFlag.SCHEDULER);
        wakeSignal.run();
    }

    private static void requireNonNegative(long value) {
        if (value < 0L) {
            throw new IllegalArgumentException("heat must be non-negative");
        }
    }
}
