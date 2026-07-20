package raziel23x.projectskyblock.simulation.core;

/**
 * Allocation-free notification emitted after one participant execution.
 *
 * <p>The observer may record integration work, diagnostics, or test evidence. It must not
 * execute simulation recursively or mutate scheduler registration while the callback is active.</p>
 */
@FunctionalInterface
public interface SimulationExecutionObserver {
    SimulationExecutionObserver NONE = (participantId, dirtyState) -> {};

    void afterExecution(String participantId, DirtyStateTracker dirtyState);
}
