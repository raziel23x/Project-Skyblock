package raziel23x.projectskyblock.simulation.core;

/** Mutable work budget supplied to one bounded simulation execution. */
public final class SimulationBudget {
    private int remainingUnits;

    public SimulationBudget(int units) {
        if (units < 0) {
            throw new IllegalArgumentException("units must be non-negative");
        }
        remainingUnits = units;
    }

    public boolean tryConsume(int units) {
        if (units <= 0) {
            throw new IllegalArgumentException("units must be positive");
        }
        if (units > remainingUnits) {
            return false;
        }
        remainingUnits -= units;
        return true;
    }

    public int remainingUnits() {
        return remainingUnits;
    }
}
