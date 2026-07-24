package raziel23x.projectskyblock.platform.neoforge.inventory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemKey;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemState;

/**
 * Registry-aware Minecraft boundary codec for exact simulation item identity.
 *
 * <p>Only the item identifier, quantity, stack limit, and a canonical persistent
 * data-component patch cross the boundary. Component identifiers and registry references are
 * encoded through Minecraft's typed persistence codecs and canonical JSON, not numeric network
 * registry IDs. NBT, SNBT, and Minecraft object references never enter authoritative simulation
 * state.</p>
 */
public final class MinecraftItemStackCodec {
    public static final String COMPONENT_CODEC_ID =
            "projectskyblock:minecraft_data_components_json_v1";
    private static final int MAX_JSON_DEPTH = 64;

    private final Supplier<RegistryAccess> registryAccessSupplier;

    public MinecraftItemStackCodec(Supplier<RegistryAccess> registryAccessSupplier) {
        this.registryAccessSupplier = Objects.requireNonNull(
                registryAccessSupplier,
                "registryAccessSupplier");
    }

    public SimulationItemStack encode(ItemStack stack) {
        Objects.requireNonNull(stack, "stack");
        if (stack.isEmpty()) {
            return SimulationItemStack.empty();
        }

        try {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (itemId == null) {
                throw new ItemStackBoundaryException("cannot encode an unregistered Minecraft item");
            }

            DataComponentPatch patch = canonicalizePatch(stack.getComponentsPatch());
            SimulationItemState state = patch.isEmpty()
                    ? SimulationItemState.none()
                    : SimulationItemState.opaque(COMPONENT_CODEC_ID, encodePatch(patch));
            return SimulationItemStack.of(
                    new SimulationItemKey(itemId.getNamespace(), itemId.getPath(), state),
                    stack.getCount(),
                    stack.getMaxStackSize());
        } catch (ItemStackBoundaryException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new ItemStackBoundaryException("failed to encode Minecraft ItemStack", exception);
        }
    }

    public ItemStack decode(SimulationItemStack stack) {
        Objects.requireNonNull(stack, "stack");
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        try {
            ResourceLocation itemId = ResourceLocation.fromNamespaceAndPath(
                    stack.item().namespace(),
                    stack.item().path());
            Item item = BuiltInRegistries.ITEM.getOptional(itemId)
                    .orElseThrow(() -> new ItemStackBoundaryException(
                            "cannot decode unknown Minecraft item " + itemId));
            int quantity = Math.toIntExact(stack.quantity());
            DataComponentPatch patch = stack.item().hasState()
                    ? decodePatch(stack.item().state())
                    : DataComponentPatch.EMPTY;
            ItemStack decoded = new ItemStack(
                    BuiltInRegistries.ITEM.wrapAsHolder(item),
                    quantity,
                    patch);
            if (decoded.isEmpty() || decoded.getCount() != quantity) {
                throw new ItemStackBoundaryException("decoded Minecraft ItemStack lost its quantity");
            }
            if (quantity > decoded.getMaxStackSize()) {
                throw new ItemStackBoundaryException(
                        "decoded stack quantity exceeds its Minecraft stack limit");
            }
            if (stack.maximumStackSize() != decoded.getMaxStackSize()) {
                throw new ItemStackBoundaryException(
                        "simulation and Minecraft stack limits disagree for " + itemId
                                + ": " + stack.maximumStackSize()
                                + " != " + decoded.getMaxStackSize());
            }
            return decoded;
        } catch (ItemStackBoundaryException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new ItemStackBoundaryException("failed to decode simulation ItemStack", exception);
        }
    }

    private byte[] encodePatch(DataComponentPatch patch) {
        rejectTransientComponents(patch);
        RegistryOps<JsonElement> operations = RegistryOps.create(
                JsonOps.INSTANCE,
                registryAccess());
        JsonElement encoded = DataComponentPatch.CODEC
                .encodeStart(operations, patch)
                .getOrThrow(message -> new ItemStackBoundaryException(
                        "failed to encode data-component patch: " + message));
        JsonElement canonical = canonicalizeJson(encoded, 0);

        DataComponentPatch verified = DataComponentPatch.CODEC
                .parse(operations, canonical)
                .getOrThrow(message -> new ItemStackBoundaryException(
                        "failed to verify data-component patch: " + message));
        if (!verified.equals(patch)) {
            throw new ItemStackBoundaryException(
                    "data-component patch is not losslessly persistent");
        }

        byte[] payload = canonical.toString().getBytes(StandardCharsets.UTF_8);
        if (payload.length <= 0 || payload.length > SimulationItemState.MAX_PAYLOAD_BYTES) {
            throw new ItemStackBoundaryException(
                    "encoded data-component payload has invalid size " + payload.length);
        }
        return payload;
    }

