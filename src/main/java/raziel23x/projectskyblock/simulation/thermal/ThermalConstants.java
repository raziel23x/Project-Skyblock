package raziel23x.projectskyblock.simulation.thermal;

/** Shared fixed-point temperatures used by the authoritative thermal simulation. */
public final class ThermalConstants {
    public static final long ABSOLUTE_ZERO_MILLI_KELVIN = 0L;
    public static final long ZERO_CELSIUS_MILLI_KELVIN = 273_150L;
    public static final long STANDARD_AMBIENT_MILLI_KELVIN = 293_150L;

    private ThermalConstants() {
    }

    public static long celsiusToMilliKelvin(long celsius) {
        return Math.addExact(ZERO_CELSIUS_MILLI_KELVIN, Math.multiplyExact(celsius, 1_000L));
    }
}
