package raziel23x.projectskyblock.simulation.machine.component;

import java.util.Objects;
import raziel23x.projectskyblock.simulation.core.DirtyFlag;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;

/**
 * Recipe-independent processing state owned by a simulated machine.
 *
 * <p>The component tracks only the durable lifecycle of one active operation. Recipe
 * matching, inventory transactions, energy consumption, thermal requirements, and output
 * insertion remain in machine logic or future focused collaborators. This keeps processing
 * reusable without hiding resource behavior inside an oversized abstraction.</p>
 */
public final class MachineProcessingComponent {
    private final DirtyStateTracker dirtyState;
    private final Runnable wakeSignal;
    private MachineProcessingStatus status = MachineProcessingStatus.IDLE;
    private String processId = "";
    private long completedUnits;
    private long requiredUnits;
    private String blockedReason = "";
    private long completedProcesses;
    private long changeCount;

    public MachineProcessingComponent() {
        this(new DirtyStateTracker(), () -> { });
    }

    public MachineProcessingComponent(DirtyStateTracker dirtyState, Runnable wakeSignal) {
        this.dirtyState = Objects.requireNonNull(dirtyState, "dirtyState");
        this.wakeSignal = Objects.requireNonNull(wakeSignal, "wakeSignal");
    }

    public DirtyStateTracker dirtyState() {
        return dirtyState;
    }

    public MachineProcessingStatus status() {
        return status;
    }

    public boolean idle() {
        return status == MachineProcessingStatus.IDLE;
    }

    public boolean running() {
        return status == MachineProcessingStatus.RUNNING;
    }

    public boolean blocked() {
        return status == MachineProcessingStatus.BLOCKED;
    }

    public boolean readyToComplete() {
        return status == MachineProcessingStatus.READY_TO_COMPLETE;
    }

    public String processId() {
        return processId;
    }

    public long completedUnits() {
        return completedUnits;
    }

    public long requiredUnits() {
        return requiredUnits;
    }

    public long remainingUnits() {
        return requiredUnits - completedUnits;
    }

    public String blockedReason() {
        return blockedReason;
    }

    /** Starts a new operation. An active operation must be explicitly cleared first. */
    public void start(String processId, long requiredUnits) {
        requireProcessId(processId);
        if (requiredUnits <= 0L) {
            throw new IllegalArgumentException("required units must be positive");
        }
        if (!idle()) {
            throw new IllegalStateException("processing component already owns an active operation");
        }
        this.processId = processId;
        this.requiredUnits = requiredUnits;
        this.completedUnits = 0L;
        this.blockedReason = "";
        this.status = MachineProcessingStatus.RUNNING;
        markTransition();
    }

    /**
     * Advances bounded progress and returns the units actually accepted.
     *
     * <p>Progress changes do not issue an additional wake request because running machine
     * logic already returns an explicit scheduling decision. This avoids redundant scheduler
     * churn on every processing step.</p>
     */
    public long advance(long requestedUnits) {
        if (requestedUnits < 0L) {
            throw new IllegalArgumentException("requested units must be non-negative");
        }
        if (requestedUnits == 0L) {
            return 0L;
        }
        if (!running()) {
            return 0L;
        }
        long accepted = Math.min(requestedUnits, remainingUnits());
        if (accepted == 0L) {
            return 0L;
        }
        completedUnits = Math.addExact(completedUnits, accepted);
        if (completedUnits == requiredUnits) {
            status = MachineProcessingStatus.READY_TO_COMPLETE;
        }
        markProgress();
        return accepted;
    }

