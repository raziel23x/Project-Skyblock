package raziel23x.projectskyblock.platform.neoforge.machine.capability;

import java.util.Objects;
import java.util.function.Supplier;
import net.neoforged.neoforge.energy.IEnergyStorage;
import raziel23x.projectskyblock.simulation.machine.component.MachineEnergyComponent;

/** NeoForge energy capability view over backend-owned machine energy state. */
public final class EngineEnergyStorageAdapter implements IEnergyStorage {
    private final Supplier<MachineEnergyComponent> energySupplier;

    public EngineEnergyStorageAdapter(Supplier<MachineEnergyComponent> energySupplier) {
        this.energySupplier = Objects.requireNonNull(energySupplier, "energySupplier");
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0 || !energy().access().acceptsEnergy()) {
            return 0;
        }
        long accepted = Math.min(
                maxReceive,
                Math.min(energy().availableCapacity(), energy().limits().maximumReceivePerOperation()));
        if (!simulate && accepted > 0L) {
            accepted = energy().receive(accepted);
        }
        return toInt(accepted);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (maxExtract <= 0 || !energy().access().providesEnergy()) {
            return 0;
        }
        long extracted = Math.min(
                maxExtract,
                Math.min(energy().storedEnergy(), energy().limits().maximumExtractPerOperation()));
        if (!simulate && extracted > 0L) {
            extracted = energy().extract(extracted);
        }
        return toInt(extracted);
    }

    @Override
    public int getEnergyStored() {
        return toInt(energy().storedEnergy());
    }

    @Override
    public int getMaxEnergyStored() {
        return toInt(energy().capacity());
    }

    @Override
    public boolean canExtract() {
        return energy().access().providesEnergy();
    }

    @Override
    public boolean canReceive() {
        return energy().access().acceptsEnergy();
    }

    private MachineEnergyComponent energy() {
        return Objects.requireNonNull(energySupplier.get(), "energy component");
    }

    private static int toInt(long value) {
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, value));
    }
}
