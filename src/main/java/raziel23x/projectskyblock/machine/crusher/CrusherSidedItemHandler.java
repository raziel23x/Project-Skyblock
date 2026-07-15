package raziel23x.projectskyblock.machine.crusher;

import raziel23x.projectskyblock.machine.base.MachineSidedItemHandler;

/** Crusher-specific sided view backed by the shared machine handler. */
public final class CrusherSidedItemHandler extends MachineSidedItemHandler {
    public CrusherSidedItemHandler(
            CrusherInventory inventory,
            int[] slots,
            boolean allowInsert,
            boolean allowExtract) {
        super(inventory, slots, allowInsert, allowExtract, "Crusher");
    }
}
