package raziel23x.projectskyblock.blockentity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import raziel23x.projectskyblock.blockentity.EnergyCableNetworkManager.CableNetwork;
import raziel23x.projectskyblock.blockentity.EnergyCableNetworkManager.EnergyEndpoint;
import raziel23x.projectskyblock.config.MachineConfig;
import raziel23x.projectskyblock.registry.ModBlockEntities;

public final class BasicEnergyCableBlockEntity extends BlockEntity {
    public BasicEnergyCableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASIC_ENERGY_CABLE.get(), pos, state);
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            BasicEnergyCableBlockEntity cable) {
        CableNetwork network = EnergyCableNetworkManager.getNetwork(level, pos);
        if (!pos.equals(network.controller())) {
            return;
        }

        int remaining = Math.max(0, MachineConfig.BASIC_ENERGY_CABLE_TRANSFER_RATE.get());
        if (remaining <= 0 || network.endpoints().isEmpty()) {
            return;
        }

        List<EnergyEndpoint> sources = new ArrayList<>();
        List<EnergyEndpoint> receivers = new ArrayList<>();
        for (EnergyEndpoint endpoint : network.endpoints()) {
            if (endpoint.storage().canExtract()) {
                sources.add(endpoint);
            }
            if (endpoint.storage().canReceive()) {
                receivers.add(endpoint);
            }
        }

        if (sources.isEmpty() || receivers.isEmpty()) {
            return;
        }

        for (EnergyEndpoint source : sources) {
            if (remaining <= 0) {
                break;
            }

            for (EnergyEndpoint receiver : receivers) {
                if (remaining <= 0) {
                    break;
                }
                if (source.sameTarget(receiver)) {
                    continue;
                }

                int available = source.storage().extractEnergy(remaining, true);
                if (available <= 0) {
                    break;
                }

                int accepted = receiver.storage().receiveEnergy(available, true);
                if (accepted <= 0) {
                    continue;
                }

                int extracted = source.storage().extractEnergy(accepted, false);
                if (extracted <= 0) {
                    break;
                }

                int received = receiver.storage().receiveEnergy(extracted, false);
                remaining -= received;

                // FE implementations are expected to execute the amount accepted
                // during simulation. Cables deliberately have no internal buffer,
                // so stop this source/receiver pair if an endpoint violates that
                // contract rather than risking duplicate transfers.
                if (received < extracted) {
                    break;
                }
            }
        }
    }
}
