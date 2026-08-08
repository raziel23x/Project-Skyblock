package raziel23x.projectskyblock.platform.neoforge.machine.capability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import raziel23x.projectskyblock.simulation.core.DirtyStateTracker;
import raziel23x.projectskyblock.simulation.energy.EnergyLimits;
import raziel23x.projectskyblock.simulation.energy.SimulationEnergyState;
import raziel23x.projectskyblock.simulation.machine.component.MachineEnergyAccess;
import raziel23x.projectskyblock.simulation.machine.component.MachineEnergyComponent;

class EngineEnergyStorageAdapterTest {
    @Test
    void simulationUsesDisposableCandidateAndExecutionCommitsFreshCandidate() {
        AtomicInteger wakes = new AtomicInteger();
        DirtyStateTracker dirtyState = new DirtyStateTracker();
        MachineEnergyComponent energy = new MachineEnergyComponent(
                new SimulationEnergyState(new EnergyLimits(1_000L, 300L, 200L)),
                MachineEnergyAccess.BIDIRECTIONAL,
                dirtyState,
                wakes::incrementAndGet);
        EngineEnergyStorageAdapter adapter = new EngineEnergyStorageAdapter(() -> energy);

        assertEquals(250, adapter.receiveEnergy(250, true));
        assertEquals(0L, energy.storedEnergy());
        assertEquals(0L, energy.stateVersion());
        assertEquals(0, wakes.get());
        assertTrue(dirtyState.isClean());

        assertEquals(250, adapter.receiveEnergy(250, false));
        assertEquals(250L, energy.storedEnergy());
        assertEquals(1L, energy.stateVersion());
        assertEquals(1, wakes.get());

        dirtyState.clearAll();
        assertEquals(150, adapter.extractEnergy(150, true));
        assertEquals(250L, energy.storedEnergy());
        assertEquals(1L, energy.stateVersion());
        assertEquals(1, wakes.get());
        assertTrue(dirtyState.isClean());

        assertEquals(150, adapter.extractEnergy(150, false));
        assertEquals(100L, energy.storedEnergy());
        assertEquals(2L, energy.stateVersion());
        assertEquals(2, wakes.get());
    }

    @Test
    void capabilityAccessReflectsMachineEndpointAccess() {
        MachineEnergyComponent energy = new MachineEnergyComponent(
                EnergyLimits.unlimitedThroughput(1_000L),
                MachineEnergyAccess.INPUT);
        EngineEnergyStorageAdapter adapter = new EngineEnergyStorageAdapter(() -> energy);

        assertTrue(adapter.canReceive());
        assertFalse(adapter.canExtract());
        assertEquals(0, adapter.extractEnergy(10, false));

        energy.setAccess(MachineEnergyAccess.OUTPUT);

        assertFalse(adapter.canReceive());
        assertTrue(adapter.canExtract());
        assertEquals(0, adapter.receiveEnergy(10, false));
    }
}
