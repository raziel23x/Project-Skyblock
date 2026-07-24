package raziel23x.projectskyblock.simulation.machine.logic.crusher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemKey;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;
import raziel23x.projectskyblock.simulation.machine.component.MachineProcessingStatus;
import raziel23x.projectskyblock.simulation.machine.persistence.MachineRuntimeSnapshot;

class MaterialCrusherLegacySnapshotMigrationTest {
    @Test
    void clampsLegacyValuesAndPreservesAllFourInventorySlots() {
        List<SimulationItemStack> inventory = List.of(
                stack("test:input", 3L),
                stack("test:fuel", 2L),
                stack("test:output", 4L),
                SimulationItemStack.empty());

        MachineRuntimeSnapshot snapshot = MaterialCrusherLegacySnapshotMigration.migrate(
                inventory,
                500L,
                80L,
                20L,
                10L,
                50_000L,
                10_000L,
                293_150L);

        assertEquals(MachineRuntimeSnapshot.CURRENT_SCHEMA_VERSION, snapshot.schemaVersion());
        assertEquals(inventory, snapshot.inventory());
        assertEquals(10_000L, snapshot.storedEnergy());
        assertEquals(20L, snapshot.combustion().remainingBurnUnits());
        assertEquals(20L, snapshot.combustion().totalBurnUnits());
        assertEquals(MachineProcessingStatus.READY_TO_COMPLETE, snapshot.processing().status());
        assertEquals(80L, snapshot.processing().completedUnits());
        assertEquals(80L, snapshot.processing().requiredUnits());
        assertEquals(MaterialCrusherLogic.LEGACY_PROCESS_ID, snapshot.processing().processId());
    }

    @Test
    void zeroProgressMigratesToCanonicalIdleProcessing() {
        MachineRuntimeSnapshot snapshot = MaterialCrusherLegacySnapshotMigration.migrate(
                List.of(
                        SimulationItemStack.empty(),
                        SimulationItemStack.empty(),
                        SimulationItemStack.empty(),
                        SimulationItemStack.empty()),
                -10L,
                0L,
                -1L,
                -2L,
                -3L,
                -4L,
                -5L);

        assertTrue(snapshot.processing().status() == MachineProcessingStatus.IDLE);
        assertEquals(0L, snapshot.storedEnergy());
        assertEquals(0L, snapshot.thermalEnergyMicroJoules());
        assertEquals(0L, snapshot.combustion().remainingBurnUnits());
        assertEquals(0L, snapshot.combustion().totalBurnUnits());
    }

    @Test
    void rejectsIncompleteLegacyInventory() {
        assertThrows(
                IllegalArgumentException.class,
                () -> MaterialCrusherLegacySnapshotMigration.migrate(
                        List.of(SimulationItemStack.empty()),
                        0L,
                        80L,
                        0L,
                        0L,
                        0L,
                        10_000L,
                        293_150L));
    }

    private static SimulationItemStack stack(String id, long quantity) {
        return SimulationItemStack.of(SimulationItemKey.parse(id), quantity, 64L);
    }
}
