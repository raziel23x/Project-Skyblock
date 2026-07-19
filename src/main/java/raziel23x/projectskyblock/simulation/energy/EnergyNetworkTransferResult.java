package raziel23x.projectskyblock.simulation.energy;

import java.util.List;
import java.util.Objects;

/** Complete deterministic accounting for one source-to-target network transfer. */
public record EnergyNetworkTransferResult(
        EnergyRoute route,
        EnergyTransfer transfer,
        List<EnergyNetworkHopResult> hops) {

    public EnergyNetworkTransferResult {
        Objects.requireNonNull(route, "route");
        Objects.requireNonNull(transfer, "transfer");
        hops = List.copyOf(hops);
        if (route.connections().size() != hops.size()) {
            throw new IllegalArgumentException("one hop result is required for each route connection");
        }
        for (int index = 0; index < hops.size(); index++) {
            if (!route.connections().get(index).equals(hops.get(index).connection())) {
                throw new IllegalArgumentException("hop results must follow route connection order");
            }
        }
        long hopLoss = 0;
        for (EnergyNetworkHopResult hop : hops) {
            hopLoss = Math.addExact(hopLoss, hop.lostEnergy());
        }
        if (hopLoss != transfer.lostEnergy()) {
            throw new IllegalArgumentException("hop losses must equal total transfer loss");
        }
    }
}
