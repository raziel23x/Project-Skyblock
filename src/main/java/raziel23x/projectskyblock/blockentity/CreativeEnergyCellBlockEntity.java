package raziel23x.projectskyblock.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import raziel23x.projectskyblock.registry.ModBlockEntities;

public final class CreativeEnergyCellBlockEntity extends BlockEntity {
    private static final int TRANSFER_PER_TICK = 1_000_000;

    private final IEnergyStorage infiniteEnergy = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return Math.max(0, maxExtract);
        }

        @Override
        public int getEnergyStored() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int getMaxEnergyStored() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    };

    public CreativeEnergyCellBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE_ENERGY_CELL.get(), pos, state);
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            CreativeEnergyCellBlockEntity cell) {
        for (Direction direction : Direction.values()) {
            IEnergyStorage receiver = level.getCapability(
                    Capabilities.EnergyStorage.BLOCK,
                    pos.relative(direction),
                    direction.getOpposite());
            if (receiver != null && receiver.canReceive()) {
                receiver.receiveEnergy(TRANSFER_PER_TICK, false);
            }
        }
    }

    public IEnergyStorage getEnergyStorage() {
        return infiniteEnergy;
    }
}
