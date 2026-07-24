package raziel23x.projectskyblock.simulation.machine.logic.crusher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;
import raziel23x.projectskyblock.simulation.core.SimulationContext;
import raziel23x.projectskyblock.simulation.core.SimulationLifecycle;
import raziel23x.projectskyblock.simulation.core.SimulationScheduler;
import raziel23x.projectskyblock.simulation.energy.EnergyLimits;
import raziel23x.projectskyblock.simulation.energy.SimulationEnergyState;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemKey;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;
import raziel23x.projectskyblock.simulation.machine.MachineId;
import raziel23x.projectskyblock.simulation.machine.component.MachineEnergyAccess;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventoryAccess;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventorySlotDefinition;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventorySlotRule;
import raziel23x.projectskyblock.simulation.machine.component.MachineProcessingStatus;
import raziel23x.projectskyblock.simulation.machine.component.MachineThermalAccess;
import raziel23x.projectskyblock.simulation.machine.runtime.MachineRuntime;
import raziel23x.projectskyblock.simulation.thermal.ThermalProperties;
import raziel23x.projectskyblock.simulation.thermal.ThermalState;

class MaterialCrusherLogicTest {
    private static final SimulationItemStack INPUT = stack("test:ore", 1L, 64L);
    private static final SimulationItemStack FUEL = stack("test:fuel", 1L, 64L);
    private static final SimulationItemStack BUCKET = stack("test:bucket", 1L, 16L);
    private static final SimulationItemStack OUTPUT = stack("test:dust", 1L, 64L);
    private static final SimulationItemStack BYPRODUCT = stack("test:grit", 1L, 64L);

    @Test
    void completesWithFeAndCommitsInputAndOutputsAtomically() {
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        MaterialCrusherLogic logic = logic(new MaterialCrusherSettings(true, true, true, 2L, 10L, 1.0D));
        try (MachineRuntime runtime = runtime(scheduler, logic, 100L)) {
            runtime.components().inventory().store(MaterialCrusherLogic.INPUT_SLOT, INPUT);
            runtime.components().energy().receive(20L);

            tick(scheduler, 0L);
            assertTrue(logic.working());
            assertEquals(MaterialCrusherPowerSource.FE, logic.activePowerSource());
            assertEquals(1L, runtime.components().processing().completedUnits());

            tick(scheduler, 1L);
            assertTrue(runtime.components().inventory().stack(MaterialCrusherLogic.INPUT_SLOT).isEmpty());
            assertEquals(OUTPUT, runtime.components().inventory().stack(MaterialCrusherLogic.OUTPUT_SLOT));
            assertEquals(BYPRODUCT, runtime.components().inventory().stack(MaterialCrusherLogic.BYPRODUCT_SLOT));
            assertEquals(0L, runtime.components().energy().storedEnergy());
            assertTrue(runtime.components().processing().idle());

            tick(scheduler, 2L);
            assertFalse(logic.working());
            assertEquals(SimulationLifecycle.SLEEPING, scheduler.lifecycleOf(runtime.id().value()));
        }
    }

    @Test
    void missingPowerPreservesProgressAndWakesAfterEnergyArrives() {
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        MaterialCrusherLogic logic = logic(new MaterialCrusherSettings(false, true, true, 3L, 10L, 1.0D));
        try (MachineRuntime runtime = runtime(scheduler, logic, 100L)) {
            runtime.components().inventory().store(MaterialCrusherLogic.INPUT_SLOT, INPUT);
            runtime.components().energy().receive(10L);

            tick(scheduler, 0L);
            tick(scheduler, 1L);
            assertEquals(MachineProcessingStatus.BLOCKED, runtime.components().processing().status());
            assertEquals(1L, runtime.components().processing().completedUnits());
            assertFalse(logic.working());

            runtime.components().energy().receive(10L);
            tick(scheduler, 2L);
            assertEquals(2L, runtime.components().processing().completedUnits());
            assertTrue(logic.working());
        }
    }

    @Test
    void blockedMaximumOutputResetsProgressWithoutConsumingPowerOrInput() {
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        MaterialCrusherLogic logic = logic(new MaterialCrusherSettings(false, true, true, 4L, 10L, 1.0D));
        try (MachineRuntime runtime = runtime(scheduler, logic, 100L)) {
            runtime.components().inventory().store(MaterialCrusherLogic.INPUT_SLOT, INPUT);
            runtime.components().inventory().store(
                    MaterialCrusherLogic.OUTPUT_SLOT,
                    stack("test:dust", 64L, 64L));
            runtime.components().energy().receive(100L);

            tick(scheduler, 0L);

            assertTrue(runtime.components().processing().idle());
            assertEquals(INPUT, runtime.components().inventory().stack(MaterialCrusherLogic.INPUT_SLOT));
            assertEquals(100L, runtime.components().energy().storedEnergy());
            assertEquals(SimulationLifecycle.BLOCKED, scheduler.lifecycleOf(runtime.id().value()));
            assertEquals(0, tick(scheduler, 1L));

            runtime.components().inventory().extract(MaterialCrusherLogic.OUTPUT_SLOT, 64L);
            assertEquals(1, tick(scheduler, 2L));
            assertEquals(1L, runtime.components().processing().completedUnits());
            assertEquals(90L, runtime.components().energy().storedEnergy());
        }
    }

