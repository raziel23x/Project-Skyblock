package raziel23x.projectskyblock.material;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class MaterialRegistry {
    private static volatile Map<ResourceLocation, MaterialDefinition> materials = Map.of();
    private static volatile Map<ResourceLocation, ProcessingRouteDefinition> routes = Map.of();

    private MaterialRegistry() {
    }

    public static Optional<MaterialDefinition> getMaterial(ResourceLocation id) {
        return Optional.ofNullable(materials.get(id));
    }

    public static Optional<ProcessingRouteDefinition> getRoute(ResourceLocation id) {
        return Optional.ofNullable(routes.get(id));
    }

    public static Collection<MaterialDefinition> materials() {
        return materials.values();
    }

    public static Collection<ProcessingRouteDefinition> routes() {
        return routes.values();
    }

    public static Set<MaterialDefinition> materialsInCategory(MaterialCategory category) {
        return materials.values().stream()
                .filter(material -> material.category() == category)
                .collect(Collectors.toUnmodifiableSet());
    }

    public static Optional<MaterialDefinition> findByTag(ResourceLocation tag) {
        return materials.values().stream()
                .filter(material -> material.tags().contains(tag))
                .max(Comparator.comparingInt(MaterialDefinition::priority));
    }

    public static Optional<ProcessingRouteDefinition> preferredRoute(MaterialDefinition material) {
        return material.processingRoutes().stream()
                .map(routes::get)
                .filter(java.util.Objects::nonNull)
                .max(Comparator.comparingInt(ProcessingRouteDefinition::priority));
    }

    public static synchronized void replace(
            Map<ResourceLocation, MaterialDefinition> newMaterials,
            Map<ResourceLocation, ProcessingRouteDefinition> newRoutes) {
        materials = Map.copyOf(new LinkedHashMap<>(newMaterials));
        routes = Map.copyOf(new LinkedHashMap<>(newRoutes));
    }
}
