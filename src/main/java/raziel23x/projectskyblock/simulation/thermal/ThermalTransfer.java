package raziel23x.projectskyblock.simulation.thermal;

/** Deterministic fixed-point heat transfer between two thermal bodies. */
public final class ThermalTransfer {
    private ThermalTransfer() {
    }

    public static long transfer(
            ThermalState first,
            ThermalState second,
            long conductanceMicroJoulesPerMilliKelvinPerTick,
            long maximumTransferMicroJoules) {
        if (conductanceMicroJoulesPerMilliKelvinPerTick < 0) {
            throw new IllegalArgumentException("conductance must be non-negative");
        }
        if (maximumTransferMicroJoules < 0) {
            throw new IllegalArgumentException("maximum transfer must be non-negative");
        }

        long difference = first.temperatureMilliKelvin() - second.temperatureMilliKelvin();
        if (difference == 0 || conductanceMicroJoulesPerMilliKelvinPerTick == 0 || maximumTransferMicroJoules == 0) {
            return 0;
        }

        ThermalState hot = difference > 0 ? first : second;
        ThermalState cold = difference > 0 ? second : first;
        long absoluteDifference = Math.abs(difference);
        long requested = saturatedMultiply(absoluteDifference, conductanceMicroJoulesPerMilliKelvinPerTick);
        requested = Math.min(requested, maximumTransferMicroJoules);

        long removed = hot.removeHeatMicroJoules(requested);
        long accepted = cold.addHeatMicroJoules(removed);
        if (accepted < removed) {
            hot.addHeatMicroJoules(removed - accepted);
        }
        return accepted;
    }

    private static long saturatedMultiply(long left, long right) {
        try {
            return Math.multiplyExact(left, right);
        } catch (ArithmeticException ignored) {
            return Long.MAX_VALUE;
        }
    }
}
