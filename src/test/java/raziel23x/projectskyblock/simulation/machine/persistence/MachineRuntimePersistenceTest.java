package raziel23x.projectskyblock.simulation.machine.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ConcurrentModificationException;
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
import raziel23x.projectskyblock.simulation.machine.component.MachineEnergyTransaction;
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
            source.components().combustion().ignite(120L);
            source.components().combustion().consume(20L);
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
            assertEquals(100L, target.components().combustion().remainingBurnUnits());
            assertEquals(120L, target.components().combustion().totalBurnUnits());
            assertEquals(MachineProcessingStatus.RUNNING, target.components().processing().status());
            assertEquals(6L, target.components().processing().completedUnits());
            assertFalse(target.dirtyState().isDirty(DirtyFlag.PERSISTENCE));
            assertTrue(target.dirtyState().isDirty(DirtyFlag.CLIENT_SYNC));
        }
    }


    @Test
    void failedRestoreLeavesEveryComponentUnchanged() {
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        try (MachineRuntime target = runtime(scheduler, "transactional-target")) {
            target.components().energy().receive(50L);
            long originalThermalEnergy = target.components().thermal().thermalEnergyMicroJoules();
            MachineRuntimeSnapshot invalid = new MachineRuntimeSnapshot(
                    MachineRuntimeSnapshot.CURRENT_SCHEMA_VERSION,
                    900L,
                    originalThermalEnergy + 1_000L,
                    List.of(),
                    new MachineCombustionSnapshot(1L, 1L),
                    new MachineProcessingSnapshot(
                            MachineProcessingStatus.IDLE,
                            "",
                            0L,
                            0L,
                            "",
                            0L));

            assertThrows(IllegalArgumentException.class,
                    () -> MachineRuntimePersistence.restore(target, invalid));

            assertEquals(50L, target.components().energy().storedEnergy());
            assertEquals(originalThermalEnergy,
                    target.components().thermal().thermalEnergyMicroJoules());
            assertTrue(target.components().inventory().stack(0).isEmpty());
            assertFalse(target.components().combustion().burning());
            assertEquals(MachineProcessingStatus.IDLE, target.components().processing().status());
        }
    }


    @Test
    void restoreInvalidatesOpenEnergyCandidateWithoutPersistenceOrSchedulerDirty() {
        MachineRuntimeSnapshot snapshot;
        SimulationScheduler sourceScheduler = new SimulationScheduler(8, 8);
        try (MachineRuntime source = runtime(sourceScheduler, "restore-source")) {
            source.components().energy().receive(125L);
            snapshot = MachineRuntimePersistence.capture(source);
        }

        SimulationScheduler targetScheduler = new SimulationScheduler(8, 8);
        try (MachineRuntime target = runtime(targetScheduler, "restore-target")) {
            MachineEnergyTransaction stale = target.components().energy().beginTransaction();
            assertEquals(25L, stale.receive(25L));

            MachineRuntimePersistence.restore(target, snapshot);

            assertEquals(125L, target.components().energy().storedEnergy());
            assertEquals(1L, target.components().energy().stateVersion());
            assertFalse(target.dirtyState().isDirty(DirtyFlag.PERSISTENCE));
            assertTrue(target.dirtyState().isDirty(DirtyFlag.CLIENT_SYNC));
            assertFalse(target.dirtyState().isDirty(DirtyFlag.SCHEDULER));
            assertThrows(ConcurrentModificationException.class, stale::commit);
            assertEquals(125L, target.components().energy().storedEnergy());
        }
    }

    @Test
    void closedRuntimeRejectsRestoreBeforeMutation() {
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        MachineRuntime target = runtime(scheduler, "closed-target");
        target.components().energy().receive(25L);
        MachineRuntimeSnapshot snapshot = MachineRuntimePersistence.capture(target);
        target.close();

        assertThrows(IllegalStateException.class,
                () -> MachineRuntimePersistence.restore(target, snapshot));
        assertEquals(25L, target.components().energy().storedEnergy());
    }

    @Test
    void snapshotDoesNotContainSchedulerOrDiagnosticCounters() {
        SimulationScheduler scheduler = new SimulationScheduler(8, 8);
        try (MachineRuntime runtime = runtime(scheduler, "machine")) {
            MachineRuntimeSnapshot snapshot = MachineRuntimePersistence.capture(runtime);
            assertEquals(MachineRuntimeSnapshot.CURRENT_SCHEMA_VERSION, snapshot.schemaVersion());
            assertEquals(1, snapshot.inventory().size());
            assertEquals(MachineProcessingStatus.IDLE, snapshot.processing().status());
            assertEquals(MachineCombustionSnapshot.EMPTY, snapshot.combustion());
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
