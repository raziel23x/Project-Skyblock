package raziel23x.projectskyblock.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.datagen.client.ModBlockStateProvider;
import raziel23x.projectskyblock.datagen.client.ModItemModelProvider;
import raziel23x.projectskyblock.datagen.loot.LootTables;
import raziel23x.projectskyblock.datagen.recipes.ModRecipeProvider;


@Mod.EventBusSubscriber(modid = ProjectSkyblock.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {
    private DataGenerators() {
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        gen.addProvider(new ModBlockStateProvider(gen, existingFileHelper));
        gen.addProvider(new ModItemModelProvider(gen, existingFileHelper));
        gen.addProvider(new ModRecipeProvider(gen));
        gen.addProvider(new LootTables(gen));

        ModBlockTagsProvider blockTags = new ModBlockTagsProvider(gen, existingFileHelper);
        gen.addProvider(blockTags);
        gen.addProvider(new ModItemTagsProvider(gen, blockTags, existingFileHelper));
    }
}
