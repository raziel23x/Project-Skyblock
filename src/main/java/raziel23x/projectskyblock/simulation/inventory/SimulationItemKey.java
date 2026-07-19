package raziel23x.projectskyblock.simulation.inventory;

import java.util.Objects;
import java.util.regex.Pattern;

/** Stable Minecraft-independent identity for an inventory item type. */
public record SimulationItemKey(String namespace, String path) implements Comparable<SimulationItemKey> {
    private static final Pattern NAMESPACE_PATTERN = Pattern.compile("[a-z0-9_.-]+");
    private static final Pattern PATH_PATTERN = Pattern.compile("[a-z0-9/._-]+");

    public SimulationItemKey {
        namespace = requirePart(namespace, "namespace", NAMESPACE_PATTERN);
        path = requirePart(path, "path", PATH_PATTERN);
    }

    public static SimulationItemKey parse(String value) {
        Objects.requireNonNull(value, "value");
        int separator = value.indexOf(':');
        if (separator <= 0 || separator == value.length() - 1 || value.indexOf(':', separator + 1) >= 0) {
            throw new IllegalArgumentException("item key must use namespace:path format");
        }
        return new SimulationItemKey(value.substring(0, separator), value.substring(separator + 1));
    }

    public String serializedName() {
        return namespace + ':' + path;
    }

    @Override
    public int compareTo(SimulationItemKey other) {
        Objects.requireNonNull(other, "other");
        int namespaceComparison = namespace.compareTo(other.namespace);
        return namespaceComparison != 0 ? namespaceComparison : path.compareTo(other.path);
    }

    @Override
    public String toString() {
        return serializedName();
    }

    private static String requirePart(String value, String name, Pattern pattern) {
        Objects.requireNonNull(value, name);
        if (!pattern.matcher(value).matches()) {
            throw new IllegalArgumentException(name + " contains invalid characters: " + value);
        }
        return value;
    }
}
