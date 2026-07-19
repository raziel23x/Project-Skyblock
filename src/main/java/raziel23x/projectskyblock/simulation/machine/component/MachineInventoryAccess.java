package raziel23x.projectskyblock.simulation.machine.component;

/** Defines which external inventory operations a machine slot permits. */
public enum MachineInventoryAccess {
    NONE(false, false),
    INPUT(true, false),
    OUTPUT(false, true),
    BIDIRECTIONAL(true, true);

    private final boolean acceptsItems;
    private final boolean providesItems;

    MachineInventoryAccess(boolean acceptsItems, boolean providesItems) {
        this.acceptsItems = acceptsItems;
        this.providesItems = providesItems;
    }

    public boolean acceptsItems() {
        return acceptsItems;
    }

    public boolean providesItems() {
        return providesItems;
    }
}