    /** Marks the current operation blocked until a relevant external change wakes it. */
    public void block(String reason) {
        requireActive();
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("blocked reason must not be blank");
        }
        if (status == MachineProcessingStatus.BLOCKED && blockedReason.equals(reason)) {
            return;
        }
        status = MachineProcessingStatus.BLOCKED;
        blockedReason = reason;
        markTransition();
    }

    /** Resumes a blocked operation after its resource condition has changed. */
    public void resume() {
        if (!blocked()) {
            return;
        }
        blockedReason = "";
        status = completedUnits == requiredUnits
                ? MachineProcessingStatus.READY_TO_COMPLETE
                : MachineProcessingStatus.RUNNING;
        markTransition();
    }

    /** Completes and clears an operation only after machine logic commits its outputs. */
    public void complete() {
        if (!readyToComplete()) {
            throw new IllegalStateException("processing operation is not ready to complete");
        }
        completedProcesses = Math.addExact(completedProcesses, 1L);
        clearInternal();
        markTransition();
    }

    /** Cancels and clears any active operation without counting it as completed. */
    public void cancel() {
        if (idle()) {
            return;
        }
        clearInternal();
        markTransition();
    }

    /** Restores validated persistent state without replaying runtime wake behavior. */
    public void restore(
            MachineProcessingStatus status,
            String processId,
            long completedUnits,
            long requiredUnits,
            String blockedReason,
            long completedProcesses) {
        validateRestore(status, processId, completedUnits, requiredUnits, blockedReason, completedProcesses);
        this.status = status;
        this.processId = processId;
        this.completedUnits = completedUnits;
        this.requiredUnits = requiredUnits;
        this.blockedReason = blockedReason;
        this.completedProcesses = completedProcesses;
        dirtyState.mark(DirtyFlag.CLIENT_SYNC);
    }

    public MachineProcessingDiagnostics diagnostics() {
        return new MachineProcessingDiagnostics(
                status,
                processId,
                completedUnits,
                requiredUnits,
                blockedReason,
                completedProcesses,
                changeCount);
    }

    private void clearInternal() {
        status = MachineProcessingStatus.IDLE;
        processId = "";
        completedUnits = 0L;
        requiredUnits = 0L;
        blockedReason = "";
    }

    private void markProgress() {
        changeCount = Math.addExact(changeCount, 1L);
        dirtyState.mark(DirtyFlag.PERSISTENCE, DirtyFlag.CLIENT_SYNC);
    }

    private void markTransition() {
        changeCount = Math.addExact(changeCount, 1L);
        dirtyState.mark(DirtyFlag.PERSISTENCE, DirtyFlag.CLIENT_SYNC, DirtyFlag.SCHEDULER);
        wakeSignal.run();
    }

    private void requireActive() {
        if (idle()) {
            throw new IllegalStateException("processing component has no active operation");
        }
    }

    private static void requireProcessId(String processId) {
        if (processId == null || processId.isBlank()) {
            throw new IllegalArgumentException("process id must not be blank");
        }
    }

    public static void validateRestore(
            MachineProcessingStatus status,
            String processId,
            long completedUnits,
            long requiredUnits,
            String blockedReason,
            long completedProcesses) {
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(processId, "processId");
        Objects.requireNonNull(blockedReason, "blockedReason");
        if (completedUnits < 0L || requiredUnits < 0L || completedProcesses < 0L) {
            throw new IllegalArgumentException("processing state values must be non-negative");
        }
        if (status == MachineProcessingStatus.IDLE) {
            if (!processId.isEmpty() || completedUnits != 0L || requiredUnits != 0L || !blockedReason.isEmpty()) {
                throw new IllegalArgumentException("idle processing state must be empty");
            }
            return;
        }
        requireProcessId(processId);
        if (requiredUnits <= 0L || completedUnits > requiredUnits) {
            throw new IllegalArgumentException("active processing progress is invalid");
        }
        if (status == MachineProcessingStatus.READY_TO_COMPLETE && completedUnits != requiredUnits) {
            throw new IllegalArgumentException("ready processing state must be complete");
        }
        if (status == MachineProcessingStatus.BLOCKED && blockedReason.isBlank()) {
            throw new IllegalArgumentException("blocked processing state requires a reason");
        }
        if (status != MachineProcessingStatus.BLOCKED && !blockedReason.isEmpty()) {
            throw new IllegalArgumentException("only blocked processing state may have a reason");
        }
    }
}
