package raziel23x.projectskyblock.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class IntegrationConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue CURIOS_REPAIR_GEM;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.comment("Optional Curios compatibility. Curios is never required.")
                .push("curios");

        CURIOS_REPAIR_GEM = BUILDER
                .comment("Allow a Repair Gem equipped in a Curios slot to function when Curios is installed.")
                .define("enableRepairGem", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private IntegrationConfig() {
    }
}
