package raziel23x.projectskyblock.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class MachineConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_FUEL_POWER;
    public static final ModConfigSpec.BooleanValue ENABLE_FE_POWER;
    public static final ModConfigSpec.BooleanValue PREFER_FE;

    public static final ModConfigSpec.IntValue CRUSHER_PROCESS_TIME;
    public static final ModConfigSpec.IntValue CRUSHER_FE_CAPACITY;
    public static final ModConfigSpec.IntValue CRUSHER_FE_PER_TICK;
    public static final ModConfigSpec.DoubleValue FUEL_BURN_MULTIPLIER;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.comment("Shared machine power settings.")
                .push("power");

        ENABLE_FUEL_POWER = BUILDER
                .comment("Allow machines to run from furnace fuels.")
                .define("enableFuelPower", true);

        ENABLE_FE_POWER = BUILDER
                .comment("Allow machines to receive and consume Forge Energy.")
                .define("enableFEPower", true);

        PREFER_FE = BUILDER
                .comment("Use stored FE before consuming furnace fuel when both are available.")
                .define("preferFE", true);

        FUEL_BURN_MULTIPLIER = BUILDER
                .comment("Multiplier applied to furnace fuel burn time.")
                .defineInRange("fuelBurnMultiplier", 1.0D, 0.05D, 100.0D);

        BUILDER.pop();

        BUILDER.comment("Cobblestone Crusher settings.")
                .push("cobblestone_crusher");

        CRUSHER_PROCESS_TIME = BUILDER
                .comment("Ticks required to crush one item. 20 ticks = 1 second.")
                .defineInRange("processTimeTicks", 80, 1, 72_000);

        CRUSHER_FE_CAPACITY = BUILDER
                .comment("Internal Forge Energy capacity.")
                .defineInRange("energyCapacity", 10_000, 0, Integer.MAX_VALUE);

        CRUSHER_FE_PER_TICK = BUILDER
                .comment("Forge Energy consumed for each processing tick.")
                .defineInRange("energyPerTick", 20, 0, 1_000_000);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private MachineConfig() {
    }
}
