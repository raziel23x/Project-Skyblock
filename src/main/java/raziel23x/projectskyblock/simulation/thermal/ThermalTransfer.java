package raziel23x.projectskyblock.simulation.thermal;

import java.math.BigInteger;

/** Deterministic fixed-point heat transfer between two thermal bodies. */
public final class ThermalTransfer {
    private ThermalTransfer() {
    }

    /**
     * Transfers heat without allowing the two bodies to cross past equilibrium.
     *
     * @return microjoules transferred from the hotter body to the colder body
     */
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
        long absoluteDifference = safeAbsolute(difference);
        long requested = saturatedMultiply(absoluteDifference, conductanceMicroJoulesPerMilliKelvinPerTick);
        long equilibriumLimit = energyToEquilibrium(hot, cold, absoluteDifference);
        long transfer = Math.min(Math.min(requested, maximumTransferMicroJoules), equilibriumLimit);
        if (transfer <= 0) {
            return 0;
        }

        long removed = hot.removeHeatMicroJoules(transfer);
        return cold.addHeatMicroJoules(removed);
    }

    private static long energyToEquilibrium(ThermalState hot, ThermalState cold, long temperatureDifference) {
        BigInteger hotCapacity = BigInteger.valueOf(hot.heatCapacityMicroJoulesPerMilliKelvin());
        BigInteger coldCapacity = BigInteger.valueOf(cold.heatCapacityMicroJoulesPerMilliKelvin());
        BigInteger numerator = BigInteger.valueOf(temperatureDifference)
                .multiply(hotCapacity)
                .multiply(coldCapacity);
        BigInteger denominator = hotCapacity.add(coldCapacity);
        BigInteger result = numerator.divide(denominator);
        BigInteger maximum = BigInteger.valueOf(Long.MAX_VALUE);
        return result.compareTo(maximum) > 0 ? Long.MAX_VALUE : result.longValue();
    }

    static long saturatedMultiply(long left, long right) {
        try {
            return Math.multiplyExact(left, right);
        } catch (ArithmeticException ignored) {
            return Long.MAX_VALUE;
        }
    }

    private static long safeAbsolute(long value) {
        return value == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(value);
    }
}
