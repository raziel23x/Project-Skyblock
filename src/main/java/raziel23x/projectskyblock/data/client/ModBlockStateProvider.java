package raziel23x.projectskyblock.data.client;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.init.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ProjectSkyblock.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(ModBlocks.BLUE_REAGENT_BLOCK.get());
        simpleBlock(ModBlocks.GREEN_REAGENT_BLOCK.get());
        simpleBlock(ModBlocks.RED_REAGENT_BLOCK.get());

    }
}
