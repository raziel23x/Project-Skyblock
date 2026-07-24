package raziel23x.projectskyblock.platform.neoforge.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.junit.jupiter.api.Test;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemKey;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemState;
import raziel23x.projectskyblock.simulation.machine.component.MachineProcessingStatus;
import raziel23x.projectskyblock.simulation.machine.persistence.MachineCombustionSnapshot;
import raziel23x.projectskyblock.simulation.machine.persistence.MachineProcessingSnapshot;
import raziel23x.projectskyblock.simulation.machine.persistence.MachineRuntimeSnapshot;

class MachineRuntimeNbtCodecTest {
    @Test
    void currentSchemaRoundTripsOpaqueItemState() {
        SimulationItemKey item = new SimulationItemKey(
                "minecraft",
                "diamond_pickaxe",
                SimulationItemState.opaque("projectskyblock:data_component_patch_v1", new byte[] {1, 0, 0, 0, 42}));
        MachineRuntimeSnapshot snapshot = snapshot(
                List.of(SimulationItemStack.of(item, 1L, 1L)),
                new MachineCombustionSnapshot(40L, 80L));
        CompoundTag parent = new CompoundTag();

        MachineRuntimeNbtCodec.write(parent, snapshot);
        MachineRuntimeSnapshot decoded = MachineRuntimeNbtCodec.read(parent);

        assertEquals(snapshot, decoded);
        assertTrue(decoded.inventory().get(0).item().hasState());
    }

    @Test
    void schemaOneMigratesStatelessInventoryToCurrentSchema() {
        MachineRuntimeSnapshot snapshot = snapshot(
                List.of(SimulationItemStack.of(
                        new SimulationItemKey("minecraft", "cobblestone"),
                        32L,
                        64L)),
                new MachineCombustionSnapshot(20L, 40L));
        CompoundTag parent = new CompoundTag();
        MachineRuntimeNbtCodec.write(parent, snapshot);
        parent.getCompound(MachineRuntimeNbtCodec.ROOT_KEY).putInt("Schema", 1);

        MachineRuntimeSnapshot decoded = MachineRuntimeNbtCodec.read(parent);

        assertEquals(MachineRuntimeSnapshot.CURRENT_SCHEMA_VERSION, decoded.schemaVersion());
        assertFalse(decoded.inventory().get(0).item().hasState());
        assertEquals(32L, decoded.inventory().get(0).quantity());
        assertEquals(MachineCombustionSnapshot.EMPTY, decoded.combustion());
    }


    @Test
    void schemaTwoMigratesOpaqueInventoryWithEmptyCombustion() {
        SimulationItemKey item = new SimulationItemKey(
                "minecraft",
                "diamond_pickaxe",
                SimulationItemState.opaque(
                        "projectskyblock:data_component_patch_v1",
                        new byte[] {7, 8, 9}));
        CompoundTag parent = new CompoundTag();
        MachineRuntimeNbtCodec.write(parent, snapshot(
                List.of(SimulationItemStack.of(item, 1L, 1L)),
                new MachineCombustionSnapshot(10L, 20L)));
        parent.getCompound(MachineRuntimeNbtCodec.ROOT_KEY).putInt("Schema", 2);

        MachineRuntimeSnapshot decoded = MachineRuntimeNbtCodec.read(parent);

        assertTrue(decoded.inventory().get(0).item().hasState());
        assertEquals(MachineCombustionSnapshot.EMPTY, decoded.combustion());
    }

    @Test
    void schemaThreeRequiresValidCombustionState() {
        CompoundTag parent = new CompoundTag();
        MachineRuntimeNbtCodec.write(parent, snapshot(
                List.of(SimulationItemStack.empty()),
                MachineCombustionSnapshot.EMPTY));
        CompoundTag root = parent.getCompound(MachineRuntimeNbtCodec.ROOT_KEY);
        root.remove("Combustion");

        assertThrows(IllegalArgumentException.class, () -> MachineRuntimeNbtCodec.read(parent));

        MachineRuntimeNbtCodec.write(parent, snapshot(
                List.of(SimulationItemStack.empty()),
                MachineCombustionSnapshot.EMPTY));
        CompoundTag combustion = parent.getCompound(MachineRuntimeNbtCodec.ROOT_KEY)
                .getCompound("Combustion");
        combustion.putLong("RemainingBurnUnits", 21L);
        combustion.putLong("TotalBurnUnits", 20L);

        assertThrows(IllegalArgumentException.class, () -> MachineRuntimeNbtCodec.read(parent));
    }

    @Test
    void malformedOpaqueStateIsRejectedWithoutProducingSnapshot() {
        CompoundTag parent = new CompoundTag();
        MachineRuntimeNbtCodec.write(parent, snapshot(
                List.of(SimulationItemStack.of(
                        new SimulationItemKey("minecraft", "diamond_pickaxe"),
                        1L,
                        1L)),
                MachineCombustionSnapshot.EMPTY));
        CompoundTag root = parent.getCompound(MachineRuntimeNbtCodec.ROOT_KEY);
        ListTag inventory = root.getList("Inventory", Tag.TAG_COMPOUND);
        inventory.getCompound(0).putString("StateCodec", "minecraft:data_components");

        assertThrows(IllegalArgumentException.class, () -> MachineRuntimeNbtCodec.read(parent));
    }

    private static MachineRuntimeSnapshot snapshot(
            List<SimulationItemStack> inventory,
            MachineCombustionSnapshot combustion) {
        return new MachineRuntimeSnapshot(
                MachineRuntimeSnapshot.CURRENT_SCHEMA_VERSION,
                100L,
                300_000L,
                inventory,
                combustion,
                new MachineProcessingSnapshot(
                        MachineProcessingStatus.IDLE,
                        "",
                        0L,
                        0L,
                        "",
                        0L));
    }
}
