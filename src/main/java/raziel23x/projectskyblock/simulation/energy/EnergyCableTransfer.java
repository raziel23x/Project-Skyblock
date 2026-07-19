package raziel23x.projectskyblock.simulation.energy;

/** Electrical transfer with deterministic resistive loss converted into cable heat. */
public final class EnergyCableTransfer {
    private static final long PARTS_PER_MILLION = 1_000_000L;

    private EnergyCableTransfer() {
    }

    public static TransferResult transfer(
            EnergyCableState cable,
            long requestedEnergy,
            long heatMicroJoulesPerLostEnergyUnit) {
        if (requestedEnergy < 0 || heatMicroJoulesPerLostEnergyUnit < 0) {
            throw new IllegalArgumentException("requested energy and heat conversion must be non-negative");
        }

        long boundedRequest = Math.min(requestedEnergy, cable.maximumTransferPerTick());
        long extracted = cable.extract(boundedRequest);
        long lost = multiplyDivideFloor(extracted, cable.lossPartsPerMillion(), PARTS_PER_MILLION);
        long delivered = extracted - lost;
        long generatedHeat = saturatedMultiply(lost, heatMicroJoulesPerLostEnergyUnit);
        long acceptedHeat = cable.thermalState().addHeatMicroJoules(generatedHeat);
        return new TransferResult(extracted, delivered, lost, acceptedHeat);
    }

    private static long multiplyDivideFloor(long value, long multiplier, long divisor) {
        if (value == 0 || multiplier == 0) return 0;
        long quotient = value / divisor;
        long remainder = value % divisor;
        return quotient * multiplier + (remainder * multiplier) / divisor;
    }

    private static long saturatedMultiply(long left, long right) {
        try {
            return Math.multiplyExact(left, right);
        } catch (ArithmeticException ignored) {
            return Long.MAX_VALUE;
        }
    }

    public record TransferResult(
            long extractedEnergy,
            long deliveredEnergy,
            long lostEnergy,
            long generatedHeatMicroJoules) {
    }
}
