package raziel23x.projectskyblock.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigBuilder {
    //public final ForgeConfigSpec CLIENT;
    public final ForgeConfigSpec SERVER;

    public ForgeConfigSpec.IntValue RepairGemTickDelay;
    public ForgeConfigSpec.BooleanValue FlintShearsVanillaDrops;
    public ForgeConfigSpec.BooleanValue WoodenShearsVanillaDrops;


    public ConfigBuilder() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();


        builder.push("Repair Gem Settings");
        RepairGemTickDelay = builder
                .comment("Repair Gem - Delay time between repair ticks " +
                        "\nDefault = 60")
                .defineInRange("RepairGemTickDelay", 60, 20, 600);

        builder.pop();


        builder.push("Flint Shears Setting");
        FlintShearsVanillaDrops = builder
                .comment("Should the Shears drop Vanilla item? " +
                        "\nDefault = true " +
                        "\nSet to false to disable")
                .define("Vanilla Drops", true);

        builder.pop();


        builder.push("Wooden Shears Setting");
        WoodenShearsVanillaDrops = builder
                .comment("Should the Shears drop Vanilla item? " +
                        "\nDefault = true" +
                        "\nSet to false to disable")
                .define("Vanilla Drops", true);

        builder.pop();


        SERVER = builder.build();
    }
}
