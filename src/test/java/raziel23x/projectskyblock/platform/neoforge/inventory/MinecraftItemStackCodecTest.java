package raziel23x.projectskyblock.platform.neoforge.inventory;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.RegistryLayer;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemKey;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemState;

class MinecraftItemStackCodecTest {
    private final MinecraftItemStackCodec codec =
            new MinecraftItemStackCodec(
                    () -> RegistryLayer.createRegistryAccess().compositeAccess());

    @Test
    void statelessStackRoundTripsWithoutOpaquePayload() {
        ItemStack source = new ItemStack(Items.COBBLESTONE, 32);

        SimulationItemStack encoded = codec.encode(source);
        ItemStack decoded = codec.decode(encoded);

        assertFalse(encoded.item().hasState());
        assertEquals(32L, encoded.quantity());
        assertEquals(64L, encoded.maximumStackSize());
        assertTrue(ItemStack.isSameItemSameComponents(source, decoded));
        assertEquals(source.getCount(), decoded.getCount());
    }

    @Test
    void componentBearingStackRoundTripsExactly() {
        ItemStack source = new ItemStack(Items.DIAMOND_PICKAXE);
        source.set(DataComponents.DAMAGE, 47);
        source.set(DataComponents.REPAIR_COST, 3);

        SimulationItemStack encoded = codec.encode(source);
        ItemStack decoded = codec.decode(encoded);

        assertTrue(encoded.item().hasState());
        assertEquals(MinecraftItemStackCodec.COMPONENT_CODEC_ID, encoded.item().state().codecId());
        assertEquals(47, decoded.getDamageValue());
        assertEquals(3, decoded.getOrDefault(DataComponents.REPAIR_COST, 0));
        assertTrue(ItemStack.isSameItemSameComponents(source, decoded));
    }

    @Test
    void canonicalEncodingDoesNotDependOnComponentMutationOrder() {
        ItemStack first = new ItemStack(Items.DIAMOND_PICKAXE);
        first.set(DataComponents.DAMAGE, 9);
        first.set(DataComponents.REPAIR_COST, 2);

        ItemStack second = new ItemStack(Items.DIAMOND_PICKAXE);
        second.set(DataComponents.REPAIR_COST, 2);
        second.set(DataComponents.DAMAGE, 9);

        SimulationItemState firstState = codec.encode(first).item().state();
        SimulationItemState secondState = codec.encode(second).item().state();

        assertEquals(firstState, secondState);
        assertArrayEquals(firstState.payload(), secondState.payload());
    }

    @Test
    void unsupportedMalformedOrNonCanonicalOpaqueStateFailsClosed() {
        SimulationItemStack wrongCodec = SimulationItemStack.of(
                new SimulationItemKey(
                        "minecraft",
                        "diamond_pickaxe",
                        SimulationItemState.opaque("example:unknown", new byte[] {1})),
                1L,
                1L);
        SimulationItemStack malformed = SimulationItemStack.of(
                new SimulationItemKey(
                        "minecraft",
                        "diamond_pickaxe",
                        SimulationItemState.opaque(
                                MinecraftItemStackCodec.COMPONENT_CODEC_ID,
                                new byte[] {0, 0, 1})),
                1L,
                1L);

        ItemStack source = new ItemStack(Items.DIAMOND_PICKAXE);
        source.set(DataComponents.DAMAGE, 1);
        SimulationItemState canonical = codec.encode(source).item().state();
        byte[] padded = (" " + new String(canonical.payload(), StandardCharsets.UTF_8))
                .getBytes(StandardCharsets.UTF_8);
        SimulationItemStack nonCanonical = SimulationItemStack.of(
                new SimulationItemKey(
                        "minecraft",
                        "diamond_pickaxe",
                        SimulationItemState.opaque(
                                MinecraftItemStackCodec.COMPONENT_CODEC_ID,
                                padded)),
                1L,
                1L);

        assertThrows(ItemStackBoundaryException.class, () -> codec.decode(wrongCodec));
        assertThrows(ItemStackBoundaryException.class, () -> codec.decode(malformed));
        assertThrows(ItemStackBoundaryException.class, () -> codec.decode(nonCanonical));
    }

    @Test
    void transientComponentsFailClosedInsteadOfDisappearing() {
        ItemStack source = new ItemStack(Items.STONE);
        source.set(DataComponents.CREATIVE_SLOT_LOCK, Unit.INSTANCE);

        assertThrows(ItemStackBoundaryException.class, () -> codec.encode(source));
    }
}
