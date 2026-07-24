package raziel23x.projectskyblock.simulation.machine.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import raziel23x.projectskyblock.simulation.core.DirtyFlag;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;
import raziel23x.projectskyblock.simulation.core.SimulationContext;
import raziel23x.projectskyblock.simulation.core.SimulationContextFactory;
import raziel23x.projectskyblock.simulation.core.SimulationLifecycle;
import raziel23x.projectskyblock.simulation.core.SimulationResult;
import raziel23x.projectskyblock.simulation.core.SimulationScheduler;
import raziel23x.projectskyblock.simulation.energy.EnergyLimits;
import raziel23x.projectskyblock.simulation.energy.SimulationEnergyState;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemKey;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;
import raziel23x.projectskyblock.simulation.machine.MachineActivity;
import raziel23x.projectskyblock.simulation.machine.MachineId;
import raziel23x.projectskyblock.simulation.machine.component.MachineEnergyAccess;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventorySlotDefinition;
import raziel23x.projectskyblock.simulation.machine.component.MachineThermalAccess;
import raziel23x.projectskyblock.simulation.thermal.ThermalProperties;
import raziel23x.projectskyblock.simulation.thermal.ThermalState;

class MachineRuntimeTest {
    private static final SimulationItemKey INPUT = SimulationItemKey.parse("projectskyblock:test_input");
    private static final SimulationContextFactory CONTEXT_FACTORY =
            (participantId, gameTime, dirtyState) -> new TestContext(gameTime, dirtyState);
    private static final ThermalProperties THERMAL_PROPERTIES = new ThermalProperties(
            10L,
            290_000L,
            350_000L,
            400_000L);

    @Test
    void componentsSchedulerAndContextShareOneDirtyTracker() {
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        try (MachineRuntime runtime = runtime(scheduler, (state, context, budget) -> SimulationResult.sleep())) {
            DirtyStateTracker shared = runtime.dirtyState();

            assertSame(shared, runtime.components().dirtyState());
            assertSame(shared, runtime.components().energy().dirtyState());
            assertSame(shared, runtime.components().inventory().dirtyState());
            assertSame(shared, runtime.components().thermal().dirtyState());
            assertSame(shared, runtime.components().combustion().dirtyState());
            assertSame(shared, runtime.components().processing().dirtyState());
            assertSame(shared, scheduler.dirtyStateOf(runtime.id().value()));

            runtime.components().energy().receive(100L);
            assertTrue(shared.isDirty(DirtyFlag.PERSISTENCE));
            assertTrue(shared.isDirty(DirtyFlag.CLIENT_SYNC));
            assertTrue(shared.isDirty(DirtyFlag.SCHEDULER));
        }
    }

    @Test
    void sleepingMachineDoesNotPollAndExternalChangeWakesIt() {
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        try (MachineRuntime runtime = runtime(scheduler, (state, context, budget) -> SimulationResult.sleep())) {
            runtime.requestWork();
            scheduler.tick(0L, CONTEXT_FACTORY);
            scheduler.tick(1L, CONTEXT_FACTORY);

            assertEquals(1L, runtime.participant().state().executionCount());
            assertEquals(SimulationLifecycle.SLEEPING, scheduler.lifecycleOf(runtime.id().value()));

            runtime.components().inventory().insert(
                    0,
                    SimulationItemStack.of(INPUT, 1L, 64L));
            assertEquals(SimulationLifecycle.READY, scheduler.lifecycleOf(runtime.id().value()));

            scheduler.tick(2L, CONTEXT_FACTORY);
            assertEquals(2L, runtime.participant().state().executionCount());
        }
    }

