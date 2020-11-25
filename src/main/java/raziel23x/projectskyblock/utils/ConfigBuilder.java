package raziel23x.projectskyblock.utils;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigBuilder {
    //public final ForgeConfigSpec CLIENT;
    public final ForgeConfigSpec CLIENT;

    public ForgeConfigSpec.IntValue RepairGemTickDelay;
    public ForgeConfigSpec.BooleanValue FlintShearsVanillaDrops;
    public ForgeConfigSpec.BooleanValue WoodenShearsVanillaDrops;


    public ConfigBuilder() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();


        builder.push("Repair Gem Settings");
        RepairGemTickDelay = builder
            .comment("Repair Gem - Delay time between repair ticks")
            .defineInRange("RepairGemTickDelay", 60, 20, 600);

        builder.pop();


        builder.push("Flint Item Settings");
        FlintShearsVanillaDrops = builder
                .comment("Should the Shears drop Vanilla items ?")
                .define("Vanilla Drops", true);

        builder.pop();


        builder.push("Wooden Item Settings");
        WoodenShearsVanillaDrops = builder
                .comment("Should the Shears drop Vanilla items ?")
                .define("Vanilla Drops", true);

        builder.pop();


        CLIENT = builder.build();
    }
}
