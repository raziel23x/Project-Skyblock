package raziel23x.projectskyblock.simulation.machine.logic;

import raziel23x.projectskyblock.simulation.core.SimulationBudget;
import raziel23x.projectskyblock.simulation.core.SimulationContext;
import raziel23x.projectskyblock.simulation.core.SimulationResult;
import raziel23x.projectskyblock.simulation.machine.MachineLogic;
import raziel23x.projectskyblock.simulation.machine.MachineState;
import raziel23x.projectskyblock.simulation.machine.runtime.MachineComponentState;

/** Backend-owned logic for the creative energy testing source. */
public final class CreativeEnergyCellLogic implements MachineLogic<MachineComponentState> {
    public static final CreativeEnergyCellLogic INSTANCE = new CreativeEnergyCellLogic();

    private CreativeEnergyCellLogic() {
    }

    @Override
    public SimulationResult execute(
            MachineState<MachineComponentState> state,
            SimulationContext context,
            SimulationBudget budget) {
        if (!budget.tryConsume(1)) {
            return SimulationResult.blocked("simulation budget exhausted");
        }

        var energy = state.data().energy();
        long missing = energy.availableCapacity();
        if (missing > 0L) {
            energy.produce(missing);
        }
        return SimulationResult.sleep();
    }
}
