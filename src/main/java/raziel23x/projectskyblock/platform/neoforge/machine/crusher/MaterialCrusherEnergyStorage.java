package raziel23x.projectskyblock.platform.neoforge.machine.crusher;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import net.neoforged.neoforge.energy.IEnergyStorage;

/** Dynamic FE-enable gate over the engine-owned receive-only crusher energy adapter. */
public final class MaterialCrusherEnergyStorage implements IEnergyStorage {
    private final IEnergyStorage delegate;
    private final BooleanSupplier receivingEnabled;

    public MaterialCrusherEnergyStorage(
            IEnergyStorage delegate,
            BooleanSupplier receivingEnabled) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.receivingEnabled = Objects.requireNonNull(receivingEnabled, "receivingEnabled");
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return receivingEnabled.getAsBoolean()
                ? delegate.receiveEnergy(maxReceive, simulate)
                : 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return delegate.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return delegate.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return receivingEnabled.getAsBoolean() && delegate.canReceive();
    }
}