    private DataComponentPatch decodePatch(SimulationItemState state) {
        if (!COMPONENT_CODEC_ID.equals(state.codecId())) {
            throw new ItemStackBoundaryException(
                    "unsupported simulation item-state codec " + state.codecId());
        }

        byte[] payload = state.payload();
        String encoded = decodeUtf8Strict(payload);
        final JsonElement parsed;
        try {
            parsed = JsonParser.parseString(encoded);
        } catch (RuntimeException exception) {
            throw new ItemStackBoundaryException(
                    "data-component payload is not valid JSON",
                    exception);
        }

        JsonElement canonical = canonicalizeJson(parsed, 0);
        byte[] canonicalPayload = canonical.toString().getBytes(StandardCharsets.UTF_8);
        if (!Arrays.equals(payload, canonicalPayload)) {
            throw new ItemStackBoundaryException(
                    "data-component payload is not in canonical form");
        }

        RegistryOps<JsonElement> operations = RegistryOps.create(
                JsonOps.INSTANCE,
                registryAccess());
        DataComponentPatch patch = DataComponentPatch.CODEC
                .parse(operations, canonical)
                .getOrThrow(message -> new ItemStackBoundaryException(
                        "failed to decode data-component patch: " + message));
        if (patch.isEmpty()) {
            throw new ItemStackBoundaryException(
                    "empty data-component patch must use stateless item identity");
        }
        rejectTransientComponents(patch);
        if (!Arrays.equals(payload, encodePatch(patch))) {
            throw new ItemStackBoundaryException(
                    "data-component payload does not round-trip canonically");
        }
        return canonicalizePatch(patch);
    }

    private RegistryAccess registryAccess() {
        RegistryAccess registryAccess = registryAccessSupplier.get();
        if (registryAccess == null) {
            throw new ItemStackBoundaryException("Minecraft registry access is unavailable");
        }
        return registryAccess;
    }

    private static String decodeUtf8Strict(byte[] payload) {
        try {
            return StandardCharsets.UTF_8
                    .newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(payload))
                    .toString();
        } catch (CharacterCodingException exception) {
            throw new ItemStackBoundaryException(
                    "data-component payload is not valid UTF-8",
                    exception);
        }
    }

    private static DataComponentPatch canonicalizePatch(DataComponentPatch patch) {
        Objects.requireNonNull(patch, "patch");
        if (patch.isEmpty()) {
            return DataComponentPatch.EMPTY;
        }

        List<Map.Entry<DataComponentType<?>, Optional<?>>> entries =
                new ArrayList<>(patch.entrySet());
        entries.sort(Comparator.comparing(entry -> componentId(entry.getKey()).toString()));

        DataComponentPatch.Builder builder = DataComponentPatch.builder();
        for (Map.Entry<DataComponentType<?>, Optional<?>> entry : entries) {
            addCanonicalEntry(builder, entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    private static JsonElement canonicalizeJson(JsonElement element, int depth) {
        Objects.requireNonNull(element, "element");
        if (depth > MAX_JSON_DEPTH) {
            throw new ItemStackBoundaryException(
                    "data-component payload exceeds maximum JSON depth " + MAX_JSON_DEPTH);
        }
        if (element.isJsonObject()) {
            JsonObject canonical = new JsonObject();
            element.getAsJsonObject().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> canonical.add(
                            entry.getKey(),
                            canonicalizeJson(entry.getValue(), depth + 1)));
            return canonical;
        }
        if (element.isJsonArray()) {
            JsonArray canonical = new JsonArray();
            for (JsonElement child : element.getAsJsonArray()) {
                canonical.add(canonicalizeJson(child, depth + 1));
            }
            return canonical;
        }
        return element.deepCopy();
    }

    private static void rejectTransientComponents(DataComponentPatch patch) {
        for (Map.Entry<DataComponentType<?>, Optional<?>> entry : patch.entrySet()) {
            if (entry.getKey().isTransient()) {
                throw new ItemStackBoundaryException(
                        "transient data component cannot cross the persistent engine boundary: "
                                + componentId(entry.getKey()));
            }
        }
    }

    private static ResourceLocation componentId(DataComponentType<?> component) {
        ResourceLocation componentId = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component);
        if (componentId == null) {
            throw new ItemStackBoundaryException("cannot encode an unregistered data component");
        }
        return componentId;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void addCanonicalEntry(
            DataComponentPatch.Builder builder,
            DataComponentType<?> component,
            Optional<?> value) {
        if (value.isPresent()) {
            builder.set((DataComponentType) component, value.get());
        } else {
            builder.remove((DataComponentType) component);
        }
    }
}
