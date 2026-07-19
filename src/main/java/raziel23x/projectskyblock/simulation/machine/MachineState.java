package raziel23x.projectskyblock.simulation.machine;

import java.util.Objects;
import raziel23x.projectskyblock.simulation.core.SimulationState;

/**
 * Authoritative machine state composed from lifecycle metadata and typed machine data.
 *
 * <p>The contained data remains Minecraft-independent. Inventories, energy, thermal,
 * fluids, and recipes can later be supplied as typed backend state rather than being
 * embedded in a BlockEntity.</p>
 */
public final class MachineState<S extends SimulationState> implements SimulationState {
    private final MachineId id;
    private final S data;
    private MachineActivity activity = MachineActivity.SLEEPING;
    private boolean enabled = true;
    private boolean workRequested;
    private long executionCount;
    private long lastExecutionGameTime = -1L;
    private String statusReason = "";

    public MachineState(MachineId id, S data) {
        this.id = Objects.requireNonNull(id, "id");
        this.data = Objects.requireNonNull(data, "data");
    }

    public MachineId id() {
        return id;
    }

    public S data() {
        return data;
    }

    public MachineActivity activity() {
        return activity;
    }

    public boolean enabled() {
        return enabled;
    }

    public boolean workRequested() {
        return workRequested;
    }

    public long executionCount() {
        return executionCount;
    }

    public long lastExecutionGameTime() {
        return lastExecutionGameTime;
    }

    public String statusReason() {
        return statusReason;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            workRequested = false;
            activity = MachineActivity.SLEEPING;
            statusReason = "";
        }
    }

    public void requestWork() {
        if (enabled && activity != MachineActivity.INVALID) {
            workRequested = true;
        }
    }

    void beginExecution(long gameTime) {
        if (gameTime < 0) {
            throw new IllegalArgumentException("game time must be non-negative");
        }
        executionCount++;
        lastExecutionGameTime = gameTime;
        workRequested = false;
        activity = MachineActivity.RUNNING;
        statusReason = "";
    }

    void becomeSleeping() {
        activity = MachineActivity.SLEEPING;
        statusReason = "";
    }

    void becomeRunning() {
        activity = MachineActivity.RUNNING;
        statusReason = "";
    }

    void becomeBlocked(String reason) {
        activity = MachineActivity.BLOCKED;
        statusReason = requireReason(reason);
    }

    void becomeInvalid(String reason) {
        activity = MachineActivity.INVALID;
        workRequested = false;
        statusReason = requireReason(reason);
    }

    private static String requireReason(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
        return reason;
    }
}
