package raziel23x.projectskyblock.simulation.machine.component;

/** Defines which external energy operations a machine energy component permits. */
public enum MachineEnergyAccess {
    NONE(false, false),
    INPUT(true, false),
    OUTPUT(false, true),
    BIDIRECTIONAL(true, true);

    private final boolean acceptsEnergy;
    private final boolean providesEnergy;

    MachineEnergyAccess(boolean acceptsEnergy, boolean providesEnergy) {
        this.acceptsEnergy = acceptsEnergy;
        this.providesEnergy = providesEnergy;
    }

    public boolean acceptsEnergy() {
        return acceptsEnergy;
    }

    public boolean providesEnergy() {
        return providesEnergy;
    }
}
