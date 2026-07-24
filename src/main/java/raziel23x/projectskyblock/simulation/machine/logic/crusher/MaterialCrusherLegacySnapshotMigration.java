package raziel23x.projectskyblock.simulation.machine.logic.crusher;

import java.util.List;
import java.util.Objects;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;
import raziel23x.projectskyblock.simulation.machine.component.MachineProcessingStatus;
import raziel23x.projectskyblock.simulation.machine.persistence.MachineCombustionSnapshot;
import raziel23x.projectskyblock.simulation.machine.persistence.MachineProcessingSnapshot;
import raziel23x.projectskyblock.simulation.machine.persistence.MachineRuntimeSnapshot;

/** Normalizes pre-engine crusher values into the current typed machine snapshot contract. */
public final class MaterialCrusherLegacySnapshotMigration {
    private MaterialCrusherLegacySnapshotMigration() {}

    public static MachineRuntimeSnapshot migrate(
            List<SimulationItemStack> inventory,
            long progress,
            long processTime,
            long remainingBurnUnits,
            long totalBurnUnits,
            long storedEnergy,
            long energyCapacity,
            long thermalEnergy) {
        Objects.requireNonNull(inventory, "inventory");
        if (inventory.size() != MaterialCrusherLogic.SLOT_COUNT) {
            throw new IllegalArgumentException("legacy crusher inventory must contain four slots");
        }
        List<SimulationItemStack> copiedInventory = inventory.stream()
                .map(stack -> Objects.requireNonNull(stack, "legacy inventory stack"))
                .toList();

        long normalizedProcessTime = Math.max(1L, processTime);
        long normalizedProgress = Math.min(normalizedProcessTime, Math.max(0L, progress));
        long normalizedRemainingBurn = Math.max(0L, remainingBurnUnits);
        long normalizedTotalBurn = Math.max(
                normalizedRemainingBurn,
                Math.max(0L, totalBurnUnits));
        long normalizedCapacity = Math.max(0L, energyCapacity);
        long normalizedEnergy = Math.min(normalizedCapacity, Math.max(0L, storedEnergy));
        long normalizedThermalEnergy = Math.max(0L, thermalEnergy);

        MachineProcessingSnapshot processing = normalizedProgress == 0L
                ? idleProcessing()
                : new MachineProcessingSnapshot(
                        normalizedProgress == normalizedProcessTime
                                ? MachineProcessingStatus.READY_TO_COMPLETE
                                : MachineProcessingStatus.RUNNING,
                        MaterialCrusherLogic.LEGACY_PROCESS_ID,
                        normalizedProgress,
                        normalizedProcessTime,
                        "",
                        0L);

        return new MachineRuntimeSnapshot(
                MachineRuntimeSnapshot.CURRENT_SCHEMA_VERSION,
                normalizedEnergy,
                normalizedThermalEnergy,
                copiedInventory,
                new MachineCombustionSnapshot(normalizedRemainingBurn, normalizedTotalBurn),
                processing);
    }

    private static MachineProcessingSnapshot idleProcessing() {
        return new MachineProcessingSnapshot(
                MachineProcessingStatus.IDLE,
                "",
                0L,
                0L,
                "",
                0L);
    }
}
