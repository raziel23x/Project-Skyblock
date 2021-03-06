package raziel23x.projectskyblock.events;


import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.BlockItem;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.init.ModBlockItems;
import raziel23x.projectskyblock.init.ModBlocks;



@Mod.EventBusSubscriber(modid = ProjectSkyblock.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {


    @SubscribeEvent
    public static void registerBlockColors(ColorHandlerEvent.Block event) {

        BlockColors blockcolors = event.getBlockColors();

        blockcolors.register((state, reader, pos, i) -> reader != null && pos != null ? BiomeColors.getWaterColor(reader, pos) : 0x3c44a9, ModBlocks.WATER_GENERATOR_BLOCK.get(), ModBlocks.COBBLESTONE_CRUSHER_BLOCK.get());
        blockcolors.register((state, reader, pos, i) -> reader != null && pos != null ? BiomeColors.getGrassColor(reader, pos) : 0x5E9D34, ModBlocks.GRASSBLOCK_GENERATOR_BLOCK.get());

    }

    @SubscribeEvent
    public static void registerItemColors(ColorHandlerEvent.Item event) {

        ItemColors itemcolors = event.getItemColors();
        BlockColors blockcolors = event.getBlockColors();


        itemcolors.register((stack, i) -> {
            BlockState state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
            return blockcolors.getColor(state, null, null, i);
        }, ModBlockItems.WATER_GENERATOR_BLOCK_ITEM.get(), ModBlockItems.COBBLESTONE_CRUSHER_BLOCK_ITEM.get(), ModBlockItems.GRASSBLOCK_GENERATOR_BLOCK_ITEM.get());
    }
}