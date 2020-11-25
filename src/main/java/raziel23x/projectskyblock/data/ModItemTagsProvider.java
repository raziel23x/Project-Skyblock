package raziel23x.projectskyblock.data;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.init.ModItems;
import raziel23x.projectskyblock.init.ModTags;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(dataGenerator, blockTagProvider, ProjectSkyblock.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerTags() {

        copy(ModTags.Blocks.STORAGE_BLOCKS_REAGENT, ModTags.Items.STORAGE_BLOCKS_REAGENT);
        copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);

        getOrCreateBuilder(ModTags.Items.DUST_REAGENT).add(ModItems.RED_REAGENT.get());
        getOrCreateBuilder(ModTags.Items.DUST_REAGENT).add(ModItems.GREEN_REAGENT.get());
        getOrCreateBuilder(ModTags.Items.DUST_REAGENT).add(ModItems.BLUE_REAGENT.get());

        getOrCreateBuilder(Tags.Items.DUSTS).addTag(ModTags.Items.DUST_REAGENT);

    }
}
