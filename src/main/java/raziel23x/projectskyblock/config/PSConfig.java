package raziel23x.projectskyblock.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class PSConfig {

    public static final ForgeConfigSpec CONFIG;
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec.BooleanValue DROP_SHEARS_PRODUCTS;

    static {
        PSConfigBuilder.init(BUILDER);

        CONFIG = BUILDER.build();

        BUILDER.push("Flint Shears Configuration");
        DROP_SHEARS_PRODUCTS = BUILDER.comment("When enabled the Flint Shears will behave the same as the iron shears.").define("drop_shears_products", true);
        BUILDER.pop();

    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();

        configData.load();

        spec.setConfig(configData);
    }
}
