package raziel23x.projectskyblock.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.utils.RegistryHandler;


@Mod.EventBusSubscriber(modid = ProjectSkyblock.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

/*
    @SubscribeEvent
    public static void registerBlockColors(ColorHandlerEvent.Block event) {


        BlockColors blockcolors = Minecraft.getInstance().getBlockColors();

        blockcolors.register((state, reader, pos, color) -> reader != null && pos != null ? BiomeColors.getWaterColor(reader, pos) : -1, RegistryHandler.WATER_GENERATOR_BLOCK.get());
    }

    @Mod(Main.MOD_ID)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public class Main {
        public static final String MOD_ID = "mod_id";
        public Main() {
            final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
            eventBus.addListener(this::onCommonSetup);
            eventBus.addListener(this::onClientSetup);
        }
    }
*/
}
