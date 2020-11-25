package raziel23x.projectskyblock.data;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.init.ModBlocks;
import raziel23x.projectskyblock.init.ModTags;


public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
        super(generatorIn, ProjectSkyblock.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerTags() {
        getOrCreateBuilder(ModTags.Blocks.STORAGE_BLOCKS_REAGENT).add(ModBlocks.RED_REAGENT_BLOCK.get());
        getOrCreateBuilder(ModTags.Blocks.STORAGE_BLOCKS_REAGENT).add(ModBlocks.GREEN_REAGENT_BLOCK.get());
        getOrCreateBuilder(ModTags.Blocks.STORAGE_BLOCKS_REAGENT).add(ModBlocks.BLUE_REAGENT_BLOCK.get());
        getOrCreateBuilder(Tags.Blocks.STORAGE_BLOCKS).addTag(ModTags.Blocks.STORAGE_BLOCKS_REAGENT);
    }
}
