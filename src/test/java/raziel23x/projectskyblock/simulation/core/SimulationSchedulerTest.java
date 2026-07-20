package raziel23x.projectskyblock.simulation.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class SimulationSchedulerTest {
    private static final SimulationContextFactory CONTEXT_FACTORY =
            (participantId, gameTime, dirtyState) -> new TestContext(gameTime, dirtyState);

    @Test
    void sleepingParticipantPerformsNoRecurringWork() {
        AtomicInteger executions = new AtomicInteger();
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        scheduler.register("sleeping", participant(executions, context -> SimulationResult.sleep()));

        scheduler.wake("sleeping");
        scheduler.tick(0L, CONTEXT_FACTORY);
        scheduler.tick(1L, CONTEXT_FACTORY);
        scheduler.tick(2L, CONTEXT_FACTORY);

        assertEquals(1, executions.get());
        assertEquals(SimulationLifecycle.SLEEPING, scheduler.lifecycleOf("sleeping"));
    }

    @Test
    void repeatedWakeRequestsAreCoalesced() {
        AtomicInteger executions = new AtomicInteger();
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        scheduler.register("coalesced", participant(executions, context -> SimulationResult.sleep()));

        scheduler.wake("coalesced");
        scheduler.wake("coalesced");
        scheduler.wake("coalesced");
        SchedulerTickReport report = scheduler.tick(0L, CONTEXT_FACTORY);

        assertEquals(1, executions.get());
        assertEquals(1, report.executedParticipants());
    }

    @Test
    void wakeDuringExecutionIsDeferredUntilNextTick() {
        AtomicInteger executions = new AtomicInteger();
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        scheduler.register("self-waking", participant(executions, context -> {
            if (executions.get() == 1) {
                assertTrue(scheduler.wake("self-waking"));
            }
            return SimulationResult.sleep();
        }));

        scheduler.wake("self-waking");
        SchedulerTickReport first = scheduler.tick(10L, CONTEXT_FACTORY);

        assertEquals(1, executions.get());
        assertEquals(1, first.executedParticipants());
        assertEquals(SimulationLifecycle.SCHEDULED, scheduler.lifecycleOf("self-waking"));

        scheduler.tick(11L, CONTEXT_FACTORY);
        assertEquals(2, executions.get());
        assertEquals(SimulationLifecycle.SLEEPING, scheduler.lifecycleOf("self-waking"));
    }



    @Test
    void executionObserverSeesOnlyParticipantsThatActuallyRun() {
        AtomicInteger observed = new AtomicInteger();
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        scheduler.register("observed", participant(new AtomicInteger(), context -> {
            context.dirtyState().mark(DirtyFlag.PERSISTENCE);
            return SimulationResult.sleep();
        }));
        scheduler.register("still-sleeping", participant(new AtomicInteger(), context -> SimulationResult.sleep()));

        scheduler.wake("observed");
        scheduler.tick(0L, CONTEXT_FACTORY, (participantId, dirtyState) -> {
            assertEquals("observed", participantId);
            assertTrue(dirtyState.isDirty(DirtyFlag.PERSISTENCE));
            observed.incrementAndGet();
        });

        assertEquals(1, observed.get());
    }

    @Test
    void externallyOwnedDirtyTrackerIsUsedBySchedulerContext() {
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        DirtyStateTracker shared = new DirtyStateTracker();
        scheduler.register(
                "shared-dirty",
                participant(new AtomicInteger(), context -> {
                    assertTrue(context.dirtyState() == shared);
                    return SimulationResult.sleep();
                }),
                shared);

        assertTrue(scheduler.dirtyStateOf("shared-dirty") == shared);
        scheduler.wake("shared-dirty");
        scheduler.tick(0L, CONTEXT_FACTORY);
    }

    @Test
    void wakeDuringExecutionOverridesLongerScheduleWithNextTickReevaluation() {
        AtomicInteger executions = new AtomicInteger();
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        scheduler.register("rescheduled", participant(executions, context -> {
            if (executions.get() == 1) {
                scheduler.wake("rescheduled");
                return SimulationResult.scheduleAt(context.gameTime() + 100L);
            }
            return SimulationResult.sleep();
        }));

        scheduler.wake("rescheduled");
        scheduler.tick(20L, CONTEXT_FACTORY);
        scheduler.tick(21L, CONTEXT_FACTORY);

        assertEquals(2, executions.get());
        assertEquals(SimulationLifecycle.SLEEPING, scheduler.lifecycleOf("rescheduled"));
    }

    private static SimulationParticipant<TestState> participant(
            AtomicInteger executions,
            TestExecution execution) {
        return new SimulationParticipant<>() {
            private final TestState state = new TestState();

            @Override
            public TestState state() {
                return state;
            }

            @Override
            public SimulationResult execute(SimulationContext context, SimulationBudget budget) {
                executions.incrementAndGet();
                return execution.execute(context);
            }
        };
    }

    @FunctionalInterface
    private interface TestExecution {
        SimulationResult execute(SimulationContext context);
    }

    private record TestContext(long gameTime, DirtyStateTracker dirtyState)
            implements SimulationContext {}

    private static final class TestState implements SimulationState {}
}
