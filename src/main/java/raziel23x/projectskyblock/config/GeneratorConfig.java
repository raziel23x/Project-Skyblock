package raziel23x.projectskyblock.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class GeneratorConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue COBBLESTONE_CAPACITY;
    public static final ModConfigSpec.IntValue COBBLESTONE_INTERVAL_TICKS;
    public static final ModConfigSpec.IntValue COBBLESTONE_PER_CYCLE;
    public static final ModConfigSpec.BooleanValue COBBLESTONE_AUTO_PUSH_UP;
    public static final ModConfigSpec.BooleanValue COBBLESTONE_PIPE_EXTRACTION;
    public static final ModConfigSpec.BooleanValue COBBLESTONE_MANUAL_EXTRACTION;

    public static final ModConfigSpec.IntValue WATER_CAPACITY_MB;
    public static final ModConfigSpec.IntValue WATER_INTERVAL_TICKS;
    public static final ModConfigSpec.IntValue WATER_PER_CYCLE_MB;
    public static final ModConfigSpec.BooleanValue WATER_PIPE_EXTRACTION;
    public static final ModConfigSpec.BooleanValue WATER_BUCKET_EXTRACTION;

    public static final ModConfigSpec.IntValue LAVA_CAPACITY_MB;
    public static final ModConfigSpec.IntValue LAVA_INTERVAL_TICKS;
    public static final ModConfigSpec.IntValue LAVA_PER_CYCLE_MB;
    public static final ModConfigSpec.BooleanValue LAVA_PIPE_EXTRACTION;
    public static final ModConfigSpec.BooleanValue LAVA_BUCKET_EXTRACTION;

    public static final ModConfigSpec.BooleanValue PARTICLES_ENABLED;
    public static final ModConfigSpec.IntValue COBBLESTONE_PARTICLE_CHANCE;
    public static final ModConfigSpec.IntValue WATER_PARTICLE_CHANCE;
    public static final ModConfigSpec.IntValue LAVA_PARTICLE_CHANCE;

    public static final ModConfigSpec.BooleanValue DEBUG_LOGGING;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.comment("Settings for the Cobblestone Generator.")
                .push("cobblestone");

        COBBLESTONE_CAPACITY = BUILDER
                .comment("Internal cobblestone capacity. Minecraft item stacks cannot exceed 64.")
                .defineInRange("capacity", 64, 1, 64);

        COBBLESTONE_INTERVAL_TICKS = BUILDER
                .comment("Ticks between cobblestone generation cycles.")
                .defineInRange("intervalTicks", 20, 1, 72_000);

        COBBLESTONE_PER_CYCLE = BUILDER
                .comment("Cobblestone generated per cycle.")
                .defineInRange("amountPerCycle", 1, 1, 64);

        COBBLESTONE_AUTO_PUSH_UP = BUILDER
                .comment("Automatically push cobblestone into an item handler directly above.")
                .define("autoPushUp", true);

        COBBLESTONE_PIPE_EXTRACTION = BUILDER
                .comment("Expose the internal inventory to item pipes.")
                .define("allowPipeExtraction", true);

        COBBLESTONE_MANUAL_EXTRACTION = BUILDER
                .comment("Allow empty-hand right-click extraction.")
                .define("allowManualExtraction", true);

        BUILDER.pop();

        BUILDER.comment("Settings for the Water Generator.")
                .push("water");

        WATER_CAPACITY_MB = BUILDER
                .comment("Internal water capacity in millibuckets. 1000 mB = 1 bucket.")
                .defineInRange("capacityMb", 8_000, 1_000, 16_000_000);

        WATER_INTERVAL_TICKS = BUILDER
                .comment("Ticks between water generation cycles.")
                .defineInRange("intervalTicks", 20, 1, 72_000);

        WATER_PER_CYCLE_MB = BUILDER
                .comment("Water generated per cycle in millibuckets.")
                .defineInRange("amountPerCycleMb", 1_000, 1, 1_000_000);

        WATER_PIPE_EXTRACTION = BUILDER
                .comment("Expose the internal tank to fluid pipes.")
                .define("allowPipeExtraction", true);

        WATER_BUCKET_EXTRACTION = BUILDER
                .comment("Allow bucket extraction.")
                .define("allowBucketExtraction", true);

        BUILDER.pop();

        BUILDER.comment("Settings for the Lava Generator.")
                .push("lava");

        LAVA_CAPACITY_MB = BUILDER
                .comment("Internal lava capacity in millibuckets. 1000 mB = 1 bucket.")
                .defineInRange("capacityMb", 8_000, 1_000, 16_000_000);

        LAVA_INTERVAL_TICKS = BUILDER
                .comment("Ticks between lava generation cycles.")
                .defineInRange("intervalTicks", 20, 1, 72_000);

        LAVA_PER_CYCLE_MB = BUILDER
                .comment("Lava generated per cycle in millibuckets.")
                .defineInRange("amountPerCycleMb", 1_000, 1, 1_000_000);

        LAVA_PIPE_EXTRACTION = BUILDER
                .comment("Expose the internal tank to fluid pipes.")
                .define("allowPipeExtraction", true);

        LAVA_BUCKET_EXTRACTION = BUILDER
                .comment("Allow bucket extraction.")
                .define("allowBucketExtraction", true);

        BUILDER.pop();

        BUILDER.comment("Client-side ambient generator effects.")
                .push("particles");

        PARTICLES_ENABLED = BUILDER
                .comment("Enable ambient particles for all resource generators.")
                .define("enabled", true);

        COBBLESTONE_PARTICLE_CHANCE = BUILDER
                .comment("One-in-N random animate ticks create cobblestone particles. Larger is quieter.")
                .defineInRange("cobblestoneChance", 6, 1, 1_000);

        WATER_PARTICLE_CHANCE = BUILDER
                .comment("One-in-N random animate ticks create water particles. Larger is quieter.")
                .defineInRange("waterChance", 6, 1, 1_000);

        LAVA_PARTICLE_CHANCE = BUILDER
                .comment("One-in-N random animate ticks create lava particles. Larger is quieter.")
                .defineInRange("lavaChance", 4, 1, 1_000);

        BUILDER.pop();

        BUILDER.comment("Developer and troubleshooting options.")
                .push("debug");

        DEBUG_LOGGING = BUILDER
                .comment("Log generator production, storage, and auto-output activity.")
                .define("enableLogging", false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private GeneratorConfig() {
    }
}
