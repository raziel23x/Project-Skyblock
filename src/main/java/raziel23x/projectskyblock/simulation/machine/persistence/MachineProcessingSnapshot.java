package raziel23x.projectskyblock.simulation.machine.persistence;

import java.util.Objects;
import raziel23x.projectskyblock.simulation.machine.component.MachineProcessingStatus;

/** Minimal durable processing state for one machine operation. */
public record MachineProcessingSnapshot(
        MachineProcessingStatus status,
        String processId,
        long completedUnits,
        long requiredUnits,
        String blockedReason,
        long completedProcesses) {

    public MachineProcessingSnapshot {
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(processId, "processId");
        Objects.requireNonNull(blockedReason, "blockedReason");
        if (completedUnits < 0L || requiredUnits < 0L || completedProcesses < 0L) {
            throw new IllegalArgumentException("processing snapshot values must be non-negative");
        }
    }
}
