package raziel23x.projectskyblock.machine.thermal;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import raziel23x.projectskyblock.config.MachineConfig;

/**
 * Shared conversion helpers for the Thermal Power System.
 */
public final class ThermalFuelMath {
    private ThermalFuelMath() {
    }

    public static int getFurnaceBurnTime(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        return Math.max(0, stack.getBurnTime(RecipeType.SMELTING));
    }

    public static int getThermalFuelFromItem(ItemStack stack) {
        int burnTime = getFurnaceBurnTime(stack);
        if (burnTime <= 0) {
            return 0;
        }

        long amount = (long) burnTime * MachineConfig.THERMAL_FUEL_MB_PER_BURN_TICK.get();
        return (int) Math.min(Integer.MAX_VALUE, amount);
    }

    public static int getEnergyFromThermalFuel(int thermalFuelMb) {
        if (thermalFuelMb <= 0) {
            return 0;
        }

        long amount = (long) thermalFuelMb * MachineConfig.THERMAL_GENERATOR_FE_PER_MB.get();
        return (int) Math.min(Integer.MAX_VALUE, amount);
    }
}
