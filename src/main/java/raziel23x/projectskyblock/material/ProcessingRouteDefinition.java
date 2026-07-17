package raziel23x.projectskyblock.material;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Set;

public record ProcessingRouteDefinition(
        ResourceLocation id,
        MaterialCategory category,
        List<String> stages,
        Set<MaterialForm> acceptedForms,
        Set<MaterialForm> producedForms,
        int priority) {

    public ProcessingRouteDefinition {
        stages = List.copyOf(stages);
        acceptedForms = Set.copyOf(acceptedForms);
        producedForms = Set.copyOf(producedForms);
    }
}
