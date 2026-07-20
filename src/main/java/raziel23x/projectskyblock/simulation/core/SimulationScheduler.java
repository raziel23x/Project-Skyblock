package raziel23x.projectskyblock.simulation.core;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

/**
 * Explicit, bounded scheduler for simulation participants.
 *
 * <p>Sleeping and blocked participants perform no recurring work. A participant
 * executes only after it is registered and woken, or when its scheduled time
 * becomes due.</p>
 */
public final class SimulationScheduler {
    private final Map<String, Entry> entries = new HashMap<>();
    private final ArrayDeque<Entry> readyQueue = new ArrayDeque<>();
    private final PriorityQueue<Ticket> scheduledQueue = new PriorityQueue<>();
    private final int maximumExecutionsPerTick;
    private final int workUnitsPerExecution;

    public SimulationScheduler(int maximumExecutionsPerTick, int workUnitsPerExecution) {
        if (maximumExecutionsPerTick <= 0) {
            throw new IllegalArgumentException("maximum executions per tick must be positive");
        }
        if (workUnitsPerExecution <= 0) {
            throw new IllegalArgumentException("work units per execution must be positive");
        }
        this.maximumExecutionsPerTick = maximumExecutionsPerTick;
        this.workUnitsPerExecution = workUnitsPerExecution;
    }

    public void register(String participantId, SimulationParticipant<?> participant) {
        register(participantId, participant, new DirtyStateTracker());
    }

    /**
     * Registers a participant with an externally owned dirty tracker.
     *
     * <p>This overload allows a composed backend object and the scheduler context to
     * observe the same allocation-free dirty state instead of copying or polling it.</p>
     */
    public void register(
            String participantId,
            SimulationParticipant<?> participant,
            DirtyStateTracker dirtyState) {
        requireId(participantId);
        Objects.requireNonNull(participant, "participant");
        Objects.requireNonNull(dirtyState, "dirtyState");
        if (entries.putIfAbsent(
                participantId,
                new Entry(participantId, participant, dirtyState)) != null) {
            throw new IllegalArgumentException("participant is already registered: " + participantId);
        }
    }

    public boolean remove(String participantId) {
        requireId(participantId);
        Entry removed = entries.remove(participantId);
        if (removed == null) {
            return false;
        }
        removed.generation++;
        removed.queuedReady = false;
        removed.wakeRequestedDuringExecution = false;
        return true;
    }

    /** Wakes a sleeping, scheduled, or blocked participant for reevaluation. */
    public boolean wake(String participantId) {
        Entry entry = entries.get(requireId(participantId));
        if (entry == null || entry.lifecycle == SimulationLifecycle.INVALID) {
            return false;
        }
        if (entry.lifecycle == SimulationLifecycle.ACTIVE) {
            entry.wakeRequestedDuringExecution = true;
            return true;
        }
        enqueueReady(entry);
        return true;
    }

    public boolean contains(String participantId) {
        return entries.containsKey(requireId(participantId));
    }

    public SimulationLifecycle lifecycleOf(String participantId) {
        Entry entry = entries.get(requireId(participantId));
        if (entry == null) {
            throw new IllegalArgumentException("participant is not registered: " + participantId);
        }
        return entry.lifecycle;
    }

    public String statusReason(String participantId) {
        Entry entry = entries.get(requireId(participantId));
        if (entry == null) {
            throw new IllegalArgumentException("participant is not registered: " + participantId);
        }
        return entry.statusReason;
    }

    public DirtyStateTracker dirtyStateOf(String participantId) {
        Entry entry = entries.get(requireId(participantId));
        if (entry == null) {
            throw new IllegalArgumentException("participant is not registered: " + participantId);
        }
        return entry.dirtyState;
    }

    public int size() {
        return entries.size();
    }

    /** Executes at most the configured number of participants for this game tick. */
    public SchedulerTickReport tick(long gameTime, SimulationContextFactory contextFactory) {
        return tick(gameTime, contextFactory, SimulationExecutionObserver.NONE);
    }

