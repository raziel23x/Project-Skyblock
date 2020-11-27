package raziel23x.projectskyblock.data.client;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.init.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ProjectSkyblock.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        registerSimpleBlocks();


    }

    private void registerSimpleBlocks() {
        basicBlock(ModBlocks.BLUE_REAGENT_BLOCK.get());
        basicBlock(ModBlocks.GREEN_REAGENT_BLOCK.get());
        basicBlock(ModBlocks.RED_REAGENT_BLOCK.get());
    }

    private void basicBlock(Block block) {
        simpleItemBlock(block, cubeAll(block));
    }

    private void simpleItemBlock(Block block, ModelFile modelFile) {
        simpleBlock(block, modelFile);
        simpleBlockItem(block, modelFile);
    }
}
