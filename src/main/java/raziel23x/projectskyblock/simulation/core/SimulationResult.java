package raziel23x.projectskyblock.simulation.core;

/** Explicit scheduling decision returned by every simulation execution. */
public sealed interface SimulationResult {
    record Sleep() implements SimulationResult {}

    record ScheduleAt(long gameTime) implements SimulationResult {
        public ScheduleAt {
            if (gameTime < 0) {
                throw new IllegalArgumentException("gameTime must be non-negative");
            }
        }
    }

    record ContinueNextTick() implements SimulationResult {}

    record Blocked(String reason) implements SimulationResult {
        public Blocked {
            if (reason == null || reason.isBlank()) {
                throw new IllegalArgumentException("reason must not be blank");
            }
        }
    }

    record Invalid(String reason) implements SimulationResult {
        public Invalid {
            if (reason == null || reason.isBlank()) {
                throw new IllegalArgumentException("reason must not be blank");
            }
        }
    }

    static Sleep sleep() {
        return new Sleep();
    }

    static ScheduleAt scheduleAt(long gameTime) {
        return new ScheduleAt(gameTime);
    }

    static ContinueNextTick continueNextTick() {
        return new ContinueNextTick();
    }

    static Blocked blocked(String reason) {
        return new Blocked(reason);
    }

    static Invalid invalid(String reason) {
        return new Invalid(reason);
    }
}
