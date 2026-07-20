package raziel23x.projectskyblock.simulation.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;
import raziel23x.projectskyblock.simulation.core.SimulationContext;
import raziel23x.projectskyblock.simulation.core.SimulationLifecycle;
import raziel23x.projectskyblock.simulation.core.SimulationScheduler;
import raziel23x.projectskyblock.simulation.energy.EnergyLimits;
import raziel23x.projectskyblock.simulation.energy.SimulationEnergyState;
import raziel23x.projectskyblock.simulation.machine.MachineId;
import raziel23x.projectskyblock.simulation.machine.component.MachineEnergyAccess;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventorySlotDefinition;
import raziel23x.projectskyblock.simulation.machine.component.MachineThermalAccess;
import raziel23x.projectskyblock.simulation.machine.runtime.MachineRuntime;
import raziel23x.projectskyblock.simulation.thermal.ThermalProperties;
import raziel23x.projectskyblock.simulation.thermal.ThermalState;

class CreativeEnergyCellLogicTest {
    @Test
    void fillsBackendEnergyBufferAndSleeps() {
        SimulationScheduler scheduler = new SimulationScheduler(4, 4);
        try (MachineRuntime runtime = new MachineRuntime(
                scheduler,
                new MachineId("creative-test"),
                new SimulationEnergyState(new EnergyLimits(6_000_000L, 6_000_000L, 6_000_000L)),
                MachineEnergyAccess.OUTPUT,
                List.of(MachineInventorySlotDefinition.unrestricted(1L)),
                new ThermalState(293_150L, 1L),
                new ThermalProperties(0L, 0L, Long.MAX_VALUE, Long.MAX_VALUE),
                MachineThermalAccess.NONE,
                CreativeEnergyCellLogic.INSTANCE)) {
            runtime.requestWork();
            var firstReport = scheduler.tick(0L, (id, time, dirty) -> new TestContext(time, dirty));
            var secondReport = scheduler.tick(1L, (id, time, dirty) -> new TestContext(time, dirty));

            assertEquals(1, firstReport.executedParticipants());
            assertEquals(1, secondReport.executedParticipants());
            assertEquals(6_000_000L, runtime.components().energy().storedEnergy());
            assertEquals(SimulationLifecycle.SLEEPING, scheduler.lifecycleOf(runtime.id().value()));
        }
    }

    private record TestContext(long gameTime, DirtyStateTracker dirtyState)
            implements SimulationContext {}
}
