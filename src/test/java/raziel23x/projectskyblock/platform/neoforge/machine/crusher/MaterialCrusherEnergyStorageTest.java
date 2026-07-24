package raziel23x.projectskyblock.platform.neoforge.machine.crusher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import net.neoforged.neoforge.energy.EnergyStorage;
import org.junit.jupiter.api.Test;

class MaterialCrusherEnergyStorageTest {
    @Test
    void dynamicallyGatesExternalFeReceiptWithoutOwningState() {
        EnergyStorage authoritative = new EnergyStorage(1_000);
        AtomicBoolean enabled = new AtomicBoolean(false);
        MaterialCrusherEnergyStorage adapter = new MaterialCrusherEnergyStorage(
                authoritative,
                enabled::get);

        assertFalse(adapter.canReceive());
        assertEquals(0, adapter.receiveEnergy(250, false));
        assertEquals(0, authoritative.getEnergyStored());

        enabled.set(true);
        assertTrue(adapter.canReceive());
        assertEquals(250, adapter.receiveEnergy(250, false));
        assertEquals(250, authoritative.getEnergyStored());
        assertEquals(250, adapter.getEnergyStored());

        enabled.set(false);
        assertEquals(0, adapter.receiveEnergy(250, false));
        assertEquals(250, authoritative.getEnergyStored());
        assertFalse(adapter.canExtract());
        assertEquals(0, adapter.extractEnergy(100, false));
    }
}