    /**
     * Executes one bounded scheduler slice and reports each participant that actually ran.
     *
     * <p>The observer exists so platform adapters can enqueue only required integration work
     * without scanning every registered participant every game tick.</p>
     */
    public SchedulerTickReport tick(
            long gameTime,
            SimulationContextFactory contextFactory,
            SimulationExecutionObserver executionObserver) {
        if (gameTime < 0) {
            throw new IllegalArgumentException("game time must be non-negative");
        }
        Objects.requireNonNull(contextFactory, "contextFactory");
        Objects.requireNonNull(executionObserver, "executionObserver");

        releaseDueParticipants(gameTime);

        int executed = 0;
        while (executed < maximumExecutionsPerTick && !readyQueue.isEmpty()) {
            Entry entry = readyQueue.removeFirst();
            entry.queuedReady = false;
            if (entries.get(entry.id) != entry || entry.lifecycle != SimulationLifecycle.READY) {
                continue;
            }

            entry.lifecycle = SimulationLifecycle.ACTIVE;
            SimulationContext context = Objects.requireNonNull(
                    contextFactory.create(entry.id, gameTime, entry.dirtyState),
                    "contextFactory returned null");
            SimulationResult result = Objects.requireNonNull(
                    execute(entry.participant, context, new SimulationBudget(workUnitsPerExecution)),
                    "participant returned null");
            applyResult(entry, result, gameTime);
            applyDeferredWake(entry, gameTime);
            executionObserver.afterExecution(entry.id, entry.dirtyState);
            executed++;
        }

        return createReport(executed);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static SimulationResult execute(
            SimulationParticipant participant,
            SimulationContext context,
            SimulationBudget budget) {
        return participant.execute(context, budget);
    }

    private void applyResult(Entry entry, SimulationResult result, long gameTime) {
        if (result instanceof SimulationResult.Sleep) {
            entry.lifecycle = SimulationLifecycle.SLEEPING;
            entry.statusReason = "";
        } else if (result instanceof SimulationResult.ContinueNextTick) {
            schedule(entry, gameTime + 1L);
        } else if (result instanceof SimulationResult.ScheduleAt scheduleAt) {
            long dueTime = Math.max(gameTime + 1L, scheduleAt.gameTime());
            schedule(entry, dueTime);
        } else if (result instanceof SimulationResult.Blocked blocked) {
            entry.lifecycle = SimulationLifecycle.BLOCKED;
            entry.statusReason = blocked.reason();
        } else if (result instanceof SimulationResult.Invalid invalid) {
            entry.lifecycle = SimulationLifecycle.INVALID;
            entry.statusReason = invalid.reason();
            entry.generation++;
        } else {
            throw new IllegalStateException("unhandled simulation result: " + result.getClass().getName());
        }
    }

    private void applyDeferredWake(Entry entry, long gameTime) {
        if (!entry.wakeRequestedDuringExecution) {
            return;
        }
        entry.wakeRequestedDuringExecution = false;
        if (entry.lifecycle != SimulationLifecycle.INVALID) {
            schedule(entry, gameTime + 1L);
        }
    }

    private void schedule(Entry entry, long gameTime) {
        entry.lifecycle = SimulationLifecycle.SCHEDULED;
        entry.statusReason = "";
        entry.nextRunTime = gameTime;
        entry.generation++;
        scheduledQueue.add(new Ticket(gameTime, entry.id, entry.generation));
    }

    private void releaseDueParticipants(long gameTime) {
        while (!scheduledQueue.isEmpty() && scheduledQueue.peek().gameTime <= gameTime) {
            Ticket ticket = scheduledQueue.remove();
            Entry entry = entries.get(ticket.participantId);
            if (entry == null
                    || entry.lifecycle != SimulationLifecycle.SCHEDULED
                    || entry.generation != ticket.generation
                    || entry.nextRunTime != ticket.gameTime) {
                continue;
            }
            enqueueReady(entry);
        }
    }

    private void enqueueReady(Entry entry) {
        entry.generation++;
        entry.lifecycle = SimulationLifecycle.READY;
        entry.statusReason = "";
        if (!entry.queuedReady) {
            entry.queuedReady = true;
            readyQueue.addLast(entry);
        }
    }

    private SchedulerTickReport createReport(int executed) {
        int sleeping = 0;
        int ready = 0;
        int scheduled = 0;
        int blocked = 0;
        int invalid = 0;
        for (Entry entry : entries.values()) {
            switch (entry.lifecycle) {
                case SLEEPING -> sleeping++;
                case READY, ACTIVE -> ready++;
                case SCHEDULED -> scheduled++;
                case BLOCKED -> blocked++;
                case INVALID -> invalid++;
            }
        }
        return new SchedulerTickReport(executed, sleeping, ready, scheduled, blocked, invalid);
    }

    private static String requireId(String participantId) {
        if (participantId == null || participantId.isBlank()) {
            throw new IllegalArgumentException("participant id must not be blank");
        }
        return participantId;
    }

    private static final class Entry {
        private final String id;
        private final SimulationParticipant<?> participant;
        private final DirtyStateTracker dirtyState;
        private SimulationLifecycle lifecycle = SimulationLifecycle.SLEEPING;
        private String statusReason = "";
        private long nextRunTime = -1L;
        private long generation;
        private boolean queuedReady;
        private boolean wakeRequestedDuringExecution;

        private Entry(
                String id,
                SimulationParticipant<?> participant,
                DirtyStateTracker dirtyState) {
            this.id = id;
            this.participant = participant;
            this.dirtyState = dirtyState;
        }
    }

    private record Ticket(long gameTime, String participantId, long generation)
            implements Comparable<Ticket> {
        @Override
        public int compareTo(Ticket other) {
            int timeComparison = Long.compare(gameTime, other.gameTime);
            return timeComparison != 0 ? timeComparison : participantId.compareTo(other.participantId);
        }
    }
}
