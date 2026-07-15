package raziel23x.projectskyblock.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class CommonConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue REPAIR_GEM_ENABLED;
    public static final ModConfigSpec.IntValue REPAIR_GEM_INTERVAL_TICKS;
    public static final ModConfigSpec.IntValue REPAIR_GEM_AMOUNT;
    public static final ModConfigSpec.BooleanValue REPAIR_GEM_DROPPED_PARTICLES;
    public static final ModConfigSpec.IntValue REPAIR_GEM_DROPPED_PARTICLE_INTERVAL;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.comment("General Project Skyblock gameplay settings.")
                .push("repair_gem");

        REPAIR_GEM_ENABLED = BUILDER
                .comment("Enable the Repair Gem's passive inventory repair behavior.")
                .define("enabled", true);

        REPAIR_GEM_INTERVAL_TICKS = BUILDER
                .comment("Ticks between Repair Gem repair attempts. 20 ticks = 1 second.")
                .defineInRange("intervalTicks", 20, 1, 72_000);

        REPAIR_GEM_AMOUNT = BUILDER
                .comment("Durability restored on each successful Repair Gem cycle.")
                .defineInRange("repairAmount", 1, 1, 10_000);

        REPAIR_GEM_DROPPED_PARTICLES = BUILDER
                .comment("Give a dropped Repair Gem a subtle green magical aura.")
                .define("droppedParticles", true);

        REPAIR_GEM_DROPPED_PARTICLE_INTERVAL = BUILDER
                .comment("Ticks between dropped Repair Gem particle pulses.")
                .defineInRange("droppedParticleIntervalTicks", 10, 1, 1_200);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private CommonConfig() {
    }
}
