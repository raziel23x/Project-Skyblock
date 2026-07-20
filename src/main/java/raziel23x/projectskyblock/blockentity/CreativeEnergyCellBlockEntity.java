package raziel23x.projectskyblock.blockentity;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import raziel23x.projectskyblock.platform.neoforge.machine.EngineMachineBlockEntity;
import raziel23x.projectskyblock.platform.neoforge.machine.capability.EngineEnergyStorageAdapter;
import raziel23x.projectskyblock.registry.ModBlockEntities;
import raziel23x.projectskyblock.simulation.core.SimulationScheduler;
import raziel23x.projectskyblock.simulation.energy.EnergyLimits;
import raziel23x.projectskyblock.simulation.energy.SimulationEnergyState;
import raziel23x.projectskyblock.simulation.machine.MachineId;
import raziel23x.projectskyblock.simulation.machine.component.MachineEnergyAccess;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventoryAccess;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventorySlotDefinition;
import raziel23x.projectskyblock.simulation.machine.component.MachineInventorySlotRule;
import raziel23x.projectskyblock.simulation.machine.component.MachineThermalAccess;
import raziel23x.projectskyblock.simulation.machine.logic.CreativeEnergyCellLogic;
import raziel23x.projectskyblock.simulation.machine.runtime.MachineRuntime;
import raziel23x.projectskyblock.simulation.thermal.ThermalProperties;
import raziel23x.projectskyblock.simulation.thermal.ThermalState;

/** First production block entity whose authoritative behavior is owned by the engine. */
public final class CreativeEnergyCellBlockEntity extends EngineMachineBlockEntity {
    private static final long CAPACITY = 6_000_000L;
    private static final long TRANSFER_PER_SIDE = 1_000_000L;
    private static final long AMBIENT_TEMPERATURE_MK = 293_150L;

    private final IEnergyStorage energyCapability =
            new EngineEnergyStorageAdapter(() -> machineRuntime().components().energy());

    public CreativeEnergyCellBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE_ENERGY_CELL.get(), pos, state);
    }

    @Override
    protected MachineRuntime createMachineRuntime(SimulationScheduler scheduler) {
        String dimension = level instanceof ServerLevel serverLevel
                ? serverLevel.dimension().location().toString()
                : "unbound";
        MachineId id = new MachineId("creative_energy_cell@" + dimension + ":" + worldPosition.asLong());
        return new MachineRuntime(
                scheduler,
                id,
                new SimulationEnergyState(
                        new EnergyLimits(CAPACITY, CAPACITY, TRANSFER_PER_SIDE * Direction.values().length)),
                MachineEnergyAccess.OUTPUT,
                List.of(new MachineInventorySlotDefinition(
                        1L,
                        MachineInventoryAccess.NONE,
                        MachineInventorySlotRule.ACCEPT_ALL)),
                new ThermalState(AMBIENT_TEMPERATURE_MK, 1L),
                new ThermalProperties(0L, 0L, Long.MAX_VALUE, Long.MAX_VALUE),
                MachineThermalAccess.NONE,
                CreativeEnergyCellLogic.INSTANCE);
    }

    @Override
    protected void afterMachineExecution() {
        if (!(level instanceof ServerLevel serverLevel) || !hasMachineRuntime()) {
            return;
        }
        var energy = machineRuntime().components().energy();
        for (Direction direction : Direction.values()) {
            if (energy.storedEnergy() <= 0L) {
                break;
            }
            IEnergyStorage receiver = serverLevel.getCapability(
                    Capabilities.EnergyStorage.BLOCK,
                    worldPosition.relative(direction),
                    direction.getOpposite());
            if (receiver == null || !receiver.canReceive()) {
                continue;
            }
            int offered = (int) Math.min(TRANSFER_PER_SIDE, energy.storedEnergy());
            int accepted = receiver.receiveEnergy(offered, true);
            if (accepted > 0) {
                int transferred = receiver.receiveEnergy(accepted, false);
                energy.extract(transferred);
            }
        }
    }

    public IEnergyStorage getEnergyStorage() {
        return hasMachineRuntime() ? energyCapability : null;
    }

    public void requestNeighborRecheck() {
        if (hasMachineRuntime()) {
            machineRuntime().requestWork();
        }
    }
}
