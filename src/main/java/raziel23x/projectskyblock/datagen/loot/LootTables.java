package raziel23x.projectskyblock.datagen.loot;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import raziel23x.projectskyblock.init.ModBlocks;

public class LootTables extends LootTableGenerator {

    public LootTables(DataGenerator generator)
    {
        super(generator);
    }

    @Override
    protected void createLootTables()
    {
        registerSelfDrops();

    }


    private void registerSelfDrops()
    {
        //Reagent Blocks
        registerSelfDrop(ModBlocks.BLUE_REAGENT_BLOCK.get());
        registerSelfDrop(ModBlocks.RED_REAGENT_BLOCK.get());
        registerSelfDrop(ModBlocks.GREEN_REAGENT_BLOCK.get());

        //Resource Generators
        registerSelfDrop(ModBlocks.CLAY_GENERATOR_BLOCK.get());
        registerSelfDrop(ModBlocks.COBBLESTONE_GENERATOR_BLOCK.get());
        registerSelfDrop(ModBlocks.DIRT_GENERATOR_BLOCK.get());
        registerSelfDrop(ModBlocks.ENDSTONE_GENERATOR_BLOCK.get());
        registerSelfDrop(ModBlocks.GRASSBLOCK_GENERATOR_BLOCK.get());
        registerSelfDrop(ModBlocks.GRAVEL_GENERATOR_BLOCK.get());
        registerSelfDrop(ModBlocks.NETHERRACK_GENERATOR_BLOCK.get());
        registerSelfDrop(ModBlocks.OBSIDIAN_GENERATOR_BLOCK.get());
        registerSelfDrop(ModBlocks.QUARTZ_GENERATOR_BLOCK.get());
        registerSelfDrop(ModBlocks.REDSAND_GENERATOR_BLOCK.get());
        registerSelfDrop(ModBlocks.SAND_GENERATOR_BLOCK.get());
        registerSelfDrop(ModBlocks.SOULSAND_GENERATOR_BLOCK.get());

        //Fluid Generators
        registerSelfDrop(ModBlocks.LAVA_GENERATOR_BLOCK.get());
        registerSelfDrop(ModBlocks.WATER_GENERATOR_BLOCK.get());

        //Machines
        registerSelfDrop(ModBlocks.COBBLESTONE_CRUSHER_BLOCK.get());

    }

    private void registerSelfDrop(Block block)
    {
        register(block, singleItem(block));
    }

    private void register(Block block, LootPool.Builder... pools)
    {
        LootTable.Builder builder = LootTable.builder();
        for (LootPool.Builder pool : pools) {
            builder.addLootPool(pool);
        }
        register(block, builder);
    }

    private void register(Block block, LootTable.Builder table)
    {
        register(block.getRegistryName(), table);
    }

    private void register(ResourceLocation registryName, LootTable.Builder table)
    {
        if (lootTables.put(toTableLoc(registryName), table.setParameterSet(LootParameterSets.BLOCK).build()) != null)
        {
            throw new IllegalStateException("Duplicate loot table: " + table);
        }
    }

    private ResourceLocation toTableLoc(ResourceLocation registryName)
    {
        return new ResourceLocation(registryName.getNamespace(), "blocks/" + registryName.getPath());
    }

    private LootPool.Builder singleItem(IItemProvider in)
    {
        return createLootPoolBuilder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(in));
    }

    private LootPool.Builder createLootPoolBuilder()
    {
        return LootPool.builder().acceptCondition(SurvivesExplosion.builder());
    }

    @Override
    public String getName()
    {
        return "Loot Tables";
    }
}
