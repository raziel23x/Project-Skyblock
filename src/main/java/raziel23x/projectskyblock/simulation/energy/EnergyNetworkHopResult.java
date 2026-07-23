package raziel23x.projectskyblock.simulation.energy;

import java.util.Objects;

/** Immutable accounting for energy crossing one network connection. */
public record EnergyNetworkHopResult(
        EnergyNetworkConnection connection,
        long enteredEnergy,
        long deliveredEnergy,
        long lostEnergy) {

    public EnergyNetworkHopResult {
        Objects.requireNonNull(connection, "connection");
        if (enteredEnergy < 0 || deliveredEnergy < 0 || lostEnergy < 0) {
            throw new IllegalArgumentException("hop energy values must be non-negative");
        }
        if (Math.addExact(deliveredEnergy, lostEnergy) != enteredEnergy) {
            throw new IllegalArgumentException("delivered energy plus loss must equal entered energy");
        }
        if (enteredEnergy > connection.maximumTransferPerOperation()) {
            throw new IllegalArgumentException("entered energy exceeds the connection transfer limit");
        }
    }
}
