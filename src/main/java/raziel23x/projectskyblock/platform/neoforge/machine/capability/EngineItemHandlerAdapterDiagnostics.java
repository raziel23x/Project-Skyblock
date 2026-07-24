package raziel23x.projectskyblock.platform.neoforge.machine.capability;

import raziel23x.projectskyblock.simulation.machine.component.MachineInventoryViewKind;

/** Immutable diagnostics for one NeoForge item-capability adapter view. */
public record EngineItemHandlerAdapterDiagnostics(
        String viewName,
        MachineInventoryViewKind viewKind,
        long encodeFailures,
        long decodeFailures,
        long transactionConflicts) {

    public EngineItemHandlerAdapterDiagnostics {
        if (viewName == null || viewName.isBlank()) {
            throw new IllegalArgumentException("adapter view name must not be blank");
        }
        if (viewKind == null) {
            throw new NullPointerException("viewKind");
        }
        if (encodeFailures < 0L || decodeFailures < 0L || transactionConflicts < 0L) {
            throw new IllegalArgumentException("adapter diagnostic counts must be non-negative");
        }
    }
}
