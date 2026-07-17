package raziel23x.projectskyblock.material.reload;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.material.MaterialCategory;
import raziel23x.projectskyblock.material.MaterialDefinition;
import raziel23x.projectskyblock.material.MaterialForm;
import raziel23x.projectskyblock.material.MaterialRegistry;
import raziel23x.projectskyblock.material.ProcessingRouteDefinition;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MaterialDataReloadListener implements ResourceManagerReloadListener {
    public static final MaterialDataReloadListener INSTANCE = new MaterialDataReloadListener();

    private static final Gson GSON = new Gson();
    private static final String MATERIAL_DIRECTORY = "materials";
    private static final String ROUTE_DIRECTORY = "processing_routes";

    private MaterialDataReloadListener() {
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        Map<ResourceLocation, ProcessingRouteDefinition> routes = loadRoutes(resourceManager);
        Map<ResourceLocation, MaterialDefinition> materials = loadMaterials(resourceManager);
        MaterialRegistry.replace(materials, routes);

        ProjectSkyblock.LOGGER.info(
                "Loaded {} Project Skyblock materials and {} processing routes",
                materials.size(),
                routes.size()
        );
    }

    private static Map<ResourceLocation, ProcessingRouteDefinition> loadRoutes(ResourceManager resourceManager) {
        Map<ResourceLocation, ProcessingRouteDefinition> loaded = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, Resource> entry : resourceManager
                .listResources(ROUTE_DIRECTORY, MaterialDataReloadListener::isJson)
                .entrySet()) {
            ResourceLocation id = definitionId(entry.getKey(), ROUTE_DIRECTORY);
            try (Reader reader = entry.getValue().openAsReader()) {
                ProcessingRouteDefinition definition = parseRoute(id, GSON.fromJson(reader, JsonObject.class));
                mergeByPriority(loaded, id, definition, ProcessingRouteDefinition::priority);
            } catch (IOException | RuntimeException exception) {
                ProjectSkyblock.LOGGER.error("Failed to load processing route {}", entry.getKey(), exception);
            }
        }
        return loaded;
    }

    private static Map<ResourceLocation, MaterialDefinition> loadMaterials(ResourceManager resourceManager) {
        Map<ResourceLocation, MaterialDefinition> loaded = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, Resource> entry : resourceManager
                .listResources(MATERIAL_DIRECTORY, MaterialDataReloadListener::isJson)
                .entrySet()) {
            ResourceLocation id = definitionId(entry.getKey(), MATERIAL_DIRECTORY);
            try (Reader reader = entry.getValue().openAsReader()) {
                MaterialDefinition definition = parseMaterial(id, GSON.fromJson(reader, JsonObject.class));
                mergeByPriority(loaded, id, definition, MaterialDefinition::priority);
            } catch (IOException | RuntimeException exception) {
                ProjectSkyblock.LOGGER.error("Failed to load material {}", entry.getKey(), exception);
            }
        }
        return loaded;
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
                path.substring(prefix.length(), path.length() - ".json".length())
        );
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
                automaticRecipes
        );
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
                optionalInt(json, "priority", 0)
        );
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

    private static <T> void mergeByPriority(
            Map<ResourceLocation, T> target,
            ResourceLocation id,
            T candidate,
            java.util.function.ToIntFunction<T> priority) {
        T current = target.get(id);
        if (current == null || priority.applyAsInt(candidate) >= priority.applyAsInt(current)) {
            target.put(id, candidate);
        }
    }
}
