package raziel23x.projectskyblock.platform.neoforge.machine.crusher;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import raziel23x.projectskyblock.machine.crusher.CrusherProcessingResult;
import raziel23x.projectskyblock.platform.neoforge.inventory.ItemStackBoundaryException;
import raziel23x.projectskyblock.platform.neoforge.inventory.MinecraftItemStackCodec;
import raziel23x.projectskyblock.recipe.CrusherRecipe;
import raziel23x.projectskyblock.registry.ModRecipes;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;
import raziel23x.projectskyblock.simulation.machine.logic.crusher.MaterialCrusherProcessResult;
import raziel23x.projectskyblock.simulation.machine.logic.crusher.MaterialCrusherRecipeMatch;
import raziel23x.projectskyblock.simulation.machine.logic.crusher.MaterialCrusherRecipePort;

/** Minecraft recipe-manager adapter for the engine-owned prototype crusher logic. */
public final class MinecraftMaterialCrusherRecipePort implements MaterialCrusherRecipePort {
    private final Supplier<Level> levelSupplier;
    private final MinecraftItemStackCodec codec;

    public MinecraftMaterialCrusherRecipePort(
            Supplier<Level> levelSupplier,
            MinecraftItemStackCodec codec) {
        this.levelSupplier = Objects.requireNonNull(levelSupplier, "levelSupplier");
        this.codec = Objects.requireNonNull(codec, "codec");
    }

    @Override
    public Optional<MaterialCrusherRecipeMatch> findMatch(SimulationItemStack input) {
        if (input.isEmpty()) {
            return Optional.empty();
        }
        try {
            ItemStack minecraftInput = codec.decode(input);
            return findHolder(minecraftInput).flatMap(holder -> encodeMatch(holder.id().toString(), holder.value()));
        } catch (ItemStackBoundaryException exception) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<MaterialCrusherProcessResult> rollResult(
            String recipeId,
            SimulationItemStack input) {
        Objects.requireNonNull(recipeId, "recipeId");
        if (input.isEmpty()) {
            return Optional.empty();
        }
        try {
            ItemStack minecraftInput = codec.decode(input);
            return findHolder(minecraftInput)
                    .filter(holder -> holder.id().toString().equals(recipeId))
                    .flatMap(holder -> encodeResult(holder.value().rollResult(requireLevel().random)));
        } catch (ItemStackBoundaryException exception) {
            return Optional.empty();
        }
    }

    public boolean accepts(ItemStack stack) {
        return stack != null && !stack.isEmpty() && findHolder(stack).isPresent();
    }

    private Optional<net.minecraft.world.item.crafting.RecipeHolder<CrusherRecipe>> findHolder(
            ItemStack input) {
        Level level = levelSupplier.get();
        if (level == null || input.isEmpty()) {
            return Optional.empty();
        }
        return level.getRecipeManager().getRecipeFor(
                ModRecipes.CRUSHING_TYPE.get(),
                new SingleRecipeInput(input),
                level);
    }

    private Optional<MaterialCrusherRecipeMatch> encodeMatch(
            String recipeId,
            CrusherRecipe recipe) {
        return encodeResult(recipe.maximumResult())
                .filter(result -> !result.isEmpty())
                .map(result -> new MaterialCrusherRecipeMatch(recipeId, result));
    }

    private Optional<MaterialCrusherProcessResult> encodeResult(CrusherProcessingResult result) {
        try {
            return Optional.of(new MaterialCrusherProcessResult(
                    codec.encode(result.primary()),
                    codec.encode(result.byproduct())));
        } catch (ItemStackBoundaryException exception) {
            return Optional.empty();
        }
    }

    private Level requireLevel() {
        Level level = levelSupplier.get();
        if (level == null) {
            throw new IllegalStateException("material crusher level is unavailable");
        }
        return level;
    }
}
