package raziel23x.projectskyblock.simulation.transport;

import java.util.List;
import raziel23x.projectskyblock.simulation.core.SimulationState;

/** Authoritative scheduler and fairness state for one typed transport topology. */
public final class TransportNetworkRuntimeState implements SimulationState {
    private TransportNetworkActivity activity = TransportNetworkActivity.SLEEPING;
    private long observedTopologyRevision = -1L;
    private long executionCount;
    private long dispatchSequence;
    private long wakeSignalCount;
    private long sleepCount;
    private long stableNoProgressCount;
    private long lastExecutionGameTime = -1L;
    private long stalledStateFingerprint;
    private boolean stalledStateFingerprintPresent;
    private boolean workRequested;
    private List<TransportChannelStepResult> lastChannelResults = List.of();

    public TransportNetworkActivity activity() {
        return activity;
    }

    public long observedTopologyRevision() {
        return observedTopologyRevision;
    }

    public long executionCount() {
        return executionCount;
    }

    public long dispatchSequence() {
        return dispatchSequence;
    }

    public long wakeSignalCount() {
        return wakeSignalCount;
    }

    public long sleepCount() {
        return sleepCount;
    }

    public long stableNoProgressCount() {
        return stableNoProgressCount;
    }

    public long lastExecutionGameTime() {
        return lastExecutionGameTime;
    }

    public boolean workRequested() {
        return workRequested;
    }

    public List<TransportChannelStepResult> lastChannelResults() {
        return lastChannelResults;
    }

    public void requestWork() {
        workRequested = true;
        wakeSignalCount = Math.addExact(wakeSignalCount, 1L);
    }

    boolean observeTopology(long revision) {
        if (revision < 0L) {
            throw new IllegalArgumentException("topology revision must be non-negative");
        }
        if (observedTopologyRevision == revision) {
            return false;
        }
        observedTopologyRevision = revision;
        return true;
    }

    void recordExecution(long gameTime) {
        executionCount = Math.addExact(executionCount, 1L);
        lastExecutionGameTime = gameTime;
    }

    void consumeWorkRequest() {
        workRequested = false;
    }

    void advanceDispatchSequence() {
        dispatchSequence = Math.addExact(dispatchSequence, 1L);
    }

    void recordStep(TransportNetworkStepResult result) {
        lastChannelResults = result.channelResults();
    }

    void becomeActive() {
        activity = TransportNetworkActivity.ACTIVE;
        clearStallConfirmation();
    }

    void beginStallConfirmation(long stateFingerprint) {
        activity = TransportNetworkActivity.CONFIRMING_STALL;
        stalledStateFingerprint = stateFingerprint;
        stalledStateFingerprintPresent = true;
        stableNoProgressCount = 1L;
    }

    boolean confirmsStableStall(long stateFingerprint) {
        return stalledStateFingerprintPresent && stalledStateFingerprint == stateFingerprint;
    }

    void recordConfirmedStall() {
        stableNoProgressCount = Math.addExact(stableNoProgressCount, 1L);
    }

    void clearStallConfirmation() {
        stalledStateFingerprintPresent = false;
        stableNoProgressCount = 0L;
    }

    void becomeSleeping() {
        if (activity != TransportNetworkActivity.SLEEPING) {
            sleepCount = Math.addExact(sleepCount, 1L);
        }
        activity = TransportNetworkActivity.SLEEPING;
        stalledStateFingerprintPresent = false;
    }
}
