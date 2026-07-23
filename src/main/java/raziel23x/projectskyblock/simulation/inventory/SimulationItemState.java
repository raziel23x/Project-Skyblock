package raziel23x.projectskyblock.simulation.inventory;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Immutable opaque adapter-owned state attached to one simulation item identity.
 *
 * <p>The simulation never interprets the payload. A platform adapter must use a stable codec
 * identifier and an exact deterministic byte representation for any component-bearing item. The
 * byte representation must come from a documented typed/component codec; NBT and SNBT are never
 * valid runtime payloads. This prevents differently damaged, enchanted, named, or otherwise
 * stateful items from merging while keeping Minecraft data-component classes and persistence
 * formats outside the engine.</p>
 */
public final class SimulationItemState implements Comparable<SimulationItemState> {
    public static final int MAX_PAYLOAD_BYTES = 64 * 1024;

    private static final Pattern CODEC_PATTERN = Pattern.compile("[a-z0-9_.-]+:[a-z0-9/._-]+");
    private static final Pattern FORBIDDEN_NBT_CODEC =
            Pattern.compile("(?:^|[/:._-])s?nbt(?:$|[/:._-])");
    private static final SimulationItemState NONE = new SimulationItemState("", new byte[0]);

    private final String codecId;
    private final byte[] payload;
    private final int hashCode;

    public SimulationItemState(String codecId, byte[] payload) {
        this.codecId = Objects.requireNonNull(codecId, "codecId");
        this.payload = Objects.requireNonNull(payload, "payload").clone();
        if (this.payload.length > MAX_PAYLOAD_BYTES) {
            throw new IllegalArgumentException(
                    "item-state payload exceeds " + MAX_PAYLOAD_BYTES + " bytes");
        }
        if (codecId.isEmpty()) {
            if (payload.length != 0) {
                throw new IllegalArgumentException("stateless item identity must not contain a payload");
            }
        } else {
            if (!CODEC_PATTERN.matcher(codecId).matches()) {
                throw new IllegalArgumentException("item-state codec id must use namespace:path format");
            }
            if (FORBIDDEN_NBT_CODEC.matcher(codecId).find()) {
                throw new IllegalArgumentException("NBT and SNBT codecs are forbidden in simulation item state");
            }
            if (payload.length == 0) {
                throw new IllegalArgumentException("stateful item identity must contain a payload");
            }
        }
        this.hashCode = 31 * codecId.hashCode() + Arrays.hashCode(this.payload);
    }

    public static SimulationItemState none() {
        return NONE;
    }

    public static SimulationItemState opaque(String codecId, byte[] payload) {
        return new SimulationItemState(codecId, payload);
    }

    public String codecId() {
        return codecId;
    }

    public byte[] payload() {
        return payload.clone();
    }

    public int payloadSize() {
        return payload.length;
    }

    public boolean isEmpty() {
        return codecId.isEmpty();
    }

    @Override
    public int compareTo(SimulationItemState other) {
        Objects.requireNonNull(other, "other");
        int codecComparison = codecId.compareTo(other.codecId);
        if (codecComparison != 0) {
            return codecComparison;
        }
        return Arrays.compareUnsigned(payload, other.payload);
    }

    @Override
    public boolean equals(Object other) {
        return this == other
                || other instanceof SimulationItemState state
                && hashCode == state.hashCode
                && codecId.equals(state.codecId)
                && Arrays.equals(payload, state.payload);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return isEmpty() ? "stateless" : codecId + "[" + payload.length + " bytes]";
    }
}
