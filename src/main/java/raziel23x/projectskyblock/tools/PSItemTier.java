package raziel23x.projectskyblock.tools;

import net.minecraft.item.IItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import raziel23x.projectskyblock.utils.RegistryHandler;

import java.util.function.Supplier;

public enum PSItemTier implements IItemTier {
    FLINT(3, 64, 5.0F, 1.0F, 8, () -> {
        return  Ingredient.fromItems(RegistryHandler.FLINT_SHOVEL.get(),
                RegistryHandler.FLINT_SWORD.get(),
                RegistryHandler.FLINT_HOE.get(),
                RegistryHandler.FLINT_PICKAXE.get(),
                RegistryHandler.FLINT_AXE.get(),
                RegistryHandler.FLINT_SHEARS.get()

                );
    });

    private final int harvestLevel;
    private final int maxUses;
    private final float efficiency;
    private final float attackDamage;
    private final int enchantability;
    private final Supplier<Ingredient> repairMaterial;

    PSItemTier(int harvestLevel, int maxUses, float efficiency, float attackDamage, int enchantability, Supplier<Ingredient> repairMaterial) {
        this.harvestLevel = harvestLevel;
        this.maxUses = maxUses;
        this.efficiency = efficiency;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairMaterial = repairMaterial;
    }

    @Override
    public int getMaxUses() {
        return maxUses;
    }

    @Override
    public float getEfficiency() {
        return efficiency;
    }

    @Override
    public float getAttackDamage() {
        return attackDamage;
    }

    @Override
    public int getHarvestLevel() {
        return harvestLevel;
    }

    @Override
    public int getEnchantability() {
        return enchantability;
    }

    @Override
    public Ingredient getRepairMaterial() {
        return repairMaterial.get();
    }
}
