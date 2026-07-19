package raziel23x.projectskyblock.simulation.machine.component;

/** Defines which external heat operations a machine thermal component permits. */
public enum MachineThermalAccess {
    NONE(false, false),
    INPUT(true, false),
    OUTPUT(false, true),
    BIDIRECTIONAL(true, true);

    private final boolean acceptsHeat;
    private final boolean providesHeat;

    MachineThermalAccess(boolean acceptsHeat, boolean providesHeat) {
        this.acceptsHeat = acceptsHeat;
        this.providesHeat = providesHeat;
    }

    public boolean acceptsHeat() {
        return acceptsHeat;
    }

    public boolean providesHeat() {
        return providesHeat;
    }
}
