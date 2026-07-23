package raziel23x.projectskyblock.simulation.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SimulationItemStateTest {
    @Test
    void opaqueStateParticipatesInIdentityAndMergeRules() {
        SimulationItemKey undamaged = SimulationItemKey.parse("minecraft:diamond_pickaxe")
                .withState(SimulationItemState.opaque("projectskyblock:data_component_patch_v1", new byte[] {0, 0, 0, 0}));
        SimulationItemKey damaged = SimulationItemKey.parse("minecraft:diamond_pickaxe")
                .withState(SimulationItemState.opaque("projectskyblock:data_component_patch_v1", new byte[] {0, 0, 0, 12}));

        assertNotEquals(undamaged, damaged);
        assertNotEquals(0, undamaged.compareTo(damaged));
        assertFalse(SimulationItemStack.of(undamaged, 1L, 1L)
                .canMerge(SimulationItemStack.of(damaged, 1L, 1L)));
        assertTrue(undamaged.hasState());
    }

    @Test
    void statelessIdentityRemainsCompactAndBackwardCompatible() {
        SimulationItemKey key = SimulationItemKey.parse("minecraft:stone");

        assertFalse(key.hasState());
        assertTrue(key.state().isEmpty());
    }

    @Test
    void payloadWithoutCodecIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new SimulationItemState("", new byte[] {1}));
        assertThrows(IllegalArgumentException.class,
                () -> SimulationItemState.opaque("minecraft:data_components", new byte[0]));
    }


    @Test
    void nbtPayloadContractsAndUnboundedStateAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> SimulationItemState.opaque("projectskyblock:nbt_v1", new byte[] {1}));
        assertThrows(IllegalArgumentException.class,
                () -> SimulationItemState.opaque("projectskyblock:snbt", new byte[] {1}));
        assertThrows(IllegalArgumentException.class,
                () -> SimulationItemState.opaque(
                        "projectskyblock:data_component_patch_v1",
                        new byte[SimulationItemState.MAX_PAYLOAD_BYTES + 1]));
    }

    @Test
    void payloadIsDefensivelyCopied() {
        byte[] source = new byte[] {1, 2, 3};
        SimulationItemState state = SimulationItemState.opaque("projectskyblock:test", source);
        source[0] = 9;
        byte[] returned = state.payload();
        returned[1] = 9;

        assertEquals(1, state.payload()[0]);
        assertEquals(2, state.payload()[1]);
    }
}
