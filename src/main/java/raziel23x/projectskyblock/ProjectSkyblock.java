package raziel23x.projectskyblock;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import raziel23x.projectskyblock.registry.ModCreativeTabs;
import raziel23x.projectskyblock.registry.ModItems;

@Mod(ProjectSkyblock.MOD_ID)
public final class ProjectSkyblock {
    public static final String MOD_ID = "projectskyblock";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ProjectSkyblock(IEventBus modEventBus) {
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        LOGGER.info("Project Skyblock 2 initialization complete");
    }
}
