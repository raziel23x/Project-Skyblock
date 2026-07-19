package raziel23x.projectskyblock.simulation.core;

/** Narrow backend context. Platform-specific services are added only with proven consumers. */
public interface SimulationContext {
    long gameTime();

    DirtyStateTracker dirtyState();
}
