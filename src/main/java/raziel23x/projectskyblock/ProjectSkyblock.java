package raziel23x.projectskyblock;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import raziel23x.projectskyblock.config.PSConfig;
import raziel23x.projectskyblock.utils.CuriosUtil;
import raziel23x.projectskyblock.utils.RegistryHandler;
import top.theillusivec4.curios.api.SlotTypeMessage;

@Mod("projectskyblock")
public class ProjectSkyblock {
    public static final String MOD_ID = "projectskyblock";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final ItemGroup TAB = new ItemGroup("projectskyblockTab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(RegistryHandler.REPAIR_GEM.get());
        }
    };
    public static ProjectSkyblock instance;

    public ProjectSkyblock() {
        instance = this;

        PSConfig.loadConfig(PSConfig.CONFIG, FMLPaths.CONFIGDIR.get().resolve("projectskyblock-general.toml"));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);

        RegistryHandler.init();

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
    }

    private void doClientStuff(final FMLClientSetupEvent even) {
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        if (CuriosUtil.findMod()) {
            InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("belt").size(1).build());
            // InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("ring").size(4).build());
        }

        LOGGER.info("Project Skyblock IMC setup");
    }

    private void serverSetup(final FMLDedicatedServerSetupEvent event) {
        LOGGER.info("Project Skyblock server setup");
    }
}