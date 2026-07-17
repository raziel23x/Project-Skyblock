package raziel23x.projectskyblock.material;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Set;

public record MaterialDefinition(
        ResourceLocation id,
        MaterialCategory category,
        Set<MaterialForm> forms,
        List<ResourceLocation> processingRoutes,
        ResourceLocation journalChapter,
        String originMod,
        Set<ResourceLocation> tags,
        Set<String> aliases,
        int priority,
        boolean automaticRecipes) {

    public MaterialDefinition {
        forms = Set.copyOf(forms);
        processingRoutes = List.copyOf(processingRoutes);
        tags = Set.copyOf(tags);
        aliases = Set.copyOf(aliases);
        originMod = originMod == null || originMod.isBlank() ? id.getNamespace() : originMod;
    }
}
