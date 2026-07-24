package raziel23x.projectskyblock.platform.neoforge.machine;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import raziel23x.projectskyblock.simulation.core.SimulationScheduler;

/** Global NeoForge event adapter for level-scoped machine schedulers. */
public final class EngineMachineLevelManager {
    private static final Map<ServerLevel, LevelMachineScheduler> LEVEL_SCHEDULERS =
            new IdentityHashMap<>();

    private EngineMachineLevelManager() {}

    public static void register(IEventBus gameEventBus) {
        Objects.requireNonNull(gameEventBus, "gameEventBus");
        gameEventBus.addListener(EngineMachineLevelManager::onLevelTick);
        gameEventBus.addListener(EngineMachineLevelManager::onLevelUnload);
    }

    static SimulationScheduler schedulerFor(ServerLevel level) {
        return levelScheduler(level).scheduler();
    }

    static void registerMachine(ServerLevel level, EngineMachineBlockEntity machine) {
        levelScheduler(level).register(machine);
    }

    static void unregisterMachine(ServerLevel level, EngineMachineBlockEntity machine) {
        LevelMachineScheduler scheduler = LEVEL_SCHEDULERS.get(level);
        if (scheduler != null) {
            scheduler.unregister(machine);
        }
    }

    /** Rechecks sleeping and blocked machines after an authoritative datapack reload. */
    public static void requestAllWork(MinecraftServer server) {
        Objects.requireNonNull(server, "server");
        for (ServerLevel level : server.getAllLevels()) {
            LevelMachineScheduler scheduler = LEVEL_SCHEDULERS.get(level);
            if (scheduler != null) {
                scheduler.requestAllWork();
            }
        }
    }

    private static LevelMachineScheduler levelScheduler(ServerLevel level) {
        Objects.requireNonNull(level, "level");
        return LEVEL_SCHEDULERS.computeIfAbsent(level, LevelMachineScheduler::new);
    }

    private static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            LevelMachineScheduler scheduler = LEVEL_SCHEDULERS.get(serverLevel);
            if (scheduler != null) {
                scheduler.tick();
            }
        }
    }

    private static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            LevelMachineScheduler scheduler = LEVEL_SCHEDULERS.remove(serverLevel);
            if (scheduler != null) {
                scheduler.close();
            }
        }
    }
}
