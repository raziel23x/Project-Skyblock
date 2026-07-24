package raziel23x.projectskyblock.platform.neoforge.machine.crusher;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import raziel23x.projectskyblock.platform.neoforge.inventory.ItemStackBoundaryException;
import raziel23x.projectskyblock.platform.neoforge.inventory.MinecraftItemStackCodec;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;
import raziel23x.projectskyblock.simulation.machine.logic.crusher.MaterialCrusherFuel;
import raziel23x.projectskyblock.simulation.machine.logic.crusher.MaterialCrusherFuelPort;

/** Minecraft furnace-fuel adapter for the engine-owned prototype crusher logic. */
public final class MinecraftMaterialCrusherFuelPort implements MaterialCrusherFuelPort {
    private final MinecraftItemStackCodec codec;

    public MinecraftMaterialCrusherFuelPort(MinecraftItemStackCodec codec) {
        this.codec = Objects.requireNonNull(codec, "codec");
    }

    @Override
    public Optional<MaterialCrusherFuel> resolveFuel(SimulationItemStack fuel) {
        if (fuel.isEmpty()) {
            return Optional.empty();
        }
        try {
            ItemStack minecraftFuel = codec.decode(fuel);
            int burnTime = minecraftFuel.getBurnTime(RecipeType.SMELTING);
            if (burnTime <= 0) {
                return Optional.empty();
            }
            SimulationItemStack remainder = minecraftFuel.is(Items.LAVA_BUCKET)
                    ? codec.encode(new ItemStack(Items.BUCKET))
                    : SimulationItemStack.empty();
            return Optional.of(new MaterialCrusherFuel(burnTime, remainder));
        } catch (ItemStackBoundaryException exception) {
            return Optional.empty();
        }
    }

    public boolean accepts(ItemStack stack) {
        return stack != null
                && !stack.isEmpty()
                && stack.getBurnTime(RecipeType.SMELTING) > 0;
    }
}
