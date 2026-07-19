package raziel23x.projectskyblock.simulation.core;

/**
 * Creates the narrow execution context for one participant.
 *
 * <p>The platform integration layer supplies world-facing services while the
 * scheduler supplies the participant's dirty-state tracker.</p>
 */
@FunctionalInterface
public interface SimulationContextFactory {
    SimulationContext create(String participantId, long gameTime, DirtyStateTracker dirtyState);
}
