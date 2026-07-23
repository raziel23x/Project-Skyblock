package raziel23x.projectskyblock.material;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

class MaterialRegistrySnapshotTest {
    private static final ResourceLocation MATERIAL_ID = id("copper");
    private static final ResourceLocation ROUTE_ID = id("crushing");
    private static final ResourceLocation MATERIAL_SOURCE = id("projectskyblock/materials/copper.json");
    private static final ResourceLocation ROUTE_SOURCE = id("projectskyblock/processing_routes/crushing.json");

    @Test
    void snapshotPreservesDeterministicOrderAndSourceProvenance() {
        ResourceLocation zincId = id("zinc");
        MaterialDefinition copper = material(MATERIAL_ID, 5);
        MaterialDefinition zinc = material(zincId, 1);
        ProcessingRouteDefinition route = route(ROUTE_ID, 2);

        MaterialRegistrySnapshot snapshot = new MaterialRegistrySnapshot(
                Map.of(zincId, zinc, MATERIAL_ID, copper),
                Map.of(ROUTE_ID, route),
                Map.of(zincId, id("projectskyblock/materials/zinc.json"), MATERIAL_ID, MATERIAL_SOURCE),
                Map.of(ROUTE_ID, ROUTE_SOURCE));

        assertEquals(List.of(MATERIAL_ID, zincId), List.copyOf(snapshot.materials().keySet()));
        assertEquals(MATERIAL_SOURCE, snapshot.materialSource(MATERIAL_ID).orElseThrow());
        assertEquals(ROUTE_SOURCE, snapshot.routeSource(ROUTE_ID).orElseThrow());
    }

    @Test
    void sourceMapsMustExactlyMatchDefinitionIds() {
        assertThrows(IllegalArgumentException.class, () -> new MaterialRegistrySnapshot(
                Map.of(MATERIAL_ID, material(MATERIAL_ID, 0)),
                Map.of(),
                Map.of(),
                Map.of()));
    }

    @Test
    void registryPublishesOneCompleteGeneration() {
        MaterialRegistrySnapshot original = MaterialRegistry.snapshot();
        MaterialRegistrySnapshot candidate = new MaterialRegistrySnapshot(
                Map.of(MATERIAL_ID, material(MATERIAL_ID, 4)),
                Map.of(ROUTE_ID, route(ROUTE_ID, 3)),
                Map.of(MATERIAL_ID, MATERIAL_SOURCE),
                Map.of(ROUTE_ID, ROUTE_SOURCE));
        try {
            MaterialRegistry.publish(candidate);

            assertTrue(MaterialRegistry.getMaterial(MATERIAL_ID).isPresent());
            assertTrue(MaterialRegistry.getRoute(ROUTE_ID).isPresent());
            assertEquals(candidate, MaterialRegistry.snapshot());
        } finally {
            MaterialRegistry.publish(original);
        }
    }

    private static MaterialDefinition material(ResourceLocation id, int priority) {
        return new MaterialDefinition(
                id,
                MaterialCategory.METAL,
                Set.of(MaterialForm.INGOT),
                List.of(ROUTE_ID),
                null,
                "projectskyblock",
                Set.of(id("metal")),
                Set.of(id.getPath()),
                priority,
                true);
    }

    private static ProcessingRouteDefinition route(ResourceLocation id, int priority) {
        return new ProcessingRouteDefinition(
                id,
                MaterialCategory.METAL,
                List.of("crush"),
                Set.of(MaterialForm.ORE),
                Set.of(MaterialForm.DUST),
                priority);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("projectskyblock", path);
    }
}
