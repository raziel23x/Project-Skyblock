package raziel23x.projectskyblock;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import raziel23x.projectskyblock.config.ConfigBuilder;
import raziel23x.projectskyblock.init.ModBlockItems;
import raziel23x.projectskyblock.init.ModBlocks;
import raziel23x.projectskyblock.init.ModEntityType;
import raziel23x.projectskyblock.init.ModItems;
import raziel23x.projectskyblock.utils.CuriosUtil;
import top.theillusivec4.curios.api.SlotTypeMessage;

@Mod("projectskyblock")
public class ProjectSkyblock {

    public static final String MOD_ID = "projectskyblock";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final ConfigBuilder CONFIGURATION = new ConfigBuilder();

    public static final ItemGroup TAB = new ItemGroup("projectskyblock") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.REPAIR_GEM.get());
        }
    };

    public ProjectSkyblock() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIGURATION.SERVER);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);

        ModBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModBlockItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModEntityType.ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());

        MinecraftForge.EVENT_BUS.register(this);
    }


    private void serverSetup(final FMLDedicatedServerSetupEvent event) { }

    private void setup(final FMLCommonSetupEvent event) {
    }

    private void doClientStuff(final FMLClientSetupEvent even) {
        //Generators

        //Resource Generators

        RenderTypeLookup.setRenderLayer(ModBlocks.CLAY_GENERATOR_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.COBBLESTONE_GENERATOR_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.DIRT_GENERATOR_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ENDSTONE_GENERATOR_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.GRASSBLOCK_GENERATOR_BLOCK.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.GRAVEL_GENERATOR_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.NETHERRACK_GENERATOR_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.OBSIDIAN_GENERATOR_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.QUARTZ_GENERATOR_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.REDSAND_GENERATOR_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.SAND_GENERATOR_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.SOULSAND_GENERATOR_BLOCK.get(), RenderType.getCutout());

        //Fluid Generators

        RenderTypeLookup.setRenderLayer(ModBlocks.LAVA_GENERATOR_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.WATER_GENERATOR_BLOCK.get(), RenderType.getTranslucent());

        //Machines

        RenderTypeLookup.setRenderLayer(ModBlocks.COBBLESTONE_CRUSHER_BLOCK.get(), RenderType.getTranslucent());





    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        if (CuriosUtil.isModLoaded()) {
            InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("curio").size(2).build());
        }
    }
}