package raziel23x.projectskyblock.simulation.machine.runtime;

import java.util.List;
import java.util.Objects;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;
import raziel23x.projectskyblock.simulation.core.SimulationScheduler;
import raziel23x.projectskyblock.simulation.energy.SimulationEnergyState;
import raziel23x.projectskyblock.simulation.machine.MachineId;
import raziel23x.projectskyblock.simulation.machine.MachineLogic;
import raziel23x.projectskyblock.simulation.machine.MachineParticipant;
import raziel23x.projectskyblock.simulation.machine.MachineState;
import raziel23x.projectskyblock.simulation.machine.component.MachineEnergyAccess;
import raziel23x.projectskyblock.simulation.machine.component.MachineEnergyComponent;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventoryComponent;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventorySlotDefinition;
import raziel23x.projectskyblock.simulation.machine.component.MachineProcessingComponent;
import raziel23x.projectskyblock.simulation.machine.component.MachineThermalAccess;
import raziel23x.projectskyblock.simulation.machine.component.MachineThermalComponent;
import raziel23x.projectskyblock.simulation.thermal.ThermalProperties;
import raziel23x.projectskyblock.simulation.thermal.ThermalState;

/**
 * Registered composition root for one Minecraft-independent simulated machine.
 *
 * <p>The runtime wires one shared dirty tracker and one coalesced scheduler wake signal
 * through all owned components. It does not contain recipes, world access, persistence,
 * networking, menus, or rendering.</p>
 */
public final class MachineRuntime implements AutoCloseable {
    private final SimulationScheduler scheduler;
    private final MachineId id;
    private final DirtyStateTracker dirtyState;
    private final MachineComponentState components;
    private final MachineParticipant<MachineComponentState> participant;
    private boolean registered = true;

    public MachineRuntime(
            SimulationScheduler scheduler,
            MachineId id,
            SimulationEnergyState energyState,
            MachineEnergyAccess energyAccess,
            List<MachineInventorySlotDefinition> inventorySlots,
            ThermalState thermalState,
            ThermalProperties thermalProperties,
            MachineThermalAccess thermalAccess,
            MachineLogic<MachineComponentState> logic) {
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
        this.id = Objects.requireNonNull(id, "id");
        this.dirtyState = new DirtyStateTracker();

        Runnable wakeSignal = this::wakeIfRegistered;
        MachineEnergyComponent energy = new MachineEnergyComponent(
                Objects.requireNonNull(energyState, "energyState"),
                Objects.requireNonNull(energyAccess, "energyAccess"),
                dirtyState,
                wakeSignal);
        MachineInventoryComponent inventory = new MachineInventoryComponent(
                Objects.requireNonNull(inventorySlots, "inventorySlots"),
                dirtyState,
                wakeSignal);
        MachineThermalComponent thermal = new MachineThermalComponent(
                Objects.requireNonNull(thermalState, "thermalState"),
                Objects.requireNonNull(thermalProperties, "thermalProperties"),
                Objects.requireNonNull(thermalAccess, "thermalAccess"),
                dirtyState,
                wakeSignal);
        MachineProcessingComponent processing = new MachineProcessingComponent(dirtyState, wakeSignal);

        this.components = new MachineComponentState(dirtyState, energy, inventory, thermal, processing);
        this.participant = new MachineParticipant<>(
                new MachineState<>(id, components),
                Objects.requireNonNull(logic, "logic"));
        scheduler.register(id.value(), participant, dirtyState);
    }

    public MachineId id() {
        return id;
    }

    public MachineComponentState components() {
        return components;
    }

    public MachineParticipant<MachineComponentState> participant() {
        return participant;
    }

    public DirtyStateTracker dirtyState() {
        return dirtyState;
    }

    public boolean registered() {
        return registered;
    }

    /** Requests reevaluation without introducing recurring polling. */
    public boolean requestWork() {
        if (!registered) {
            return false;
        }
        participant.requestWork();
        return scheduler.wake(id.value());
    }

    public void setEnabled(boolean enabled) {
        participant.setEnabled(enabled);
        if (enabled) {
            requestWork();
        }
    }

    public MachineRuntimeDiagnostics diagnostics() {
        ensureRegistered();
        return new MachineRuntimeDiagnostics(
                scheduler.lifecycleOf(id.value()),
                scheduler.statusReason(id.value()),
                participant.diagnostics(),
                components.diagnostics());
    }

    /** Removes this runtime from the scheduler. Repeated calls are harmless. */
    @Override
    public void close() {
        if (!registered) {
            return;
        }
        scheduler.remove(id.value());
        registered = false;
    }

    private void wakeIfRegistered() {
        if (registered) {
            participant.requestWork();
            scheduler.wake(id.value());
        }
    }

    private void ensureRegistered() {
        if (!registered) {
            throw new IllegalStateException("machine runtime is not registered: " + id);
        }
    }
}