    @Test
    void fuelIgnitionConsumesOneFuelAndStoresContainerRemainder() {
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        MaterialCrusherRecipePort recipes = recipePort();
        MaterialCrusherFuelPort fuels = fuel -> fuel.item().equals(FUEL.item())
                ? Optional.of(new MaterialCrusherFuel(8L, BUCKET))
                : Optional.empty();
        MaterialCrusherLogic logic = new MaterialCrusherLogic(
                recipes,
                fuels,
                () -> new MaterialCrusherSettings(true, false, false, 1L, 10L, 1.5D));
        try (MachineRuntime runtime = runtime(scheduler, logic, 0L)) {
            runtime.components().inventory().store(MaterialCrusherLogic.INPUT_SLOT, INPUT);
            runtime.components().inventory().store(MaterialCrusherLogic.FUEL_SLOT, FUEL);

            tick(scheduler, 0L);

            assertEquals(BUCKET, runtime.components().inventory().stack(MaterialCrusherLogic.FUEL_SLOT));
            assertEquals(12L, runtime.components().combustion().totalBurnUnits());
            assertEquals(11L, runtime.components().combustion().remainingBurnUnits());
            assertEquals(MaterialCrusherPowerSource.FUEL, logic.activePowerSource());
            assertEquals(OUTPUT, runtime.components().inventory().stack(MaterialCrusherLogic.OUTPUT_SLOT));
        }
    }

    @Test
    void legacyProgressAdoptsCurrentRecipeWithoutResettingCompletedUnits() {
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        MaterialCrusherLogic logic = logic(new MaterialCrusherSettings(false, true, true, 4L, 10L, 1.0D));
        try (MachineRuntime runtime = runtime(scheduler, logic, 100L)) {
            runtime.components().inventory().store(MaterialCrusherLogic.INPUT_SLOT, INPUT);
            runtime.components().energy().receive(10L);
            runtime.components().processing().restore(
                    MachineProcessingStatus.RUNNING,
                    MaterialCrusherLogic.LEGACY_PROCESS_ID,
                    2L,
                    4L,
                    "",
                    0L);
            runtime.requestWork();

            tick(scheduler, 0L);

            assertEquals("test:crushing", runtime.components().processing().processId());
            assertEquals(3L, runtime.components().processing().completedUnits());
        }
    }

    private static MaterialCrusherLogic logic(MaterialCrusherSettings settings) {
        return new MaterialCrusherLogic(
                recipePort(),
                fuel -> Optional.empty(),
                () -> settings);
    }

    private static MaterialCrusherRecipePort recipePort() {
        MaterialCrusherProcessResult result = new MaterialCrusherProcessResult(OUTPUT, BYPRODUCT);
        MaterialCrusherRecipeMatch match = new MaterialCrusherRecipeMatch("test:crushing", result);
        return new MaterialCrusherRecipePort() {
            @Override
            public Optional<MaterialCrusherRecipeMatch> findMatch(SimulationItemStack input) {
                return input.item().equals(INPUT.item()) ? Optional.of(match) : Optional.empty();
            }

            @Override
            public Optional<MaterialCrusherProcessResult> rollResult(
                    String recipeId,
                    SimulationItemStack input) {
                return recipeId.equals(match.recipeId()) && input.item().equals(INPUT.item())
                        ? Optional.of(result)
                        : Optional.empty();
            }
        };
    }

    private static MachineRuntime runtime(
            SimulationScheduler scheduler,
            MaterialCrusherLogic logic,
            long capacity) {
        MachineInventorySlotDefinition slot = new MachineInventorySlotDefinition(
                64L,
                MachineInventoryAccess.BIDIRECTIONAL,
                MachineInventorySlotRule.ACCEPT_ALL);
        return new MachineRuntime(
                scheduler,
                new MachineId("crusher-test"),
                new SimulationEnergyState(EnergyLimits.unlimitedThroughput(capacity)),
                MachineEnergyAccess.INPUT,
                List.of(slot, slot, slot, slot),
                new ThermalState(293_150L, 1L),
                new ThermalProperties(0L, 0L, Long.MAX_VALUE, Long.MAX_VALUE),
                MachineThermalAccess.NONE,
                logic);
    }

    private static int tick(SimulationScheduler scheduler, long gameTime) {
        return scheduler.tick(
                gameTime,
                (id, time, dirty) -> new TestContext(time, dirty))
                .executedParticipants();
    }

    private static SimulationItemStack stack(String id, long quantity, long maximum) {
        return SimulationItemStack.of(SimulationItemKey.parse(id), quantity, maximum);
    }

    private record TestContext(long gameTime, DirtyStateTracker dirtyState)
            implements SimulationContext {}
}
