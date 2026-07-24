package raziel23x.projectskyblock.platform.neoforge.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.simulation.core.DirtyFlag;
import raziel23x.projectskyblock.simulation.core.SimulationScheduler;
import raziel23x.projectskyblock.simulation.machine.persistence.MachineRuntimePersistence;
import raziel23x.projectskyblock.simulation.machine.persistence.MachineRuntimeSnapshot;
import raziel23x.projectskyblock.simulation.machine.runtime.MachineRuntime;

/**
 * Reusable lifecycle and persistence bridge between a Minecraft block entity and one
 * backend-owned {@link MachineRuntime}.
 *
 * <p>This class deliberately does not tick the simulation. A level-scoped scheduler driver
 * owns that responsibility so many machines never create many independent tick loops.</p>
 */
public abstract class EngineMachineBlockEntity extends BlockEntity {
    private MachineRuntime runtime;
    private MachineRuntimeSnapshot pendingSnapshot;
    private EngineMachineLifecycle lifecycle = EngineMachineLifecycle.CONSTRUCTED;
    private boolean persistenceFailureReported;

    protected EngineMachineBlockEntity(
            BlockEntityType<?> type,
            BlockPos position,
            BlockState blockState) {
        super(type, position, blockState);
    }

    /** Creates and registers the backend runtime against the level-owned scheduler. */
    protected abstract MachineRuntime createMachineRuntime(SimulationScheduler scheduler);

    /**
     * Optional platform-specific migration hook for pre-engine block-entity data.
     * Returning {@code null} means the tag does not contain a supported legacy state.
     */
    protected MachineRuntimeSnapshot readLegacyMachineSnapshot(
            CompoundTag tag,
            HolderLookup.Provider registries) {
        return null;
    }

    /** Invoked only after a decoded or migrated snapshot has been restored successfully. */
    protected void afterMachineSnapshotRestored(MachineRuntimeSnapshot snapshot) {
    }

    public final EngineMachineLifecycle engineLifecycle() {
        return lifecycle;
    }

    public final boolean hasMachineRuntime() {
        return runtime != null;
    }

    public final MachineRuntime machineRuntime() {
        if (runtime == null) {
            throw new IllegalStateException("machine runtime is not active");
        }
        return runtime;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lifecycle = EngineMachineLifecycle.LOADED;
        if (!(level instanceof ServerLevel serverLevel) || runtime != null) {
            return;
        }
        runtime = createMachineRuntime(EngineMachineLevelManager.schedulerFor(serverLevel));
        if (pendingSnapshot != null) {
            restoreSnapshotSafely(pendingSnapshot, "load");
            pendingSnapshot = null;
        }
        EngineMachineLevelManager.registerMachine(serverLevel, this);
        runtime.requestWork();
        lifecycle = EngineMachineLifecycle.ACTIVE;
    }

    /**
     * Applies deferred platform side effects after the level scheduler has executed.
     * The level-scoped driver calls this only for machines that executed and became dirty.
     */
    public final void flushMachineIntegrationWork() {
        if (runtime == null) {
            return;
        }
        afterMachineExecution();
        if (runtime.dirtyState().isDirty(DirtyFlag.PERSISTENCE)) {
            setChanged();
            runtime.dirtyState().clear(DirtyFlag.PERSISTENCE);
        }
        if (runtime.dirtyState().isDirty(DirtyFlag.CLIENT_SYNC) && level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            runtime.dirtyState().clear(DirtyFlag.CLIENT_SYNC);
        }
        runtime.dirtyState().clear(DirtyFlag.SCHEDULER);
    }

    /** Platform-side integration hook invoked only after this machine actually executes. */
    protected void afterMachineExecution() {
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        MachineRuntimeSnapshot snapshot = runtime == null
                ? pendingSnapshot
                : MachineRuntimePersistence.capture(runtime);
        if (snapshot != null) {
            MachineRuntimeNbtCodec.write(tag, snapshot);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        try {
            MachineRuntimeSnapshot snapshot = tag.contains(MachineRuntimeNbtCodec.ROOT_KEY)
                    ? MachineRuntimeNbtCodec.read(tag)
                    : readLegacyMachineSnapshot(tag, registries);
            if (snapshot == null) {
                return;
            }
            if (runtime == null) {
                pendingSnapshot = snapshot;
            } else {
                restoreSnapshotSafely(snapshot, "reload");
            }
        } catch (RuntimeException failure) {
            reportPersistenceFailure("decode", failure);
            pendingSnapshot = null;
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setRemoved() {
        closeRuntime();
        lifecycle = EngineMachineLifecycle.REMOVED;
        super.setRemoved();
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        if (lifecycle == EngineMachineLifecycle.REMOVED) {
            lifecycle = EngineMachineLifecycle.UNLOADED;
        }
    }

    private void restoreSnapshotSafely(MachineRuntimeSnapshot snapshot, String phase) {
        try {
            MachineRuntimePersistence.restore(machineRuntime(), snapshot);
            afterMachineSnapshotRestored(snapshot);
        } catch (RuntimeException failure) {
            reportPersistenceFailure(phase, failure);
        }
    }

    private void reportPersistenceFailure(String phase, RuntimeException failure) {
        if (persistenceFailureReported) {
            return;
        }
        persistenceFailureReported = true;
        ProjectSkyblock.LOGGER.error(
                "Failed to {} engine runtime for {} at {}; retaining safe runtime state",
                phase,
                getType(),
                worldPosition,
                failure);
    }

    private void closeRuntime() {
        if (runtime != null) {
            if (level instanceof ServerLevel serverLevel) {
                EngineMachineLevelManager.unregisterMachine(serverLevel, this);
            }
            runtime.close();
            runtime = null;
        }
    }
}
