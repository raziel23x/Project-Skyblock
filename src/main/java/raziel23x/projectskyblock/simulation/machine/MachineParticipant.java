package raziel23x.projectskyblock.simulation.machine;

import java.util.Objects;
import raziel23x.projectskyblock.simulation.core.SimulationBudget;
import raziel23x.projectskyblock.simulation.core.SimulationContext;
import raziel23x.projectskyblock.simulation.core.SimulationParticipant;
import raziel23x.projectskyblock.simulation.core.SimulationResult;
import raziel23x.projectskyblock.simulation.core.SimulationState;

/**
 * Reusable scheduler adapter for typed machine state and machine-specific logic.
 *
 * <p>This class owns only machine lifecycle bookkeeping. Resource components and
 * processing rules remain separate backend objects supplied through the typed state.</p>
 */
public final class MachineParticipant<S extends SimulationState>
        implements SimulationParticipant<MachineState<S>> {
    private final MachineState<S> state;
    private final MachineLogic<S> logic;

    public MachineParticipant(MachineState<S> state, MachineLogic<S> logic) {
        this.state = Objects.requireNonNull(state, "state");
        this.logic = Objects.requireNonNull(logic, "logic");
    }

    @Override
    public MachineState<S> state() {
        return state;
    }

    public void requestWork() {
        state.requestWork();
    }

    public void setEnabled(boolean enabled) {
        state.setEnabled(enabled);
    }

    public MachineDiagnostics diagnostics() {
        return MachineDiagnostics.capture(state);
    }

    @Override
    public SimulationResult execute(SimulationContext context, SimulationBudget budget) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(budget, "budget");
        if (!state.enabled()) {
            state.becomeSleeping();
            return SimulationResult.sleep();
        }
        if (state.activity() == MachineActivity.INVALID) {
            return SimulationResult.invalid(state.statusReason());
        }

        state.beginExecution(context.gameTime());
        SimulationResult result = Objects.requireNonNull(
                logic.execute(state, context, budget),
                "machine logic returned null");
        applyResult(result);
        return result;
    }

    private void applyResult(SimulationResult result) {
        if (result instanceof SimulationResult.Sleep) {
            state.becomeSleeping();
        } else if (result instanceof SimulationResult.ContinueNextTick
                || result instanceof SimulationResult.ScheduleAt) {
            state.becomeRunning();
        } else if (result instanceof SimulationResult.Blocked blocked) {
            state.becomeBlocked(blocked.reason());
        } else if (result instanceof SimulationResult.Invalid invalid) {
            state.becomeInvalid(invalid.reason());
        } else {
            throw new IllegalStateException("unhandled simulation result: " + result.getClass().getName());
        }
    }
}
