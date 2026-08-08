package raziel23x.projectskyblock.platform.neoforge.machine.capability;

import java.util.Objects;
import java.util.function.Supplier;
import net.neoforged.neoforge.energy.IEnergyStorage;
import raziel23x.projectskyblock.simulation.machine.component.MachineEnergyComponent;
import raziel23x.projectskyblock.simulation.machine.component.MachineEnergyTransaction;

/** NeoForge energy capability view over backend-owned machine energy state. */
public final class EngineEnergyStorageAdapter implements IEnergyStorage {
    private final Supplier<MachineEnergyComponent> energySupplier;

    public EngineEnergyStorageAdapter(Supplier<MachineEnergyComponent> energySupplier) {
        this.energySupplier = Objects.requireNonNull(energySupplier, "energySupplier");
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0) {
            return 0;
        }
        try (MachineEnergyTransaction transaction = energy().beginTransaction()) {
            long accepted = transaction.receive(maxReceive);
            if (!simulate && transaction.changed()) {
                transaction.commit();
            }
            return toInt(accepted);
        }
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (maxExtract <= 0) {
            return 0;
        }
        try (MachineEnergyTransaction transaction = energy().beginTransaction()) {
            long extracted = transaction.extract(maxExtract);
            if (!simulate && transaction.changed()) {
                transaction.commit();
            }
            return toInt(extracted);
        }
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
