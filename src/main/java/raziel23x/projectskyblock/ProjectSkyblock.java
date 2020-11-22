package raziel23x.projectskyblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
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
        RenderTypeLookup.setRenderLayer(RegistryHandler.COBBLESTONE_GENERATOR_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.LAVA_GENERATOR_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryHandler.WATER_GENERATOR_BLOCK.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(RegistryHandler.COBBLESTONE_CRUSHER_BLOCK.get(), RenderType.getTranslucent());

    }



    private void enqueueIMC(final InterModEnqueueEvent event) {
        if (CuriosUtil.isModLoaded()) {
            InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("curio").size(2).build());
            // InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("ring").size(4).build());
        }

        LOGGER.info("Project Skyblock IMC setup");
    }

    private void serverSetup(final FMLDedicatedServerSetupEvent event) {
        LOGGER.info("Project Skyblock server setup");
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();

        if (!world.isRemote) {
            PlayerEntity player = event.getPlayer();
            BlockState state = world.getBlockState(event.getPos());
            Block block = state.getBlock();

            if (event.getItemStack().getItem() == RegistryHandler.FLINT_SHEARS.get()) {
                if (block.getBlock() == Blocks.PUMPKIN) {
                    //LOGGER.info("Clicked Pumpkin with Flint Shears");

                    ItemStack seedItem = new ItemStack (Items.PUMPKIN_SEEDS);
                    seedItem.setCount(4);

                    world.removeBlock(event.getPos(),false);
                    world.setBlockState(event.getPos(), Blocks.CARVED_PUMPKIN.getDefaultState());
                    world.playSound(null, event.getPos(), SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1f, 1f);
                    block.spawnAsEntity(world, event.getPos(), seedItem);

                    if (!player.isCreative()) {
                        if (event.getItemStack().getItem().isDamageable()) {
                            event.getItemStack().getItem().setDamage(event.getItemStack(), event.getItemStack().getItem().getDamage(event.getItemStack()) + 1);
                        }
                    }
                } else {

                    if (block.getBlock() == Blocks.BEE_NEST) {
                        // do something
                    }
                }
            }
        }
    }
}