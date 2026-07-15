package raziel23x.projectskyblock;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import raziel23x.projectskyblock.registry.ModArmorMaterials;
import raziel23x.projectskyblock.registry.ModCreativeTabs;
import raziel23x.projectskyblock.registry.ModBlocks;
import raziel23x.projectskyblock.registry.ModBlockEntities;
import raziel23x.projectskyblock.registry.ModItems;
import raziel23x.projectskyblock.repair.RepairGemHandler;

@Mod(ProjectSkyblock.MOD_ID)
public final class ProjectSkyblock {
    public static final String MOD_ID = "projectskyblock";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ProjectSkyblock(IEventBus modEventBus) {
        ModArmorMaterials.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(RepairGemHandler::onPlayerTick);
        modEventBus.addListener(ProjectSkyblock::registerCapabilities);

        LOGGER.info("Project Skyblock 2 initialization complete");
    }
    private static void registerCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.RESOURCE_GENERATOR.get(),
                (blockEntity, side) -> blockEntity.getOutputForItemCapability()
        );
        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.RESOURCE_GENERATOR.get(),
                (blockEntity, side) -> blockEntity.getOutputForFluidCapability()
        );
    }

}
