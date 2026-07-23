package raziel23x.projectskyblock.material.reload;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.material.MaterialCategory;
import raziel23x.projectskyblock.material.MaterialDefinition;
import raziel23x.projectskyblock.material.MaterialForm;
import raziel23x.projectskyblock.material.MaterialRegistry;
import raziel23x.projectskyblock.material.MaterialRegistrySnapshot;
import raziel23x.projectskyblock.material.ProcessingRouteDefinition;

/** Builds and atomically publishes one deterministic last-known-good material snapshot. */
public final class MaterialDataReloadListener implements ResourceManagerReloadListener {
    public static final MaterialDataReloadListener INSTANCE = new MaterialDataReloadListener();

    private static final Gson GSON = new Gson();
    /**
     * Mod-specific roots avoid interpreting another mod's unrelated generic data folder as a
     * Project Skyblock definition. Addons may contribute under their own namespace by using these
     * same roots.
     */
    private static final String MATERIAL_DIRECTORY = "projectskyblock/materials";
    private static final String ROUTE_DIRECTORY = "projectskyblock/processing_routes";
    private static final Comparator<Map.Entry<ResourceLocation, Resource>> SOURCE_ORDER =
            Comparator.comparing(entry -> entry.getKey().toString());

    private MaterialDataReloadListener() {
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        try {
            reloadCandidate(resourceManager);
        } catch (RuntimeException exception) {
            retainLastKnownGood(
                    List.of("unexpected material reload failure ("
                            + exception.getClass().getSimpleName() + ": " + exception.getMessage() + ")"),
                    exception);
        }
    }

    private static void reloadCandidate(ResourceManager resourceManager) {
        LoadResult<ProcessingRouteDefinition> routes = loadRoutes(resourceManager);
        LoadResult<MaterialDefinition> materials = loadMaterials(resourceManager);
        List<String> errors = new ArrayList<>(routes.errors());
        errors.addAll(materials.errors());
        if (materials.definitions().isEmpty() && routes.definitions().isEmpty()) {
            errors.add("material-data candidate contains no materials or processing routes");
        }
        validateReferences(materials.definitions(), routes.definitions(), errors);

        if (!errors.isEmpty()) {
            retainLastKnownGood(errors, null);
            return;
        }

        MaterialRegistrySnapshot candidate = new MaterialRegistrySnapshot(
                materials.definitions(),
                routes.definitions(),
                materials.sources(),
                routes.sources());
        MaterialRegistry.publish(candidate);
        ProjectSkyblock.LOGGER.info(
                "Published {} Project Skyblock materials and {} processing routes as one validated snapshot",
                candidate.materials().size(),
                candidate.routes().size());
    }

    private static void retainLastKnownGood(List<String> errors, RuntimeException exception) {
        for (String error : errors) {
            ProjectSkyblock.LOGGER.error("Project Skyblock material reload rejected: {}", error);
        }
        MaterialRegistrySnapshot retained = MaterialRegistry.snapshot();
        if (exception == null) {
            ProjectSkyblock.LOGGER.error(
                    "Rejected material-data candidate with {} error(s); retaining last-known-good snapshot of {} materials and {} routes",
                    errors.size(),
                    retained.materials().size(),
                    retained.routes().size());
        } else {
            ProjectSkyblock.LOGGER.error(
                    "Rejected material-data candidate after unexpected failure; retaining last-known-good snapshot of {} materials and {} routes",
                    retained.materials().size(),
                    retained.routes().size(),
                    exception);
        }
    }

