package raziel23x.projectskyblock.simulation.machine.component;

import raziel23x.projectskyblock.simulation.inventory.SimulationItemKey;

/** Minecraft-independent insertion rule for one machine inventory slot. */
@FunctionalInterface
public interface MachineInventorySlotRule {
    MachineInventorySlotRule ACCEPT_ALL = item -> true;

    boolean accepts(SimulationItemKey item);
}
