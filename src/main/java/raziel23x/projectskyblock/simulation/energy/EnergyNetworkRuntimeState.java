package raziel23x.projectskyblock.simulation.energy;

import raziel23x.projectskyblock.simulation.core.SimulationState;

/** Authoritative scheduler-facing runtime state for one energy topology. */
public final class EnergyNetworkRuntimeState implements SimulationState {
    private EnergyNetworkActivity activity = EnergyNetworkActivity.SLEEPING;
    private long observedTopologyRevision = -1L;
    private long executionCount;
    private long wakeSignalCount;
    private long sleepCount;
    private long lastExecutionGameTime = -1L;
    private boolean workRequested;

    public EnergyNetworkActivity activity() {
        return activity;
    }

    public long observedTopologyRevision() {
        return observedTopologyRevision;
    }

    public long executionCount() {
        return executionCount;
    }

    public long wakeSignalCount() {
        return wakeSignalCount;
    }

    public long sleepCount() {
        return sleepCount;
    }

    public long lastExecutionGameTime() {
        return lastExecutionGameTime;
    }

    public boolean workRequested() {
        return workRequested;
    }

    /** Records an explicit reason for the scheduler to wake this network. */
    public void requestWork() {
        workRequested = true;
        wakeSignalCount++;
    }

    boolean observeTopology(long revision) {
        if (revision < 0) {
            throw new IllegalArgumentException("topology revision must be non-negative");
        }
        if (observedTopologyRevision == revision) {
            return false;
        }
        observedTopologyRevision = revision;
        return true;
    }

    void recordExecution(long gameTime) {
        executionCount++;
        lastExecutionGameTime = gameTime;
    }

    void becomeActive() {
        activity = EnergyNetworkActivity.ACTIVE;
        workRequested = false;
    }

    void becomeIdle() {
        activity = EnergyNetworkActivity.IDLE;
    }

    void becomeSleeping() {
        if (activity != EnergyNetworkActivity.SLEEPING) {
            sleepCount++;
        }
        activity = EnergyNetworkActivity.SLEEPING;
    }
}
