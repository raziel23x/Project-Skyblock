package raziel23x.projectskyblock.simulation.machine.persistence;

import java.util.List;
import java.util.Objects;
import raziel23x.projectskyblock.simulation.core.DirtyFlag;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventorySlotSnapshot;
import raziel23x.projectskyblock.simulation.machine.component.MachineProcessingDiagnostics;
import raziel23x.projectskyblock.simulation.machine.runtime.MachineRuntime;

/** Captures and restores only authoritative durable machine state. */
public final class MachineRuntimePersistence {
    private MachineRuntimePersistence() {
    }

    public static MachineRuntimeSnapshot capture(MachineRuntime runtime) {
        Objects.requireNonNull(runtime, "runtime");
        List<SimulationItemStack> inventory = runtime.components().inventory().snapshot().stream()
                .map(MachineInventorySlotSnapshot::stack)
                .toList();
        MachineProcessingDiagnostics processing = runtime.components().processing().diagnostics();
        return new MachineRuntimeSnapshot(
                MachineRuntimeSnapshot.CURRENT_SCHEMA_VERSION,
                runtime.components().energy().storedEnergy(),
                runtime.components().thermal().thermalEnergyMicroJoules(),
                inventory,
                new MachineProcessingSnapshot(
                        processing.status(),
                        processing.processId(),
                        processing.completedUnits(),
                        processing.requiredUnits(),
                        processing.blockedReason(),
                        processing.completedProcesses()));
    }

    /** Restores state without persisting scheduler internals or replaying old wake events. */
    public static void restore(MachineRuntime runtime, MachineRuntimeSnapshot snapshot) {
        Objects.requireNonNull(runtime, "runtime");
        Objects.requireNonNull(snapshot, "snapshot");
        runtime.components().energy().authoritativeState().restoreStoredEnergy(snapshot.storedEnergy());
        runtime.components().thermal().restoreThermalEnergyMicroJoules(snapshot.thermalEnergyMicroJoules());
        runtime.components().inventory().restore(snapshot.inventory());
        MachineProcessingSnapshot processing = snapshot.processing();
        runtime.components().processing().restore(
                processing.status(),
                processing.processId(),
                processing.completedUnits(),
                processing.requiredUnits(),
                processing.blockedReason(),
                processing.completedProcesses());

        // Loading may require client presentation refresh, but restored state is already durable.
        runtime.dirtyState().clear(DirtyFlag.PERSISTENCE);
        runtime.dirtyState().clear(DirtyFlag.SCHEDULER);
        runtime.requestWork();
    }
}
