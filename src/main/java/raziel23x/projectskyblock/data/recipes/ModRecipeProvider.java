package raziel23x.projectskyblock.data.recipes;

import net.minecraft.data.*;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.init.ModBlocks;
import raziel23x.projectskyblock.init.ModItems;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder{

    public ModRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer){
        this.addReagentRecipes(consumer);
        this.addReagentBlockRecipes(consumer);
        this.addGeneratorsRecipes(consumer);
        this.addFlintToolsRecipes(consumer);
        this.addFlintArmorRecipes(consumer);
        this.addWoodenToolsRecipes(consumer);
        this.addWoodenArmorRecipes(consumer);
        this.addMiscRecipes(consumer);
    }

    private void addMiscRecipes(Consumer<IFinishedRecipe> consumer) {

        //Repair Gem
        ShapedRecipeBuilder.shapedRecipe(ModItems.REPAIR_GEM.get())
                .key('A', Items.ANVIL)
                .key('S', Items.SMITHING_TABLE)
                .key('R', ModItems.RED_REAGENT.get())
                .key('G', ModItems.GREEN_REAGENT.get())
                .key('B', ModItems.BLUE_REAGENT.get())
                .patternLine("RGB")
                .patternLine("SAS")
                .patternLine("BGR")
                .addCriterion("", hasItem(ModItems.MIXING_BOWL.get()))
                .build(consumer, location("repair_gem"));

        //Mixing Bowl
        ShapedRecipeBuilder.shapedRecipe(ModItems.MIXING_BOWL.get())

                .key('S', Items.STICK)
                .key('B', Items.BOWL)
                .patternLine("S")
                .patternLine("B")
                .addCriterion("has_bowl", hasItem(Items.BOWL))
                .build(consumer, location("mixing_bowl"));
    }

    private void addReagentRecipes(Consumer<IFinishedRecipe> consumer) {
        String folder = "reagents/items/";

        //Blue Reagent
        ShapedRecipeBuilder.shapedRecipe(ModItems.BLUE_REAGENT.get(), 40)
                .key('L', Tags.Items.GEMS_LAPIS)
                .key('F', Items.FLINT)
                .key('P', Tags.Items.DUSTS_PRISMARINE)
                .key('M', ModItems.MIXING_BOWL.get())
                .patternLine("LFL")
                .patternLine("PMP")
                .patternLine("LFL")
                .addCriterion("", hasItem(ModItems.MIXING_BOWL.get()))
                .build(consumer, location(folder + "blue_reagent"));

        ShapelessRecipeBuilder.shapelessRecipe(ModItems.BLUE_REAGENT.get(), 9)
                .addIngredient(ModBlocks.BLUE_REAGENT_BLOCK.get())
                .addCriterion("", hasItem(ModItems.MIXING_BOWL.get()))
                .build(consumer, location(folder + "blue_reagent_alt"));

        //Green Reagent
        ShapedRecipeBuilder.shapedRecipe(ModItems.GREEN_REAGENT.get(), 40)
                .key('E', Tags.Items.GEMS_EMERALD)
                .key('D', Tags.Items.GEMS_DIAMOND)
                .key('P', Tags.Items.DUSTS_PRISMARINE)
                .key('M', ModItems.MIXING_BOWL.get())
                .patternLine("EDE")
                .patternLine("PMP")
                .patternLine("EDE")
                .addCriterion("", hasItem(ModItems.MIXING_BOWL.get()))
                .build(consumer, location(folder + "green_reagent"));

        ShapelessRecipeBuilder.shapelessRecipe(ModItems.GREEN_REAGENT.get(), 9)
                .addIngredient(ModBlocks.GREEN_REAGENT_BLOCK.get())
                .addCriterion("", hasItem(ModItems.GREEN_REAGENT.get()))
                .build(consumer, location(folder + "green_reagent_alt"));

        //Red Reagent
        ShapedRecipeBuilder.shapedRecipe(ModItems.RED_REAGENT.get(), 40)
                .key('R', Tags.Items.DUSTS_REDSTONE)
                .key('F', Items.FLINT)
                .key('C', Items.CHARCOAL)
                .key('M', ModItems.MIXING_BOWL.get())
                .patternLine("RFR")
                .patternLine("CMC")
                .patternLine("RFR")
                .addCriterion("", hasItem(ModItems.MIXING_BOWL.get()))
                .build(consumer, location(folder + "red_reagent"));

        ShapelessRecipeBuilder.shapelessRecipe(ModItems.RED_REAGENT.get(), 9)
                .addIngredient(ModBlocks.RED_REAGENT_BLOCK.get())
                .addCriterion("", hasItem(ModItems.MIXING_BOWL.get()))
                .build(consumer, location(folder + "red_reagent_alt"));
    }

    private void addReagentBlockRecipes(Consumer<IFinishedRecipe> consumer) {
        String folder = "reagents/blocks/";

        //Blue Reagent Blocks
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.BLUE_REAGENT_BLOCK.get())
                .key('B', ModItems.BLUE_REAGENT.get())
                .patternLine("BBB")
                .patternLine("BBB")
                .patternLine("BBB")
                .addCriterion("", hasItem(ModItems.BLUE_REAGENT.get()))
                .build(consumer, location(folder + "blue_reagent_block"));

        //Green Reagent Blocks
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.GREEN_REAGENT_BLOCK.get())
                .key('G', ModItems.GREEN_REAGENT.get())
                .patternLine("GGG")
                .patternLine("GGG")
                .patternLine("GGG")
                .addCriterion("", hasItem(ModItems.GREEN_REAGENT.get()))
                .build(consumer, location(folder + "green_reagent_block"));

        //Red Reagent Blocks
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.RED_REAGENT_BLOCK.get())
                .key('R', ModItems.RED_REAGENT.get())
                .patternLine("RRR")
                .patternLine("RRR")
                .patternLine("RRR")
                .addCriterion("", hasItem(ModItems.RED_REAGENT.get()))
                .build(consumer, location(folder + "red_reagent_block"));
    }

    private void addGeneratorsRecipes(Consumer<IFinishedRecipe> consumer) {
        String folder = "generators/";

        //Cobblestone Generator
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.COBBLESTONE_GENERATOR_BLOCK.get())
                .key('C', Tags.Items.COBBLESTONE)
                .key('L', Items.LAVA_BUCKET)
                .key('G', Tags.Items.GLASS)
                .key('W',Items.WATER_BUCKET)
                .patternLine("CCC")
                .patternLine("LGW")
                .patternLine("CCC")
                .addCriterion("has_lava_bucket", hasItem(Items.LAVA_BUCKET))
                .addCriterion("has_water_bucket", hasItem(Items.WATER_BUCKET))
                .build(consumer, location(folder + "cobblestone_generator"));

        //Cobblestone Generator Alternative
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.COBBLESTONE_GENERATOR_BLOCK.get())
                .key('C', Tags.Items.COBBLESTONE)
                .key('L', Items.LAVA_BUCKET)
                .key('G', Tags.Items.GLASS)
                .key('W',Items.WATER_BUCKET)
                .patternLine("CCC")
                .patternLine("WGL")
                .patternLine("CCC")
                .addCriterion("has_lava_bucket", hasItem(Items.LAVA_BUCKET))
                .addCriterion("has_water_bucket", hasItem(Items.WATER_BUCKET))
                .build(consumer, location(folder + "cobblestone_generator_alt"));

        //Lava Generator
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.LAVA_GENERATOR_BLOCK.get())
                .key('I', Tags.Items.INGOTS_IRON)
                .key('L', Items.LAVA_BUCKET)
                .key('G', Tags.Items.GLASS)
                .patternLine("ILI")
                .patternLine("LGL")
                .patternLine("ILI")
                .addCriterion("has_lava_bucket", hasItem(Items.LAVA_BUCKET))
                .build(consumer, location(folder + "lava_generator"));

        //Water Generator
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.WATER_GENERATOR_BLOCK.get())
                .key('I', Tags.Items.INGOTS_IRON)
                .key('W', Items.WATER_BUCKET)
                .key('G', Tags.Items.GLASS)
                .patternLine("IWI")
                .patternLine("WGW")
                .patternLine("IWI")
                .addCriterion("has_water_bucket", hasItem(Items.WATER_BUCKET))
                .build(consumer, location(folder + "water_generator"));
    }

    private void addFlintToolsRecipes(Consumer<IFinishedRecipe> consumer) {
        String folder = "flint/tools/";

        //Flint Axe
        ShapedRecipeBuilder.shapedRecipe(ModItems.FLINT_AXE.get())
                .key('F', Items.FLINT)
                .key('S', Items.STICK)
                .patternLine("FF ")
                .patternLine("FS ")
                .patternLine(" S ")
                .addCriterion("has_flint", hasItem(Items.FLINT))
                .build(consumer, location(folder + "flint_axe"));

        //Flint Hoe
        ShapedRecipeBuilder.shapedRecipe(ModItems.FLINT_HOE.get())
                .key('F', Items.FLINT)
                .key('S', Items.STICK)
                .patternLine("FF ")
                .patternLine(" S ")
                .patternLine(" S ")
                .addCriterion("has_flint", hasItem(Items.FLINT))
                .build(consumer, location(folder + "flint_hoe"));

        //Flint Pickaxe
        ShapedRecipeBuilder.shapedRecipe(ModItems.FLINT_PICKAXE.get())
                .key('F', Items.FLINT)
                .key('S', Items.STICK)
                .patternLine("FFF")
                .patternLine(" S ")
                .patternLine(" S ")
                .addCriterion("has_flint", hasItem(Items.FLINT))
                .build(consumer, location(folder + "flint_pickaxe"));

        //Flint Shears
        ShapedRecipeBuilder.shapedRecipe(ModItems.FLINT_SHEARS.get())
                .key('F', Items.FLINT)
                .key('P', ItemTags.PLANKS)
                .patternLine("F ")
                .patternLine("PF")
                .addCriterion("has_flint", hasItem(Items.FLINT))
                .build(consumer, location(folder + "flint_shears"));

        //Flint Shovel
        ShapedRecipeBuilder.shapedRecipe(ModItems.FLINT_SHOVEL.get())
                .key('F', Items.FLINT)
                .key('S', Items.STICK)
                .patternLine(" F ")
                .patternLine(" S ")
                .patternLine(" S ")
                .addCriterion("has_flint", hasItem(Items.FLINT))
                .build(consumer, location(folder + "flint_shovel"));

        //Flint Sword
        ShapedRecipeBuilder.shapedRecipe(ModItems.FLINT_SWORD.get())
                .key('F', Items.FLINT)
                .key('S', Items.STICK)
                .patternLine(" F ")
                .patternLine(" F ")
                .patternLine(" S ")
                .addCriterion("has_flint", hasItem(Items.FLINT))
                .build(consumer, location(folder + "flint_sword"));
    }

    private void addFlintArmorRecipes(Consumer<IFinishedRecipe> consumer) {
        String folder = "flint/armor/";

        //Flint Boots
        ShapedRecipeBuilder.shapedRecipe(ModItems.FLINT_BOOTS.get())
                .key('F', Items.FLINT)
                .patternLine("F F")
                .patternLine("F F")
                .addCriterion("has_flint", hasItem(Items.FLINT))
                .build(consumer, location(folder + "flint_boots"));

        //Flint Chestplate
        ShapedRecipeBuilder.shapedRecipe(ModItems.FLINT_CHESTPLATE.get())
                .key('F', Items.FLINT)
                .patternLine("F F")
                .patternLine("FFF")
                .patternLine("FFF")
                .addCriterion("has_flint", hasItem(Items.FLINT))
                .build(consumer, location(folder + "flint_chestplate"));

        //Flint Helmet
        ShapedRecipeBuilder.shapedRecipe(ModItems.FLINT_HELMET.get())
                .key('F', Items.FLINT)
                .patternLine("FFF")
                .patternLine("F F")
                .addCriterion("has_flint", hasItem(Items.FLINT))
                .build(consumer, location(folder + "flint_helmet"));

        //Flint Leggings
        ShapedRecipeBuilder.shapedRecipe(ModItems.FLINT_LEGGINGS.get())
                .key('F', Items.FLINT)
                .patternLine("FFF")
                .patternLine("F F")
                .patternLine("F F")
                .addCriterion("has_flint", hasItem(Items.FLINT))
                .build(consumer, location(folder + "flint_leggings"));
    }

    private void addWoodenToolsRecipes(Consumer<IFinishedRecipe> consumer) {
        String folder = "wooden/tools/";

        //Wooden Shears
        ShapedRecipeBuilder.shapedRecipe(ModItems.WOODEN_SHEARS.get())
                .key('B', ItemTags.BUTTONS)
                .key('P', ItemTags.PLANKS)
                .patternLine("P ")
                .patternLine("BP")
                .addCriterion("has_planks", hasItem(ItemTags.PLANKS))
                .build(consumer, location(folder + "wooden_shears"));
    }

    private void addWoodenArmorRecipes(Consumer<IFinishedRecipe> consumer) {
        String folder = "wooden/armor/";
        //Wooden Boots
        ShapedRecipeBuilder.shapedRecipe(ModItems.WOODEN_BOOTS.get())
                .key('P', ItemTags.PLANKS)
                .patternLine("P P")
                .patternLine("P P")
                .addCriterion("has_planks", hasItem(ItemTags.PLANKS))
                .build(consumer, location(folder + "wooden_boots"));

        //Wooden Chestplate
        ShapedRecipeBuilder.shapedRecipe(ModItems.WOODEN_CHESTPLATE.get())
                .key('P', ItemTags.PLANKS)
                .patternLine("P P")
                .patternLine("PPP")
                .patternLine("PPP")
                .addCriterion("has_planks", hasItem(ItemTags.PLANKS))
                .build(consumer, location(folder + "wooden_chestplate"));

        //Wooden Helmet
        ShapedRecipeBuilder.shapedRecipe(ModItems.WOODEN_HELMET.get())
                .key('P', ItemTags.PLANKS)
                .patternLine("PPP")
                .patternLine("P P")
                .addCriterion("has_planks", hasItem(ItemTags.PLANKS))
                .build(consumer, location(folder + "wooden_helmet"));

        //Wooden Leggings
        ShapedRecipeBuilder.shapedRecipe(ModItems.WOODEN_LEGGINGS.get())
                .key('P', ItemTags.PLANKS)
                .patternLine("PPP")
                .patternLine("P P")
                .patternLine("P P")
                .addCriterion("has_planks", hasItem(ItemTags.PLANKS))
                .build(consumer, location(folder + "wooden_leggings"));
    }

        private static ResourceLocation location(String id) {
        return new ResourceLocation(ProjectSkyblock.MOD_ID, id);
    }
}