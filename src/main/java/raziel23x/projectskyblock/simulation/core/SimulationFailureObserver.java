package raziel23x.projectskyblock.simulation.core;

/** Receives isolated scheduler failures for platform diagnostics. */
@FunctionalInterface
public interface SimulationFailureObserver {
    SimulationFailureObserver NONE = (participantId, stage, failure) -> { };

    void onFailure(String participantId, SimulationFailureStage stage, RuntimeException failure);
}
