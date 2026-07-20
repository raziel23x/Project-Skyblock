package raziel23x.projectskyblock.simulation.machine.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import raziel23x.projectskyblock.simulation.core.DirtyFlag;
import raziel23x.projectskyblock.simulation.core.SimulationResult;
import raziel23x.projectskyblock.simulation.core.SimulationScheduler;
import raziel23x.projectskyblock.simulation.energy.EnergyLimits;
import raziel23x.projectskyblock.simulation.energy.SimulationEnergyState;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemKey;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;
import raziel23x.projectskyblock.simulation.machine.MachineId;
import raziel23x.projectskyblock.simulation.machine.component.MachineEnergyAccess;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventorySlotDefinition;
import raziel23x.projectskyblock.simulation.machine.component.MachineProcessingStatus;
import raziel23x.projectskyblock.simulation.machine.component.MachineThermalAccess;
import raziel23x.projectskyblock.simulation.machine.runtime.MachineRuntime;
import raziel23x.projectskyblock.simulation.thermal.ThermalProperties;
import raziel23x.projectskyblock.simulation.thermal.ThermalState;

class MachineRuntimePersistenceTest {
    private static final SimulationItemKey ITEM = SimulationItemKey.parse("projectskyblock:persisted_input");
    private static final ThermalProperties PROPERTIES = new ThermalProperties(10L, 290_000L, 350_000L, 400_000L);

    @Test
    void captureAndRestoreRoundTripsOnlyAuthoritativeState() {
        SimulationScheduler sourceScheduler = new SimulationScheduler(8, 8);
        MachineRuntimeSnapshot snapshot;
        try (MachineRuntime source = runtime(sourceScheduler, "source")) {
            source.components().energy().receive(400L);
            source.components().inventory().insert(0, SimulationItemStack.of(ITEM, 7L, 64L));
            source.components().thermal().generateHeat(12_345L);
            source.components().processing().start("projectskyblock:test_process", 20L);
            source.components().processing().advance(6L);
            snapshot = MachineRuntimePersistence.capture(source);
        }

        SimulationScheduler targetScheduler = new SimulationScheduler(8, 8);
        try (MachineRuntime target = runtime(targetScheduler, "target")) {
            MachineRuntimePersistence.restore(target, snapshot);

            assertEquals(400L, target.components().energy().storedEnergy());
            assertEquals(7L, target.components().inventory().stack(0).quantity());
            assertEquals(snapshot.thermalEnergyMicroJoules(), target.components().thermal().thermalEnergyMicroJoules());
            assertEquals(MachineProcessingStatus.RUNNING, target.components().processing().status());
            assertEquals(6L, target.components().processing().completedUnits());
            assertFalse(target.dirtyState().isDirty(DirtyFlag.PERSISTENCE));
            assertTrue(target.dirtyState().isDirty(DirtyFlag.CLIENT_SYNC));
        }
    }

    @Test
    void snapshotDoesNotContainSchedulerOrDiagnosticCounters() {
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        try (MachineRuntime runtime = runtime(scheduler, "machine")) {
            MachineRuntimeSnapshot snapshot = MachineRuntimePersistence.capture(runtime);
            assertEquals(MachineRuntimeSnapshot.CURRENT_SCHEMA_VERSION, snapshot.schemaVersion());
            assertEquals(1, snapshot.inventory().size());
            assertEquals(MachineProcessingStatus.IDLE, snapshot.processing().status());
        }
    }

    private static MachineRuntime runtime(SimulationScheduler scheduler, String id) {
        return new MachineRuntime(
                scheduler,
                new MachineId(id),
                new SimulationEnergyState(new EnergyLimits(1_000L, 1_000L, 1_000L)),
                MachineEnergyAccess.BIDIRECTIONAL,
                List.of(MachineInventorySlotDefinition.unrestricted(64L)),
                new ThermalState(300_000L, 100L),
                PROPERTIES,
                MachineThermalAccess.BIDIRECTIONAL,
                (state, context, budget) -> SimulationResult.sleep());
    }
}
