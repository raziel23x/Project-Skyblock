package raziel23x.projectskyblock.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class PSConfigBuilder {

    public static ForgeConfigSpec.IntValue REPAIR_GEM_DELAY;

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER) {
        REPAIR_GEM_DELAY = SERVER_BUILDER.comment("Repair Gem - Delay time between repair ticks [default: 60]").defineInRange("repairGemDelay", 60, 20, 600);
    }

}
