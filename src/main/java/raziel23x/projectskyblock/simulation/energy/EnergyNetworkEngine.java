package raziel23x.projectskyblock.simulation.energy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Stateless solver for one deterministic transfer across an energy-network route. */
public final class EnergyNetworkEngine {
    private EnergyNetworkEngine() {
    }

    /**
     * Moves energy over the topology's deterministic minimum-hop route.
     *
     * <p>This milestone intentionally solves only one transfer at a time. Shared-edge
     * reservations and fair distribution between competing transfers remain separate
     * network-scheduling concerns.</p>
     */
    public static Optional<EnergyNetworkTransferResult> transfer(
            EnergyNetworkTopology topology,
            EnergyNetworkNodeId sourceNode,
            EnergyBuffer source,
            EnergyNetworkNodeId targetNode,
            EnergyBuffer target,
            long requestedEnergy) {
        Objects.requireNonNull(topology, "topology");
        Objects.requireNonNull(sourceNode, "sourceNode");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(targetNode, "targetNode");
        Objects.requireNonNull(target, "target");
        if (requestedEnergy < 0) {
            throw new IllegalArgumentException("requested energy must be non-negative");
        }
        if (sourceNode.equals(targetNode)) {
            throw new IllegalArgumentException("source and target nodes must be different");
        }
        if (source == target) {
            throw new IllegalArgumentException("source and target buffers must be different");
        }

        Optional<EnergyRoute> routeResult = topology.findRoute(sourceNode, targetNode);
        if (routeResult.isEmpty()) {
            return Optional.empty();
        }
        EnergyRoute route = routeResult.get();

        long sourceLimit = Math.min(source.storedEnergy(), source.limits().maximumExtractPerTick());
        long routeLimit = route.maximumTransferPerTick();
        long targetLimit = Math.min(target.availableCapacity(), target.limits().maximumReceivePerTick());
        long upperBound = Math.min(requestedEnergy, Math.min(sourceLimit, routeLimit));
        long extractedPlanned = maximumInputForTarget(route, upperBound, targetLimit);
        RouteCalculation calculation = calculateRoute(route, extractedPlanned);

        long extracted = source.extract(extractedPlanned);
        long delivered = target.receive(calculation.deliveredEnergy());
        if (extracted != extractedPlanned || delivered != calculation.deliveredEnergy()) {
            throw new IllegalStateException("energy buffer violated its advertised limits");
        }

        EnergyTransfer transfer = new EnergyTransfer(
                requestedEnergy,
                extracted,
                delivered,
                extracted - delivered);
        return Optional.of(new EnergyNetworkTransferResult(route, transfer, calculation.hops()));
    }

    private static long maximumInputForTarget(
            EnergyRoute route,
            long upperBound,
            long targetAcceptance) {
        long low = 0;
        long high = upperBound;
        while (low < high) {
            long middle = low + ((high - low + 1) / 2);
            long delivered = calculateRoute(route, middle).deliveredEnergy();
            if (delivered <= targetAcceptance) {
                low = middle;
            } else {
                high = middle - 1;
            }
        }
        return low;
    }

    private static RouteCalculation calculateRoute(EnergyRoute route, long enteredEnergy) {
        long current = enteredEnergy;
        List<EnergyNetworkHopResult> hops = new ArrayList<>(route.connections().size());
        for (EnergyNetworkConnection connection : route.connections()) {
            long lost = multiplyDivideFloor(
                    current,
                    connection.lossPartsPerMillion(),
                    EnergyConstants.PARTS_PER_MILLION);
            long delivered = current - lost;
            hops.add(new EnergyNetworkHopResult(connection, current, delivered, lost));
            current = delivered;
        }
        return new RouteCalculation(current, List.copyOf(hops));
    }

    private static long multiplyDivideFloor(long value, long multiplier, long divisor) {
        if (value == 0 || multiplier == 0) {
            return 0;
        }
        long quotient = value / divisor;
        long remainder = value % divisor;
        return Math.addExact(
                Math.multiplyExact(quotient, multiplier),
                (remainder * multiplier) / divisor);
    }

    private record RouteCalculation(long deliveredEnergy, List<EnergyNetworkHopResult> hops) {
    }
}
