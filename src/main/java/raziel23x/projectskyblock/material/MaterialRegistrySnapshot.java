package raziel23x.projectskyblock.material;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import net.minecraft.resources.ResourceLocation;

/**
 * One immutable, atomically published material-data generation.
 *
 * <p>Definitions and their source provenance are prepared and validated together so readers
 * never observe materials from one reload paired with routes from another.</p>
 */
public final class MaterialRegistrySnapshot {
    private static final MaterialRegistrySnapshot EMPTY = new MaterialRegistrySnapshot(
            Map.of(), Map.of(), Map.of(), Map.of());

    private final Map<ResourceLocation, MaterialDefinition> materials;
    private final Map<ResourceLocation, ProcessingRouteDefinition> routes;
    private final Map<ResourceLocation, ResourceLocation> materialSources;
    private final Map<ResourceLocation, ResourceLocation> routeSources;

    public MaterialRegistrySnapshot(
            Map<ResourceLocation, MaterialDefinition> materials,
            Map<ResourceLocation, ProcessingRouteDefinition> routes,
            Map<ResourceLocation, ResourceLocation> materialSources,
            Map<ResourceLocation, ResourceLocation> routeSources) {
        this.materials = sortedImmutable(materials, "materials");
        this.routes = sortedImmutable(routes, "routes");
        this.materialSources = sortedImmutable(materialSources, "materialSources");
        this.routeSources = sortedImmutable(routeSources, "routeSources");
        requireMatchingKeys(this.materials, this.materialSources, "material");
        requireMatchingKeys(this.routes, this.routeSources, "route");
    }

    public static MaterialRegistrySnapshot empty() {
        return EMPTY;
    }

    public Map<ResourceLocation, MaterialDefinition> materials() {
        return materials;
    }

    public Map<ResourceLocation, ProcessingRouteDefinition> routes() {
        return routes;
    }

    public Optional<ResourceLocation> materialSource(ResourceLocation id) {
        return Optional.ofNullable(materialSources.get(Objects.requireNonNull(id, "id")));
    }

    public Optional<ResourceLocation> routeSource(ResourceLocation id) {
        return Optional.ofNullable(routeSources.get(Objects.requireNonNull(id, "id")));
    }

    private static <T> Map<ResourceLocation, T> sortedImmutable(
            Map<ResourceLocation, T> source,
            String name) {
        Objects.requireNonNull(source, name);
        TreeMap<ResourceLocation, T> sorted = new TreeMap<>();
        source.forEach((id, value) -> sorted.put(
                Objects.requireNonNull(id, name + " id"),
                Objects.requireNonNull(value, name + " value")));
        return Collections.unmodifiableMap(new LinkedHashMap<>(sorted));
    }

    private static void requireMatchingKeys(
            Map<ResourceLocation, ?> definitions,
            Map<ResourceLocation, ResourceLocation> sources,
            String kind) {
        if (!definitions.keySet().equals(sources.keySet())) {
            throw new IllegalArgumentException(kind + " provenance must match definition ids");
        }
    }
}
