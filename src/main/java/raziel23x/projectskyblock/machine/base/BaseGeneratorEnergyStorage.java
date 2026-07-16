package raziel23x.projectskyblock.machine.base;

import java.util.Objects;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Shared generator energy buffer.
 *
 * External capability users may extract FE but cannot insert it. Generator
 * logic adds power through {@link #generateInternally(int)}.
 */
public class BaseGeneratorEnergyStorage implements IEnergyStorage {
    private final int capacity;
    private final int maxExtract;
    private final Runnable changeListener;
    private int energy;
    private boolean extractionEnabled = true;

    public BaseGeneratorEnergyStorage(
            int capacity,
            int maxExtract,
            Runnable changeListener) {
        this.capacity = Math.max(0, capacity);
        this.maxExtract = Math.max(0, maxExtract);
        this.changeListener = Objects.requireNonNull(changeListener, "changeListener");
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract() || maxExtract <= 0) {
            return 0;
        }

        int extracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate && extracted > 0) {
            energy -= extracted;
            changeListener.run();
        }
        return extracted;
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
        return extractionEnabled && maxExtract > 0 && energy > 0;
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    /**
     * Adds generated FE to the internal buffer and returns the amount accepted.
     */
    public int generateInternally(int amount) {
        if (amount <= 0 || capacity <= 0) {
            return 0;
        }

        int accepted = Math.min(capacity - energy, amount);
        if (accepted > 0) {
            energy += accepted;
            changeListener.run();
        }
        return accepted;
    }

    public int getRemainingCapacity() {
        return Math.max(0, capacity - energy);
    }

    public void setStoredEnergy(int value) {
        energy = Math.max(0, Math.min(capacity, value));
    }

    public void setExtractionEnabled(boolean enabled) {
        extractionEnabled = enabled;
    }
}
