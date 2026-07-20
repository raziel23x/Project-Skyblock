package raziel23x.projectskyblock.simulation.machine.runtime;

import java.util.Objects;
import raziel23x.projectskyblock.simulation.machine.component.MachineEnergyDiagnostics;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventoryDiagnostics;
import raziel23x.projectskyblock.simulation.machine.component.MachineThermalDiagnostics;

/** Immutable aggregate snapshot of the resources owned by one composed machine. */
public record MachineComponentDiagnostics(
        int dirtyMask,
        MachineEnergyDiagnostics energy,
        MachineInventoryDiagnostics inventory,
        MachineThermalDiagnostics thermal) {

    public MachineComponentDiagnostics {
        Objects.requireNonNull(energy, "energy");
        Objects.requireNonNull(inventory, "inventory");
        Objects.requireNonNull(thermal, "thermal");
    }
}