    @Test
    void blockedMachineReevaluatesOnlyAfterRelevantComponentChange() {
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        try (MachineRuntime runtime = runtime(scheduler, (state, context, budget) -> {
            if (state.data().inventory().stack(0).isEmpty()) {
                return SimulationResult.blocked("missing input");
            }
            state.data().inventory().consume(0, 1L);
            return SimulationResult.sleep();
        })) {
            runtime.requestWork();
            scheduler.tick(0L, CONTEXT_FACTORY);
            scheduler.tick(1L, CONTEXT_FACTORY);

            assertEquals(SimulationLifecycle.BLOCKED, scheduler.lifecycleOf(runtime.id().value()));
            assertEquals(1L, runtime.participant().state().executionCount());

            runtime.components().inventory().insert(
                    0,
                    SimulationItemStack.of(INPUT, 1L, 64L));
            scheduler.tick(2L, CONTEXT_FACTORY);

            assertEquals(2L, runtime.participant().state().executionCount());
            assertTrue(runtime.components().inventory().stack(0).isEmpty());
            assertEquals(SimulationLifecycle.SCHEDULED, scheduler.lifecycleOf(runtime.id().value()));

            scheduler.tick(3L, CONTEXT_FACTORY);
            assertEquals(3L, runtime.participant().state().executionCount());
            assertEquals(SimulationLifecycle.BLOCKED, scheduler.lifecycleOf(runtime.id().value()));
        }
    }

    @Test
    void boundedLogicCannotConsumeMoreWorkThanProvided() {
        SimulationScheduler scheduler = new SimulationScheduler(8, 1);
        try (MachineRuntime runtime = runtime(scheduler, (state, context, budget) -> {
            assertTrue(budget.tryConsume(1));
            assertFalse(budget.tryConsume(1));
            return SimulationResult.sleep();
        })) {
            runtime.requestWork();
            scheduler.tick(0L, CONTEXT_FACTORY);

            assertEquals(1L, runtime.participant().state().executionCount());
            assertEquals(MachineActivity.SLEEPING, runtime.participant().state().activity());
        }
    }

    @Test
    void diagnosticsAggregateLifecycleAndComponentState() {
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        try (MachineRuntime runtime = runtime(scheduler, (state, context, budget) ->
                SimulationResult.blocked("waiting for recipe"))) {
            runtime.components().energy().receive(250L);
            scheduler.tick(0L, CONTEXT_FACTORY);

            MachineRuntimeDiagnostics diagnostics = runtime.diagnostics();
            assertEquals(SimulationLifecycle.BLOCKED, diagnostics.schedulerLifecycle());
            assertEquals("waiting for recipe", diagnostics.schedulerStatusReason());
            assertEquals(250L, diagnostics.components().energy().storedEnergy());
            assertFalse(diagnostics.components().combustion().burning());
            assertFalse(diagnostics.components().processing().active());
            assertEquals(MachineActivity.BLOCKED, diagnostics.machine().activity());
        }
    }

    @Test
    void closeUnregistersAndPreventsFurtherRuntimeOperations() {
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        MachineRuntime runtime = runtime(scheduler, (state, context, budget) -> SimulationResult.sleep());

        runtime.close();
        runtime.close();

        assertFalse(runtime.registered());
        assertFalse(scheduler.contains(runtime.id().value()));
        assertFalse(runtime.requestWork());
        assertThrows(IllegalStateException.class, runtime::diagnostics);
    }

    private static MachineRuntime runtime(
            SimulationScheduler scheduler,
            raziel23x.projectskyblock.simulation.machine.MachineLogic<MachineComponentState> logic) {
        return new MachineRuntime(
                scheduler,
                new MachineId("test-machine"),
                new SimulationEnergyState(new EnergyLimits(1_000L, 1_000L, 1_000L)),
                MachineEnergyAccess.INPUT,
                List.of(MachineInventorySlotDefinition.unrestricted(64L)),
                new ThermalState(300_000L, 100L),
                THERMAL_PROPERTIES,
                MachineThermalAccess.NONE,
                logic);
    }

    private record TestContext(long gameTime, DirtyStateTracker dirtyState)
            implements SimulationContext {}
}
