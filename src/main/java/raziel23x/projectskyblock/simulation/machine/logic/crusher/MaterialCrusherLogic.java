package raziel23x.projectskyblock.simulation.machine.logic.crusher;

import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.Optional;
import raziel23x.projectskyblock.simulation.core.SimulationBudget;
import raziel23x.projectskyblock.simulation.core.SimulationContext;
import raziel23x.projectskyblock.simulation.core.SimulationResult;
import raziel23x.projectskyblock.simulation.inventory.SimulationItemStack;
import raziel23x.projectskyblock.simulation.machine.MachineLogic;
import raziel23x.projectskyblock.simulation.machine.MachineState;
import raziel23x.projectskyblock.simulation.machine.component.MachineCombustionComponent;
import raziel23x.projectskyblock.simulation.machine.component.MachineEnergyComponent;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventoryComponent;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventoryTransaction;
import raziel23x.projectskyblock.simulation.machine.component.MachineProcessingComponent;
import raziel23x.projectskyblock.simulation.machine.runtime.MachineComponentState;

/**
 * Minecraft-independent behavior for the prototype Material Crusher.
 *
 * <p>The engine owns inventory, energy, combustion, and processing state. Recipe lookup, random
 * result rolling, and furnace-fuel interpretation enter only through narrow host ports. The logic
 * intentionally preserves the prototype's legacy behavior: invalid input or blocked maximum output
 * resets progress, missing power preserves progress, FE/fuel preference remains configurable, and
 * completion consumes input and inserts both outputs as one optimistic inventory transaction.</p>
 */
public final class MaterialCrusherLogic implements MachineLogic<MachineComponentState> {
    public static final int INPUT_SLOT = 0;
    public static final int FUEL_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    public static final int BYPRODUCT_SLOT = 3;
    public static final int SLOT_COUNT = 4;
    public static final String LEGACY_PROCESS_ID = "projectskyblock:legacy_material_crusher";

    private static final String BLOCKED_OUTPUT = "material crusher output blocked";
    private static final String BLOCKED_POWER = "material crusher has no usable power";
    private static final String BLOCKED_INVENTORY_CONFLICT = "material crusher inventory changed";

    private final MaterialCrusherRecipePort recipePort;
    private final MaterialCrusherFuelPort fuelPort;
    private final MaterialCrusherSettingsSource settingsSource;
    private boolean working;
    private MaterialCrusherPowerSource activePowerSource = MaterialCrusherPowerSource.NONE;

    public MaterialCrusherLogic(
            MaterialCrusherRecipePort recipePort,
            MaterialCrusherFuelPort fuelPort,
            MaterialCrusherSettingsSource settingsSource) {
        this.recipePort = Objects.requireNonNull(recipePort, "recipePort");
        this.fuelPort = Objects.requireNonNull(fuelPort, "fuelPort");
        this.settingsSource = Objects.requireNonNull(settingsSource, "settingsSource");
    }

    @Override
    public SimulationResult execute(
            MachineState<MachineComponentState> state,
            SimulationContext context,
            SimulationBudget budget) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(budget, "budget");
        if (!budget.tryConsume(1)) {
            setIdlePresentation();
            return SimulationResult.blocked("simulation budget exhausted");
        }

        MaterialCrusherSettings settings = Objects.requireNonNull(
                settingsSource.currentSettings(),
                "settings source returned null");
        MachineComponentState components = state.data();
        MachineInventoryComponent inventory = components.inventory();
        MachineProcessingComponent processing = components.processing();
        SimulationItemStack input = inventory.stack(INPUT_SLOT);

        Optional<MaterialCrusherRecipeMatch> match = input.isEmpty()
                ? Optional.empty()
                : recipePort.findMatch(input);
        if (match.isEmpty()) {
            processing.cancel();
            setIdlePresentation();
            return SimulationResult.sleep();
        }

        MaterialCrusherRecipeMatch recipe = match.orElseThrow();
        if (!canAcceptMaximum(inventory, recipe.maximumResult())) {
            processing.cancel();
            setIdlePresentation();
            return SimulationResult.blocked(BLOCKED_OUTPUT);
        }

        adoptOrStartProcess(processing, recipe.recipeId(), settings.processTimeUnits());
        MaterialCrusherPowerSource source = consumePower(components, settings);
        if (source == MaterialCrusherPowerSource.NONE) {
            if (!processing.blocked() || !BLOCKED_POWER.equals(processing.blockedReason())) {
                processing.block(BLOCKED_POWER);
            }
            setIdlePresentation();
            return SimulationResult.blocked(BLOCKED_POWER);
        }

        processing.resume();
        working = true;
        activePowerSource = source;
        processing.advance(1L);

        if (!processing.readyToComplete()) {
            return SimulationResult.continueNextTick();
        }

        Optional<MaterialCrusherProcessResult> rolled = recipePort.rollResult(
                recipe.recipeId(),
                inventory.stack(INPUT_SLOT));
        if (rolled.isEmpty() || rolled.orElseThrow().isEmpty()) {
            processing.cancel();
            setIdlePresentation();
            return SimulationResult.sleep();
        }

        if (!commitCompletion(inventory, rolled.orElseThrow())) {
            processing.block(BLOCKED_INVENTORY_CONFLICT);
            setIdlePresentation();
            return SimulationResult.blocked(BLOCKED_INVENTORY_CONFLICT);
        }

