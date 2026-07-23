package raziel23x.projectskyblock.material;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

/** Read-only access to the last fully validated material-data snapshot. */
public final class MaterialRegistry {
    private static final Comparator<MaterialDefinition> MATERIAL_PREFERENCE =
            Comparator.comparingInt(MaterialDefinition::priority)
                    .reversed()
                    .thenComparing(material -> material.id().toString());
    private static final Comparator<ProcessingRouteDefinition> ROUTE_PREFERENCE =
            Comparator.comparingInt(ProcessingRouteDefinition::priority)
                    .reversed()
                    .thenComparing(route -> route.id().toString());

    private static volatile MaterialRegistrySnapshot snapshot = MaterialRegistrySnapshot.empty();

    private MaterialRegistry() {
    }

    public static MaterialRegistrySnapshot snapshot() {
        return snapshot;
    }

    public static Optional<MaterialDefinition> getMaterial(ResourceLocation id) {
        return Optional.ofNullable(snapshot.materials().get(Objects.requireNonNull(id, "id")));
    }

    public static Optional<ProcessingRouteDefinition> getRoute(ResourceLocation id) {
        return Optional.ofNullable(snapshot.routes().get(Objects.requireNonNull(id, "id")));
    }

    public static Collection<MaterialDefinition> materials() {
        return snapshot.materials().values();
    }

    public static Collection<ProcessingRouteDefinition> routes() {
        return snapshot.routes().values();
    }

    public static Set<MaterialDefinition> materialsInCategory(MaterialCategory category) {
        Objects.requireNonNull(category, "category");
        LinkedHashSet<MaterialDefinition> matches = new LinkedHashSet<>();
        for (MaterialDefinition material : snapshot.materials().values()) {
            if (material.category() == category) {
                matches.add(material);
            }
        }
        return Collections.unmodifiableSet(matches);
    }

    public static Optional<MaterialDefinition> findByTag(ResourceLocation tag) {
        Objects.requireNonNull(tag, "tag");
        return snapshot.materials().values().stream()
                .filter(material -> material.tags().contains(tag))
                .min(MATERIAL_PREFERENCE);
    }

    public static Optional<ProcessingRouteDefinition> preferredRoute(MaterialDefinition material) {
        Objects.requireNonNull(material, "material");
        Map<ResourceLocation, ProcessingRouteDefinition> routes = snapshot.routes();
        return material.processingRoutes().stream()
                .map(routes::get)
                .filter(Objects::nonNull)
                .min(ROUTE_PREFERENCE);
    }

    /** Publishes one complete prevalidated generation through a single volatile write. */
    public static synchronized void publish(MaterialRegistrySnapshot newSnapshot) {
        snapshot = Objects.requireNonNull(newSnapshot, "newSnapshot");
    }
}
