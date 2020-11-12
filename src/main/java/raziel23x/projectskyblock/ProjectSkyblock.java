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
import raziel23x.projectskyblock.config.Config;
import raziel23x.projectskyblock.util.BlockRenders;
import raziel23x.projectskyblock.util.CuriosModCheck;
import raziel23x.projectskyblock.util.RegistryHandler;
import top.theillusivec4.curios.api.SlotTypeMessage;

@Mod("projectskyblock")
public class ProjectSkyblock 
{
    public static ProjectSkyblock instance;
    public static final String MOD_ID = "projectskyblock";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public ProjectSkyblock() {
        instance = this;

        Config.loadConfig(Config.CONFIG, FMLPaths.CONFIGDIR.get().resolve("projectskyblock-general.toml"));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        RegistryHandler.init();

        MinecraftForge.EVENT_BUS.register( this);
    }

        private void setup(final FMLCommonSetupEvent event) { }

        private void doClientStuff(final FMLClientSetupEvent even) { }

        public static final ItemGroup TAB = new ItemGroup("projectskyblockTab") {
            @Override
            public ItemStack createIcon() {
                return new ItemStack(RegistryHandler.REPAIR_GEM.get());
            }
        };

    private void clientSetup(final FMLClientSetupEvent event)
    {
        BlockRenders.defineRenders();

        LOGGER.info("Project Skyblock client setup");
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        if (CuriosModCheck.CURIOS.isLoaded())
        {
            InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("belt").size(2).build());
           // InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("ring").size(4).build());
        }

        LOGGER.info("Project Skyblock IMC setup");
    }

    private void serverSetup(final FMLDedicatedServerSetupEvent event)
    {
        LOGGER.info("Project Skyblock server setup");
    }
}