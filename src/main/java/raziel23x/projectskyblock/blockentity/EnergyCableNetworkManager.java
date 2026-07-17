package raziel23x.projectskyblock.blockentity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import raziel23x.projectskyblock.block.BasicEnergyCableBlock;

/**
 * Builds and caches cable-network topology for one server tick.
 *
 * <p>The former implementation performed a complete breadth-first scan from
 * every cable block entity before discovering which cable was the controller.
 * This cache maps every cable position discovered by the first scan to the same
 * immutable network snapshot, so each connected network is traversed only once
 * per level tick.</p>
 */
final class EnergyCableNetworkManager {
    private static final int MAX_NETWORK_CABLES = 4096;
    private static final Map<Level, TickCache> LEVEL_CACHES = new WeakHashMap<>();

    private EnergyCableNetworkManager() {
    }

    static CableNetwork getNetwork(Level level, BlockPos start) {
        TickCache cache = LEVEL_CACHES.computeIfAbsent(level, ignored -> new TickCache());
        long gameTime = level.getGameTime();
        if (cache.gameTime != gameTime) {
            cache.gameTime = gameTime;
            cache.networksByCable.clear();
        }

        BlockPos immutableStart = start.immutable();
        CableNetwork cached = cache.networksByCable.get(immutableStart);
        if (cached != null) {
            return cached;
        }

        CableNetwork network = scanNetwork(level, immutableStart);
        for (BlockPos cablePos : network.cables()) {
            cache.networksByCable.put(cablePos, network);
        }
        return network;
    }

    private static CableNetwork scanNetwork(Level level, BlockPos start) {
        ArrayDeque<BlockPos> open = new ArrayDeque<>();
        Set<BlockPos> cables = new HashSet<>();
        Set<EndpointKey> endpointKeys = new HashSet<>();
        List<EnergyEndpoint> endpoints = new ArrayList<>();
        BlockPos controller = start;

        open.add(start);
        while (!open.isEmpty() && cables.size() < MAX_NETWORK_CABLES) {
            BlockPos cablePos = open.removeFirst().immutable();
            if (!cables.add(cablePos)) {
                continue;
            }
            if (compare(cablePos, controller) < 0) {
                controller = cablePos;
            }

            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = cablePos.relative(direction);
                if (level.getBlockState(neighborPos).getBlock() instanceof BasicEnergyCableBlock) {
                    if (!cables.contains(neighborPos)) {
                        open.addLast(neighborPos.immutable());
                    }
                    continue;
                }

                Direction neighborFace = direction.getOpposite();
                EndpointKey key = new EndpointKey(neighborPos.immutable(), neighborFace);
                if (!endpointKeys.add(key)) {
                    continue;
                }

                IEnergyStorage storage = level.getCapability(
                        Capabilities.EnergyStorage.BLOCK,
                        neighborPos,
                        neighborFace);
                if (storage != null) {
                    endpoints.add(new EnergyEndpoint(neighborPos.immutable(), neighborFace, storage));
                }
            }
        }

        return new CableNetwork(controller.immutable(), Set.copyOf(cables), List.copyOf(endpoints));
    }

    private static int compare(BlockPos first, BlockPos second) {
        int y = Integer.compare(first.getY(), second.getY());
        if (y != 0) {
            return y;
        }
        int z = Integer.compare(first.getZ(), second.getZ());
        if (z != 0) {
            return z;
        }
        return Integer.compare(first.getX(), second.getX());
    }

    record CableNetwork(BlockPos controller, Set<BlockPos> cables, List<EnergyEndpoint> endpoints) {
    }

    record EnergyEndpoint(BlockPos pos, Direction face, IEnergyStorage storage) {
        boolean sameTarget(EnergyEndpoint other) {
            return pos.equals(other.pos) && face == other.face;
        }
    }

    private record EndpointKey(BlockPos pos, Direction face) {
    }

    private static final class TickCache {
        private long gameTime = Long.MIN_VALUE;
        private final Map<BlockPos, CableNetwork> networksByCable = new HashMap<>();
    }
}
