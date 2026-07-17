package raziel23x.projectskyblock.material;

import java.util.Locale;
import java.util.Optional;

public enum MaterialCategory {
    METAL,
    ALLOY,
    MINERAL,
    GEM,
    CRYSTAL,
    CHEMICAL,
    ORGANIC,
    AGRICULTURAL,
    ALCHEMICAL,
    UNKNOWN;

    public static Optional<MaterialCategory> parse(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }

        try {
            return Optional.of(valueOf(value.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }
}
