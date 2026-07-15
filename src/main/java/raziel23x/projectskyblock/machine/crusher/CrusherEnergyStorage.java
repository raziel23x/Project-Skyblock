package raziel23x.projectskyblock.machine.crusher;

import raziel23x.projectskyblock.machine.base.BaseMachineEnergyStorage;

/** Crusher-specific type alias for the shared machine energy implementation. */
public final class CrusherEnergyStorage extends BaseMachineEnergyStorage {
    public CrusherEnergyStorage(int capacity, Runnable changeListener) {
        super(capacity, changeListener);
    }
}
