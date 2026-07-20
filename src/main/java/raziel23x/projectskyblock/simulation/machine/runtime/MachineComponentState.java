package raziel23x.projectskyblock.simulation.machine.runtime;

import java.util.Objects;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;
import raziel23x.projectskyblock.simulation.core.SimulationState;
import raziel23x.projectskyblock.simulation.machine.component.MachineEnergyComponent;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventoryComponent;
import raziel23x.projectskyblock.simulation.machine.component.MachineProcessingComponent;
import raziel23x.projectskyblock.simulation.machine.component.MachineThermalComponent;

/**
 * Authoritative composed state for a machine that owns energy, inventory, thermal, and processing resources.
 *
 * <p>All components must share one dirty tracker. This keeps persistence, client-sync,
 * and scheduler work represented by one allocation-free state boundary.</p>
 */
public final class MachineComponentState implements SimulationState {
    private final DirtyStateTracker dirtyState;
    private final MachineEnergyComponent energy;
    private final MachineInventoryComponent inventory;
    private final MachineThermalComponent thermal;
    private final MachineProcessingComponent processing;

    public MachineComponentState(
            DirtyStateTracker dirtyState,
            MachineEnergyComponent energy,
            MachineInventoryComponent inventory,
            MachineThermalComponent thermal,
            MachineProcessingComponent processing) {
        this.dirtyState = Objects.requireNonNull(dirtyState, "dirtyState");
        this.energy = requireSharedTracker(Objects.requireNonNull(energy, "energy"), energy.dirtyState());
        this.inventory = requireSharedTracker(
                Objects.requireNonNull(inventory, "inventory"), inventory.dirtyState());
        this.thermal = requireSharedTracker(
                Objects.requireNonNull(thermal, "thermal"), thermal.dirtyState());
        this.processing = requireSharedTracker(
                Objects.requireNonNull(processing, "processing"), processing.dirtyState());
    }

    public DirtyStateTracker dirtyState() {
        return dirtyState;
    }

    public MachineEnergyComponent energy() {
        return energy;
    }

    public MachineInventoryComponent inventory() {
        return inventory;
    }

    public MachineThermalComponent thermal() {
        return thermal;
    }

    public MachineProcessingComponent processing() {
        return processing;
    }

    public MachineComponentDiagnostics diagnostics() {
        return new MachineComponentDiagnostics(
                dirtyState.snapshot(),
                energy.diagnostics(),
                inventory.diagnostics(),
                thermal.diagnostics(),
                processing.diagnostics());
    }

    private <T> T requireSharedTracker(T component, DirtyStateTracker componentTracker) {
        if (componentTracker != dirtyState) {
            throw new IllegalArgumentException("all machine components must share the composed dirty tracker");
        }
        return component;
    }
}
