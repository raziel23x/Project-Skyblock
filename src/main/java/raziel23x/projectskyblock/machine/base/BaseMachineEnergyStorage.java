package raziel23x.projectskyblock.machine.base;

import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Shared receive-only machine energy storage.
 * External capability users may insert FE but cannot extract it. Machines use
 * {@link #consumeInternally(int)} for their own processing costs.
 */
public class BaseMachineEnergyStorage implements IEnergyStorage {
    private final int capacity;
    private final Runnable changeListener;
    private int energy;
    private boolean receivingEnabled = true;

    public BaseMachineEnergyStorage(int capacity, Runnable changeListener) {
        this.capacity = Math.max(0, capacity);
        this.changeListener = changeListener;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive() || maxReceive <= 0) {
            return 0;
        }

        int received = Math.min(capacity - energy, maxReceive);
        if (!simulate && received > 0) {
            energy += received;
            changeListener.run();
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return receivingEnabled && capacity > 0;
    }

    public boolean consumeInternally(int amount) {
        if (amount <= 0) {
            return true;
        }
        if (energy < amount) {
            return false;
        }

        energy -= amount;
        changeListener.run();
        return true;
    }

    public void setStoredEnergy(int value) {
        energy = Math.max(0, Math.min(capacity, value));
    }

    public void setReceivingEnabled(boolean enabled) {
        receivingEnabled = enabled;
    }
}
