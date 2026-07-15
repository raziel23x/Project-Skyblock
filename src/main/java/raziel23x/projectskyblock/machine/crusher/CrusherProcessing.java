package raziel23x.projectskyblock.machine.crusher;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModList;
import raziel23x.projectskyblock.config.MachineConfig;

public final class CrusherProcessing {
    private static final ResourceLocation EX_DEORUM_DUST =
            ResourceLocation.fromNamespaceAndPath("exdeorum", "dust");

    private CrusherProcessing() {
    }

    public static boolean isValidInput(ItemStack stack) {
        return stack.is(Items.COBBLESTONE)
                || stack.is(Items.GRAVEL)
                || canMakeExDeorumDust(stack);
    }

    public static CrusherProcessingResult createResult(
            ItemStack input,
            RandomSource random) {
        if (input.is(Items.COBBLESTONE)) {
            int count = 1;
            if (random.nextDouble()
                    < MachineConfig.CRUSHER_EXTRA_GRAVEL_CHANCE.get()) {
                count++;
            }
            return new CrusherProcessingResult(
                    new ItemStack(Items.GRAVEL, count),
                    ItemStack.EMPTY
            );
        }

        if (input.is(Items.GRAVEL)) {
            int count = 1;
            if (random.nextDouble()
                    < MachineConfig.CRUSHER_EXTRA_SAND_CHANCE.get()) {
                count++;
            }

            ItemStack byproduct = random.nextDouble()
                    < MachineConfig.CRUSHER_FLINT_CHANCE.get()
                    ? new ItemStack(Items.FLINT)
                    : ItemStack.EMPTY;

            return new CrusherProcessingResult(
                    new ItemStack(Items.SAND, count),
                    byproduct
            );
        }

        if (canMakeExDeorumDust(input)) {
            Item dust = BuiltInRegistries.ITEM.get(EX_DEORUM_DUST);
            if (dust != Items.AIR) {
                return new CrusherProcessingResult(
                        new ItemStack(dust),
                        ItemStack.EMPTY
                );
            }
        }

        return CrusherProcessingResult.EMPTY;
    }

    private static boolean canMakeExDeorumDust(ItemStack stack) {
        return stack.is(Items.SAND)
                && ModList.get().isLoaded("exdeorum")
                && BuiltInRegistries.ITEM.containsKey(EX_DEORUM_DUST)
                && BuiltInRegistries.ITEM.get(EX_DEORUM_DUST) != Items.AIR;
    }
}
