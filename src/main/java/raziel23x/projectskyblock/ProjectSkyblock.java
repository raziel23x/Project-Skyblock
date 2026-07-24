package raziel23x.projectskyblock;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import raziel23x.projectskyblock.config.CommonConfig;
import raziel23x.projectskyblock.config.GeneratorConfig;
import raziel23x.projectskyblock.config.IntegrationConfig;
import raziel23x.projectskyblock.config.MachineConfig;
import raziel23x.projectskyblock.registry.ModArmorMaterials;
import raziel23x.projectskyblock.registry.ModBlockEntities;
import raziel23x.projectskyblock.registry.ModBlocks;
import raziel23x.projectskyblock.registry.ModCreativeTabs;
import raziel23x.projectskyblock.registry.ModItems;
import raziel23x.projectskyblock.registry.ModMenus;
import raziel23x.projectskyblock.registry.ModRecipes;
import raziel23x.projectskyblock.material.reload.MaterialDataReloadListener;
import raziel23x.projectskyblock.platform.neoforge.machine.EngineMachineLevelManager;
import raziel23x.projectskyblock.repair.RepairGemHandler;

@Mod(ProjectSkyblock.MOD_ID)
public final class ProjectSkyblock {
    public static final String MOD_ID = "projectskyblock";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ProjectSkyblock(IEventBus modEventBus, ModContainer modContainer) {
        ModArmorMaterials.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModMenus.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        modContainer.registerConfig(
                ModConfig.Type.COMMON,
                CommonConfig.SPEC,
                "projectskyblock-common.toml"
        );
        modContainer.registerConfig(
                ModConfig.Type.COMMON,
                GeneratorConfig.SPEC,
                "projectskyblock-generators.toml"
        );
        modContainer.registerConfig(
                ModConfig.Type.COMMON,
                IntegrationConfig.SPEC,
                "projectskyblock-integrations.toml"
        );
        modContainer.registerConfig(
                ModConfig.Type.COMMON,
                MachineConfig.SPEC,
                "projectskyblock-machines.toml"
        );

        NeoForge.EVENT_BUS.addListener(RepairGemHandler::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(ProjectSkyblock::addReloadListeners);
        NeoForge.EVENT_BUS.addListener(ProjectSkyblock::onDatapackSync);
        EngineMachineLevelManager.register(NeoForge.EVENT_BUS);
        modEventBus.addListener(ProjectSkyblock::registerCapabilities);

        LOGGER.info("Project Skyblock 2 initialization complete");
    }

    private static void addReloadListeners(
            net.neoforged.neoforge.event.AddReloadListenerEvent event) {
        event.addListener(MaterialDataReloadListener.INSTANCE);
    }

    private static void onDatapackSync(
            net.neoforged.neoforge.event.OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            EngineMachineLevelManager.requestAllWork(event.getPlayerList().getServer());
        }
    }

    private static void registerCapabilities(
            net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
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

        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.MATERIAL_CRUSHER.get(),
                (blockEntity, side) -> blockEntity.getItemHandler(side)
        );

        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.MATERIAL_CRUSHER.get(),
                (blockEntity, side) -> blockEntity.getEnergyStorage()
        );

        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.THERMAL_GENERATOR.get(),
                (blockEntity, side) -> blockEntity.getItemHandler(side)
        );

        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.THERMAL_GENERATOR.get(),
                (blockEntity, side) -> blockEntity.getFluidHandler(side)
        );

        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.THERMAL_GENERATOR.get(),
                (blockEntity, side) -> blockEntity.getEnergyStorage(side)
        );

        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.CREATIVE_ENERGY_CELL.get(),
                (blockEntity, side) -> blockEntity.getEnergyStorage()
        );
    }
}
