package raziel23x.projectskyblock.simulation.machine.runtime;

import java.util.Objects;
import raziel23x.projectskyblock.simulation.core.SimulationLifecycle;
import raziel23x.projectskyblock.simulation.machine.MachineDiagnostics;

/** Immutable aggregate snapshot for one registered composed machine runtime. */
public record MachineRuntimeDiagnostics(
        SimulationLifecycle schedulerLifecycle,
        String schedulerStatusReason,
        MachineDiagnostics machine,
        MachineComponentDiagnostics components) {

    public MachineRuntimeDiagnostics {
        Objects.requireNonNull(schedulerLifecycle, "schedulerLifecycle");
        Objects.requireNonNull(schedulerStatusReason, "schedulerStatusReason");
        Objects.requireNonNull(machine, "machine");
        Objects.requireNonNull(components, "components");
    }
}