        processing.complete();
        return SimulationResult.continueNextTick();
    }

    public boolean working() {
        return working;
    }

    public MaterialCrusherPowerSource activePowerSource() {
        return activePowerSource;
    }

    /** Restores only legacy presentation until the first authoritative execution refreshes it. */
    public void restorePresentation(
            boolean working,
            MaterialCrusherPowerSource activePowerSource) {
        this.working = working;
        this.activePowerSource = Objects.requireNonNull(activePowerSource, "activePowerSource");
    }

    private void adoptOrStartProcess(
            MachineProcessingComponent processing,
            String recipeId,
            long processTimeUnits) {
        if (processing.idle()) {
            processing.start(recipeId, processTimeUnits);
            return;
        }
        if (processing.processId().equals(recipeId)) {
            return;
        }

        long preservedUnits = processing.processId().equals(LEGACY_PROCESS_ID)
                ? Math.min(processing.completedUnits(), processTimeUnits)
                : 0L;
        processing.cancel();
        processing.start(recipeId, processTimeUnits);
        if (preservedUnits > 0L) {
            processing.advance(preservedUnits);
        }
    }

    private MaterialCrusherPowerSource consumePower(
            MachineComponentState components,
            MaterialCrusherSettings settings) {
        if (settings.preferFe()) {
            if (tryConsumeEnergy(components.energy(), settings)) {
                return MaterialCrusherPowerSource.FE;
            }
            if (tryConsumeFuel(components, settings)) {
                return MaterialCrusherPowerSource.FUEL;
            }
        } else {
            if (tryConsumeFuel(components, settings)) {
                return MaterialCrusherPowerSource.FUEL;
            }
            if (tryConsumeEnergy(components.energy(), settings)) {
                return MaterialCrusherPowerSource.FE;
            }
        }
        return MaterialCrusherPowerSource.NONE;
    }

    private static boolean tryConsumeEnergy(
            MachineEnergyComponent energy,
            MaterialCrusherSettings settings) {
        if (!settings.fePowerEnabled()) {
            return false;
        }
        long requested = settings.energyPerUnit();
        if (!energy.canConsume(requested)) {
            return false;
        }
        return requested == 0L || energy.consume(requested) == requested;
    }

    private boolean tryConsumeFuel(
            MachineComponentState components,
            MaterialCrusherSettings settings) {
        if (!settings.fuelPowerEnabled()) {
            return false;
        }
        MachineCombustionComponent combustion = components.combustion();
        if (!combustion.burning() && !igniteFuel(components.inventory(), combustion, settings)) {
            return false;
        }
        return combustion.consume(1L) == 1L;
    }

    private boolean igniteFuel(
            MachineInventoryComponent inventory,
            MachineCombustionComponent combustion,
            MaterialCrusherSettings settings) {
        SimulationItemStack fuelStack = inventory.stack(FUEL_SLOT);
        Optional<MaterialCrusherFuel> resolved = fuelStack.isEmpty()
                ? Optional.empty()
                : fuelPort.resolveFuel(fuelStack);
        if (resolved.isEmpty()) {
            return false;
        }

        MaterialCrusherFuel fuel = resolved.orElseThrow();
        long burnUnits = scaledBurnUnits(fuel.baseBurnUnits(), settings.fuelBurnMultiplier());
        try (MachineInventoryTransaction transaction = inventory.beginTransaction()) {
            SimulationItemStack consumed = transaction.consume(FUEL_SLOT, 1L);
            if (consumed.isEmpty()) {
                return false;
            }
            if (!fuel.remainder().isEmpty()
                    && !transaction.store(FUEL_SLOT, fuel.remainder()).isEmpty()) {
                return false;
            }
            transaction.commit();
        } catch (ConcurrentModificationException exception) {
            return false;
        }
        combustion.ignite(burnUnits);
        return true;
    }

    private static long scaledBurnUnits(long baseBurnUnits, double multiplier) {
        double scaled = baseBurnUnits * multiplier;
        if (!Double.isFinite(scaled) || scaled >= Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        }
        return Math.max(1L, Math.round(scaled));
    }

    private static boolean canAcceptMaximum(
            MachineInventoryComponent inventory,
            MaterialCrusherProcessResult result) {
        try (MachineInventoryTransaction transaction = inventory.beginTransaction()) {
            if (!transaction.canConsume(INPUT_SLOT, 1L)) {
                return false;
            }
            if (!result.primary().isEmpty()
                    && !transaction.store(OUTPUT_SLOT, result.primary()).isEmpty()) {
                return false;
            }
            return result.byproduct().isEmpty()
                    || transaction.store(BYPRODUCT_SLOT, result.byproduct()).isEmpty();
        }
    }

    private static boolean commitCompletion(
            MachineInventoryComponent inventory,
            MaterialCrusherProcessResult result) {
        try (MachineInventoryTransaction transaction = inventory.beginTransaction()) {
            if (transaction.consume(INPUT_SLOT, 1L).isEmpty()) {
                return false;
            }
            if (!transaction.store(OUTPUT_SLOT, result.primary()).isEmpty()) {
                return false;
            }
            if (!result.byproduct().isEmpty()
                    && !transaction.store(BYPRODUCT_SLOT, result.byproduct()).isEmpty()) {
                return false;
            }
            transaction.commit();
            return true;
        } catch (ConcurrentModificationException exception) {
            return false;
        }
    }

    private void setIdlePresentation() {
        working = false;
        activePowerSource = MaterialCrusherPowerSource.NONE;
    }
}
