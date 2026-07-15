package raziel23x.projectskyblock.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.registry.ModMenus;

@EventBusSubscriber(
        modid = ProjectSkyblock.MOD_ID,
        bus = EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(
                ModMenus.COBBLESTONE_CRUSHER.get(),
                CobblestoneCrusherScreen::new
        );
    }
}
