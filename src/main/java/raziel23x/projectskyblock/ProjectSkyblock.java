package raziel23x.projectskyblock;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
import raziel23x.projectskyblock.init.ModBlocks;
import raziel23x.projectskyblock.init.ModEntityType;
import raziel23x.projectskyblock.init.ModItems;
import raziel23x.projectskyblock.utils.CuriosUtil;
import top.theillusivec4.curios.api.SlotTypeMessage;

@Mod("projectskyblock")
public class ProjectSkyblock {
    public static final String MOD_ID = "projectskyblock";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final ItemGroup TAB = new ItemGroup("projectskyblockTab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.REPAIR_GEM.get());
        }
    };
    public static ProjectSkyblock instance;

    public ProjectSkyblock() {
        instance = this;

        PSConfig.loadConfig(PSConfig.CONFIG, FMLPaths.CONFIGDIR.get().resolve("projectskyblock-general.toml"));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);

        ModBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModEntityType.ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());


        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
    }

    private void doClientStuff(final FMLClientSetupEvent even) {
        RenderTypeLookup.setRenderLayer(ModBlocks.COBBLESTONE_GENERATOR_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.LAVA_GENERATOR_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.WATER_GENERATOR_BLOCK.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.COBBLESTONE_CRUSHER_BLOCK.get(), RenderType.getTranslucent());
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        if (CuriosUtil.isModLoaded()) {
            InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("curio").size(2).build());
        }
    }

    private void serverSetup(final FMLDedicatedServerSetupEvent event) {
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();

        if (!world.isRemote) {
            PlayerEntity player = event.getPlayer();
            BlockState state = world.getBlockState(event.getPos());
            Block block = state.getBlock();

            if (!event.getItemStack().isEmpty()) {
                if (event.getItemStack().getItem() == ModItems.FLINT_SHEARS.get()) {
                    if (block == Blocks.PUMPKIN) {
                        //LOGGER.info("Clicked Pumpkin with Flint Shears");

                        ItemStack seedItem = new ItemStack(Items.PUMPKIN_SEEDS);
                        seedItem.setCount(4);

                        world.removeBlock(event.getPos(), false);
                        world.setBlockState(event.getPos(), Blocks.CARVED_PUMPKIN.getDefaultState());
                        world.playSound(null, event.getPos(), SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1f, 1f);
                        Block.spawnAsEntity(world, event.getPos(), seedItem);

                        if (!player.isCreative()) {
                            if (event.getItemStack().getItem().isDamageable()) {
                                event.getItemStack().getItem().setDamage(event.getItemStack(), event.getItemStack().getItem().getDamage(event.getItemStack()) + 1);
                            }
                        }

                    } else {
                        IntegerProperty HONEY_LEVEL = BlockStateProperties.HONEY_LEVEL;

                        if (block == Blocks.BEEHIVE || block == Blocks.BEE_NEST) {
                            if (state.get(HONEY_LEVEL) >= 5) {
                                world.playSound(player, player.getPosition(), SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                                //BeehiveBlock.dropHoneyComb(world, event.getPos());  // this does exact same as .dropHoneyComb method
                                Block.spawnAsEntity(world, event.getPos(), new ItemStack(Items.HONEYCOMB, 3));

                                if (!player.isCreative()) {
                                    if (event.getItemStack().getItem().isDamageable()) {
                                        event.getItemStack().getItem().setDamage(event.getItemStack(), event.getItemStack().getItem().getDamage(event.getItemStack()) + 1);
                                    }
                                }

                                BeehiveBlock HIVE = (BeehiveBlock) state.getBlock();
                                BeehiveTileEntity HIVE_TE = (BeehiveTileEntity) world.getTileEntity(event.getPos());

                                if (HIVE_TE != null) {
                                    if (!HIVE_TE.hasNoBees()) {
                                        HIVE.takeHoney(world, state, event.getPos(), player, BeehiveTileEntity.State.EMERGENCY);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}