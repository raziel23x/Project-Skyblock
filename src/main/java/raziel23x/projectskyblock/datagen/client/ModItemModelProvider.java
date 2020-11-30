package raziel23x.projectskyblock.datagen.client;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import raziel23x.projectskyblock.ProjectSkyblock;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ProjectSkyblock.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        ModelFile itemGenerated = getExistingFile(mcLoc("item/generated"));

        //Items
        builder(itemGenerated, "repair_gem");
        builder(itemGenerated, "red_reagent");
        builder(itemGenerated, "green_reagent");
        builder(itemGenerated, "blue_reagent");
        builder(itemGenerated, "mixing_bowl");
        //Tools
        builder(itemGenerated, "flint_shovel");
        builder(itemGenerated, "flint_sword");
        builder(itemGenerated, "flint_pickaxe");
        builder(itemGenerated, "flint_axe");
        builder(itemGenerated, "flint_hoe");
        builder(itemGenerated, "flint_shears");
        builder(itemGenerated, "wooden_shears");
        //Armor
        //Flint Armor
        builder(itemGenerated, "flint_helmet");
        builder(itemGenerated, "flint_chestplate");
        builder(itemGenerated, "flint_leggings");
        builder(itemGenerated, "flint_boots");
        //Wooden Armor
        builder(itemGenerated, "wooden_helmet");
        builder(itemGenerated, "wooden_chestplate");
        builder(itemGenerated, "wooden_leggings");
        builder(itemGenerated, "wooden_boots");
    }

    private ItemModelBuilder builder(ModelFile itemGenerated, String name) {
        return getBuilder(name).parent(itemGenerated).texture("layer0", "item/" + name);
    }
}