    private static LoadResult<ProcessingRouteDefinition> loadRoutes(ResourceManager resourceManager) {
        Map<ResourceLocation, ProcessingRouteDefinition> loaded = new LinkedHashMap<>();
        Map<ResourceLocation, ResourceLocation> sources = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();
        sortedResources(resourceManager, ROUTE_DIRECTORY).forEach(entry -> {
            ResourceLocation source = entry.getKey();
            ResourceLocation id;
            try {
                id = definitionId(source, ROUTE_DIRECTORY);
            } catch (RuntimeException exception) {
                errors.add(describeFailure("processing route", source, exception));
                return;
            }
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonObject json = requireObject(GSON.fromJson(reader, JsonElement.class), source);
                ProcessingRouteDefinition definition = parseRoute(id, json);
                loaded.put(id, definition);
                sources.put(id, source);
            } catch (IOException | RuntimeException exception) {
                errors.add(describeFailure("processing route", source, exception));
            }
        });
        return new LoadResult<>(loaded, sources, errors);
    }

    private static LoadResult<MaterialDefinition> loadMaterials(ResourceManager resourceManager) {
        Map<ResourceLocation, MaterialDefinition> loaded = new LinkedHashMap<>();
        Map<ResourceLocation, ResourceLocation> sources = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();
        sortedResources(resourceManager, MATERIAL_DIRECTORY).forEach(entry -> {
            ResourceLocation source = entry.getKey();
            ResourceLocation id;
            try {
                id = definitionId(source, MATERIAL_DIRECTORY);
            } catch (RuntimeException exception) {
                errors.add(describeFailure("material", source, exception));
                return;
            }
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonObject json = requireObject(GSON.fromJson(reader, JsonElement.class), source);
                MaterialDefinition definition = parseMaterial(id, json);
                loaded.put(id, definition);
                sources.put(id, source);
            } catch (IOException | RuntimeException exception) {
                errors.add(describeFailure("material", source, exception));
            }
        });
        return new LoadResult<>(loaded, sources, errors);
    }

    private static List<Map.Entry<ResourceLocation, Resource>> sortedResources(
            ResourceManager resourceManager,
            String directory) {
        return resourceManager.listResources(directory, MaterialDataReloadListener::isJson)
                .entrySet()
                .stream()
                .sorted(SOURCE_ORDER)
                .toList();
    }

    private static JsonObject requireObject(JsonElement element, ResourceLocation source) {
        if (element == null || !element.isJsonObject()) {
            throw new JsonParseException("Definition must be a JSON object: " + source);
        }
        return element.getAsJsonObject();
    }

    private static void validateReferences(
            Map<ResourceLocation, MaterialDefinition> materials,
            Map<ResourceLocation, ProcessingRouteDefinition> routes,
            List<String> errors) {
        for (MaterialDefinition material : materials.values()) {
            for (ResourceLocation routeId : material.processingRoutes()) {
                ProcessingRouteDefinition route = routes.get(routeId);
                if (route == null) {
                    errors.add("material " + material.id() + " references missing route " + routeId);
                } else if (route.category() != material.category()) {
                    errors.add("material " + material.id() + " category " + material.category()
                            + " does not match route " + routeId + " category " + route.category());
                }
            }
        }
    }

    private static boolean isJson(ResourceLocation location) {
        return location.getPath().endsWith(".json");
    }

    private static ResourceLocation definitionId(ResourceLocation file, String directory) {
        String prefix = directory + "/";
        String path = file.getPath();
        if (!path.startsWith(prefix) || !path.endsWith(".json")) {
            throw new JsonParseException("Unexpected definition path: " + file);
        }
        return ResourceLocation.fromNamespaceAndPath(
                file.getNamespace(),
                path.substring(prefix.length(), path.length() - ".json".length()));
    }

    private static MaterialDefinition parseMaterial(ResourceLocation id, JsonObject json) {
        MaterialCategory category = MaterialCategory.parse(requiredString(json, "category"))
                .orElseThrow(() -> new JsonParseException("Unknown material category in " + id));

        Set<MaterialForm> forms = new LinkedHashSet<>();
        for (String value : stringArray(json, "forms")) {
            forms.add(MaterialForm.parse(value)
                    .orElseThrow(() -> new JsonParseException("Unknown material form '" + value + "' in " + id)));
        }

        List<ResourceLocation> routes = resourceLocationArray(json, "processing_routes");
        Set<ResourceLocation> tags = new LinkedHashSet<>(resourceLocationArray(json, "tags"));
        Set<String> aliases = new LinkedHashSet<>(stringArray(json, "aliases"));

        ResourceLocation journalChapter = optionalResourceLocation(json, "journal_chapter");
        String originMod = optionalString(json, "origin_mod", id.getNamespace());
        int priority = optionalInt(json, "priority", 0);
        boolean automaticRecipes = optionalBoolean(json, "automatic_recipes", true);

        return new MaterialDefinition(
                id,
                category,
                forms,
                routes,
                journalChapter,
                originMod,
                tags,
                aliases,
                priority,
                automaticRecipes);
    }

    private static ProcessingRouteDefinition parseRoute(ResourceLocation id, JsonObject json) {
        MaterialCategory category = MaterialCategory.parse(requiredString(json, "category"))
                .orElseThrow(() -> new JsonParseException("Unknown route category in " + id));
        List<String> stages = stringArray(json, "stages");
        if (stages.isEmpty()) {
            throw new JsonParseException("Processing route " + id + " must contain at least one stage");
        }

        Set<MaterialForm> accepted = parseForms(json, "accepted_forms", id);
        Set<MaterialForm> produced = parseForms(json, "produced_forms", id);
        return new ProcessingRouteDefinition(
                id,
                category,
                stages,
                accepted,
                produced,
                optionalInt(json, "priority", 0));
    }

    private static Set<MaterialForm> parseForms(JsonObject json, String key, ResourceLocation id) {
        Set<MaterialForm> result = new LinkedHashSet<>();
        for (String value : stringArray(json, key)) {
            result.add(MaterialForm.parse(value)
                    .orElseThrow(() -> new JsonParseException("Unknown form '" + value + "' in " + id)));
        }
        return result;
    }

    private static String requiredString(JsonObject json, String key) {
        if (!json.has(key) || !json.get(key).isJsonPrimitive()) {
            throw new JsonParseException("Missing required string '" + key + "'");
        }
        return json.get(key).getAsString();
    }

    private static String optionalString(JsonObject json, String key, String fallback) {
        return json.has(key) ? json.get(key).getAsString() : fallback;
    }

    private static int optionalInt(JsonObject json, String key, int fallback) {
        return json.has(key) ? json.get(key).getAsInt() : fallback;
    }

    private static boolean optionalBoolean(JsonObject json, String key, boolean fallback) {
        return json.has(key) ? json.get(key).getAsBoolean() : fallback;
    }

    private static ResourceLocation optionalResourceLocation(JsonObject json, String key) {
        return json.has(key) ? parseResourceLocation(json.get(key).getAsString(), key) : null;
    }

    private static List<String> stringArray(JsonObject json, String key) {
        if (!json.has(key)) {
            return List.of();
        }
        JsonElement element = json.get(key);
        if (!element.isJsonArray()) {
            throw new JsonParseException("'" + key + "' must be an array");
        }
        List<String> result = new ArrayList<>();
        JsonArray array = element.getAsJsonArray();
        for (JsonElement value : array) {
            if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
                throw new JsonParseException("'" + key + "' must contain only strings");
            }
            result.add(value.getAsString());
        }
        return result;
    }

    private static List<ResourceLocation> resourceLocationArray(JsonObject json, String key) {
        List<ResourceLocation> result = new ArrayList<>();
        for (String value : stringArray(json, key)) {
            result.add(parseResourceLocation(value, key));
        }
        return result;
    }

    private static ResourceLocation parseResourceLocation(String value, String field) {
        ResourceLocation location = ResourceLocation.tryParse(value);
        if (location == null) {
            throw new JsonParseException("Invalid resource location '" + value + "' in '" + field + "'");
        }
        return location;
    }

    private static String describeFailure(
            String kind,
            ResourceLocation source,
            Exception exception) {
        String message = exception.getMessage();
        return "failed to load " + kind + " " + source + " ("
                + exception.getClass().getSimpleName() + (message == null ? "" : ": " + message) + ")";
    }

    private record LoadResult<T>(
            Map<ResourceLocation, T> definitions,
            Map<ResourceLocation, ResourceLocation> sources,
            List<String> errors) {
        private LoadResult {
            definitions = Collections.unmodifiableMap(new LinkedHashMap<>(definitions));
            sources = Collections.unmodifiableMap(new LinkedHashMap<>(sources));
            errors = List.copyOf(errors);
        }
    }
}
