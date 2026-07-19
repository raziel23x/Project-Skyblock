package raziel23x.projectskyblock.simulation.core;

/** Allocation-light summary returned after one scheduler tick. */
public record SchedulerTickReport(
        int executedParticipants,
        int sleepingParticipants,
        int readyParticipants,
        int scheduledParticipants,
        int blockedParticipants,
        int invalidParticipants) {
}
