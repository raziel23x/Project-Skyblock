package raziel23x.projectskyblock.simulation.energy;

/** Immutable request to move energy with a fixed-point transfer efficiency. */
public record EnergyRequest(long requestedEnergy, int efficiencyPartsPerMillion) {
    public EnergyRequest {
        if (requestedEnergy < 0) {
            throw new IllegalArgumentException("requested energy must be non-negative");
        }
        if (efficiencyPartsPerMillion < 0
                || efficiencyPartsPerMillion > EnergyConstants.PARTS_PER_MILLION) {
            throw new IllegalArgumentException("efficiency must be between 0 and 1,000,000 ppm");
        }
    }

    public static EnergyRequest lossless(long requestedEnergy) {
        return new EnergyRequest(requestedEnergy, EnergyConstants.PERFECT_EFFICIENCY_PPM);
    }
}
