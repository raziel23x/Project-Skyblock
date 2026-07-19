package raziel23x.projectskyblock.simulation.energy;

/** Derived diagnostics for a completed energy transfer. */
public record EnergyDiagnostics(
        EnergyFlowResult flow,
        boolean sourceLimited,
        boolean targetLimited,
        boolean throughputLimited) {

    public EnergyDiagnostics {
        java.util.Objects.requireNonNull(flow, "flow");
    }

    public long requestedEnergy() {
        return flow.transfer().requestedEnergy();
    }

    public long deliveredEnergy() {
        return flow.transfer().deliveredEnergy();
    }

    public long lostEnergy() {
        return flow.transfer().lostEnergy();
    }
}
