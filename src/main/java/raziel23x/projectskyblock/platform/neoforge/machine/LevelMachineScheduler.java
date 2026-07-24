package raziel23x.projectskyblock.platform.neoforge.machine;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.server.level.ServerLevel;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;
import raziel23x.projectskyblock.simulation.core.SchedulerTickReport;
import raziel23x.projectskyblock.simulation.core.SimulationContext;
import raziel23x.projectskyblock.simulation.core.SimulationFailureStage;
import raziel23x.projectskyblock.simulation.core.SimulationScheduler;

/**
 * Owns one bounded simulation scheduler for one logical server level.
 *
 * <p>Machines register once when loaded. Only participants that execute and produce dirty
 * integration state are queued for platform flushing, avoiding a per-tick scan of sleeping
 * machines.</p>
 */
final class LevelMachineScheduler {
    static final int DEFAULT_MAXIMUM_EXECUTIONS_PER_TICK = 1_024;
    static final int DEFAULT_WORK_UNITS_PER_EXECUTION = 64;

    private final ServerLevel level;
    private final SimulationScheduler scheduler;
    private final Map<String, EngineMachineBlockEntity> machinesByParticipantId = new HashMap<>();
    private final LinkedHashSet<EngineMachineBlockEntity> pendingIntegration = new LinkedHashSet<>();
    private SchedulerTickReport lastReport = new SchedulerTickReport(0, 0, 0, 0, 0, 0);

    LevelMachineScheduler(ServerLevel level) {
        this(level, new SimulationScheduler(
                DEFAULT_MAXIMUM_EXECUTIONS_PER_TICK,
                DEFAULT_WORK_UNITS_PER_EXECUTION));
    }

    LevelMachineScheduler(ServerLevel level, SimulationScheduler scheduler) {
        this.level = Objects.requireNonNull(level, "level");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
    }

    SimulationScheduler scheduler() {
        return scheduler;
    }

    SchedulerTickReport lastReport() {
        return lastReport;
    }

    int registeredMachineCount() {
        return machinesByParticipantId.size();
    }

    void register(EngineMachineBlockEntity machine) {
        Objects.requireNonNull(machine, "machine");
        String participantId = machine.machineRuntime().id().value();
        EngineMachineBlockEntity existing = machinesByParticipantId.putIfAbsent(participantId, machine);
        if (existing != null && existing != machine) {
            throw new IllegalStateException("duplicate engine machine participant id: " + participantId);
        }
    }

    void unregister(EngineMachineBlockEntity machine) {
        if (machine == null || !machine.hasMachineRuntime()) {
            pendingIntegration.remove(machine);
            return;
        }
        String participantId = machine.machineRuntime().id().value();
        machinesByParticipantId.remove(participantId, machine);
        pendingIntegration.remove(machine);
    }

    void requestAllWork() {
        for (EngineMachineBlockEntity machine : List.copyOf(machinesByParticipantId.values())) {
            if (!machine.isRemoved() && machine.getLevel() == level && machine.hasMachineRuntime()) {
                machine.machineRuntime().requestWork();
            }
        }
    }

    void tick() {
        long gameTime = level.getGameTime();
        lastReport = scheduler.tick(
                gameTime,
                (participantId, ignoredGameTime, dirtyState) ->
                        new LevelSimulationContext(gameTime, dirtyState),
                this::afterExecution,
                this::onFailure);
        flushPendingIntegration();
    }

    void close() {
        pendingIntegration.clear();
        machinesByParticipantId.clear();
    }

    private void onFailure(
            String participantId,
            SimulationFailureStage stage,
            RuntimeException failure) {
        ProjectSkyblock.LOGGER.error(
                "Engine participant {} failed during {} in level {}; participant invalidated",
                participantId,
                stage,
                level.dimension().location(),
                failure);
    }

    private void afterExecution(String participantId, DirtyStateTracker dirtyState) {
        if (dirtyState.isClean()) {
            return;
        }
        EngineMachineBlockEntity machine = machinesByParticipantId.get(participantId);
        if (machine != null) {
            pendingIntegration.add(machine);
        }
    }

    private void flushPendingIntegration() {
        if (pendingIntegration.isEmpty()) {
            return;
        }
        List<EngineMachineBlockEntity> batch = List.copyOf(pendingIntegration);
        pendingIntegration.clear();
        for (EngineMachineBlockEntity machine : batch) {
            if (machine.isRemoved() || machine.getLevel() != level || !machine.hasMachineRuntime()) {
                continue;
            }
            String participantId = machine.machineRuntime().id().value();
            try {
                machine.flushMachineIntegrationWork();
            } catch (RuntimeException failure) {
                scheduler.invalidateAfterFailure(
                        participantId,
                        SimulationFailureStage.PLATFORM_INTEGRATION,
                        failure);
                onFailure(participantId, SimulationFailureStage.PLATFORM_INTEGRATION, failure);
            }
        }
    }

    private record LevelSimulationContext(long gameTime, DirtyStateTracker dirtyState)
            implements SimulationContext {}
}
