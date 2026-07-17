package raziel23x.projectskyblock.material;

import java.util.Locale;
import java.util.Optional;

public enum MaterialForm {
    ORE,
    RAW_MATERIAL,
    INGOT,
    NUGGET,
    DUST,
    GEM,
    CRYSTAL,
    PLATE,
    ROD,
    GEAR,
    BLOCK,
    FLUID,
    GAS,
    SOLUTION,
    SLURRY,
    RESIDUE,
    CONCENTRATE,
    CROP,
    SEED,
    REAGENT;

    public static Optional<MaterialForm> parse(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }

        String normalized = value.trim()
                .replace('-', '_')
                .replace(' ', '_')
                .toUpperCase(Locale.ROOT);
        try {
            return Optional.of(valueOf(normalized));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }
}
