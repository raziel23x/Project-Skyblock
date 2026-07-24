package raziel23x.projectskyblock.compat.curios;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class CuriosRepairGemResourcesTest {
    @Test
    void dedicatedRepairGemSlotResourcesArePackaged() throws IOException {
        JsonObject slot = readJson(
                "/data/projectskyblock/curios/slots/repair_gem.json");
        assertEquals(1, slot.get("size").getAsInt());
        assertEquals("SET", slot.get("operation").getAsString());
        assertEquals(
                "projectskyblock:slot/repair_gem",
                slot.get("icon").getAsString());
        assertFalse(slot.get("add_cosmetic").getAsBoolean());
        assertTrue(slot.get("use_native_gui").getAsBoolean());
        assertFalse(slot.get("render_toggle").getAsBoolean());
        assertEquals("DEFAULT", slot.get("drop_rule").getAsString());
        assertTrue(contains(slot.getAsJsonArray("validators"), "curios:tag"));

        JsonObject entities = readJson(
                "/data/projectskyblock/curios/entities/repair_gem.json");
        assertTrue(contains(
                entities.getAsJsonArray("entities"),
                "minecraft:player"));
        assertTrue(contains(
                entities.getAsJsonArray("slots"),
                "repair_gem"));

        JsonObject tag = readJson(
                "/data/curios/tags/item/repair_gem.json");
        assertFalse(tag.get("replace").getAsBoolean());
        assertTrue(contains(
                tag.getAsJsonArray("values"),
                "projectskyblock:repair_gem"));

        JsonObject language = readJson(
                "/assets/projectskyblock/lang/en_us.json");
        assertEquals(
                "Repair Gem",
                language.get("curios.identifier.repair_gem").getAsString());

        try (InputStream icon = CuriosRepairGemResourcesTest.class
                .getResourceAsStream(
                        "/assets/projectskyblock/textures/slot/repair_gem.png")) {
            assertNotNull(icon, "Repair Gem Curios slot icon must be packaged");
        }
    }

    private static JsonObject readJson(String path) throws IOException {
        try (InputStream stream = CuriosRepairGemResourcesTest.class
                .getResourceAsStream(path)) {
            assertNotNull(stream, "Missing runtime resource: " + path);
            try (InputStreamReader reader = new InputStreamReader(
                    stream,
                    StandardCharsets.UTF_8)) {
                return JsonParser.parseReader(reader).getAsJsonObject();
            }
        }
    }

    private static boolean contains(JsonArray values, String expected) {
        return values.asList().stream()
                .anyMatch(element -> expected.equals(element.getAsString()));
    }
}
