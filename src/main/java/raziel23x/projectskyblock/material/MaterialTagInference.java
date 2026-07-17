package raziel23x.projectskyblock.material;

import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.Optional;

/**
 * Conservative fallback inference for common item-tag paths. Explicit datapack
 * definitions always take precedence over these guesses.
 */
public final class MaterialTagInference {
    private MaterialTagInference() {
    }

    public static Optional<MaterialForm> inferForm(ResourceLocation tag) {
        String path = tag.getPath().toLowerCase(Locale.ROOT);
        if (path.startsWith("ores/")) return Optional.of(MaterialForm.ORE);
        if (path.startsWith("raw_materials/")) return Optional.of(MaterialForm.RAW_MATERIAL);
        if (path.startsWith("ingots/")) return Optional.of(MaterialForm.INGOT);
        if (path.startsWith("nuggets/")) return Optional.of(MaterialForm.NUGGET);
        if (path.startsWith("dusts/")) return Optional.of(MaterialForm.DUST);
        if (path.startsWith("gems/")) return Optional.of(MaterialForm.GEM);
        if (path.startsWith("storage_blocks/")) return Optional.of(MaterialForm.BLOCK);
        if (path.startsWith("plates/")) return Optional.of(MaterialForm.PLATE);
        if (path.startsWith("rods/")) return Optional.of(MaterialForm.ROD);
        if (path.startsWith("gears/")) return Optional.of(MaterialForm.GEAR);
        if (path.startsWith("crops/")) return Optional.of(MaterialForm.CROP);
        if (path.startsWith("seeds/")) return Optional.of(MaterialForm.SEED);
        return Optional.empty();
    }

    public static MaterialCategory inferCategory(ResourceLocation tag, MaterialForm form) {
        String path = tag.getPath().toLowerCase(Locale.ROOT);
        if (path.contains("alloy")) return MaterialCategory.ALLOY;
        if (form == MaterialForm.CROP || form == MaterialForm.SEED) return MaterialCategory.AGRICULTURAL;
        if (form == MaterialForm.GEM || form == MaterialForm.CRYSTAL) return MaterialCategory.CRYSTAL;
        if (form == MaterialForm.ORE || form == MaterialForm.RAW_MATERIAL
                || form == MaterialForm.INGOT || form == MaterialForm.NUGGET
                || form == MaterialForm.PLATE || form == MaterialForm.ROD
                || form == MaterialForm.GEAR) return MaterialCategory.METAL;
        if (form == MaterialForm.DUST || form == MaterialForm.BLOCK) return MaterialCategory.MINERAL;
        return MaterialCategory.UNKNOWN;
    }

    public static Optional<String> inferMaterialName(ResourceLocation tag) {
        String path = tag.getPath();
        int slash = path.indexOf('/');
        if (slash < 0 || slash == path.length() - 1) {
            return Optional.empty();
        }
        return Optional.of(path.substring(slash + 1));
    }
}
