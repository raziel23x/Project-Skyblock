package raziel23x.projectskyblock.machine.fluid;

import java.util.Objects;
import java.util.function.Predicate;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

/**
 * Shared machine fluid tank with validation and a centralized dirty callback.
 */
public class BaseMachineFluidTank extends FluidTank {
    private final Predicate<FluidStack> validator;
    private final Runnable changeListener;

    public BaseMachineFluidTank(
            int capacity,
            Predicate<FluidStack> validator,
            Runnable changeListener) {
        super(Math.max(0, capacity));
        this.validator = Objects.requireNonNull(validator, "validator");
        this.changeListener = Objects.requireNonNull(changeListener, "changeListener");
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return !stack.isEmpty() && validator.test(stack);
    }

    @Override
    protected void onContentsChanged() {
        changeListener.run();
    }
}
