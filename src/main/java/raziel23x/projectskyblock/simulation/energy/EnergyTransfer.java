package raziel23x.projectskyblock.simulation.energy;

/** Immutable accounting for one energy transfer. */
public record EnergyTransfer(
        long requestedEnergy,
        long extractedEnergy,
        long deliveredEnergy,
        long lostEnergy) {

    public EnergyTransfer {
        if (requestedEnergy < 0 || extractedEnergy < 0 || deliveredEnergy < 0 || lostEnergy < 0) {
            throw new IllegalArgumentException("energy accounting values must be non-negative");
        }
        if (extractedEnergy > requestedEnergy) {
            throw new IllegalArgumentException("extracted energy cannot exceed requested energy");
        }
        if (Math.addExact(deliveredEnergy, lostEnergy) != extractedEnergy) {
            throw new IllegalArgumentException("delivered energy plus loss must equal extracted energy");
        }
    }

    public boolean movedEnergy() {
        return extractedEnergy > 0;
    }
}
