package raziel23x.projectskyblock.blocks;


import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

import static raziel23x.projectskyblock.utils.RegistryHandler.COBBLECRUSHER_TILE;


public class CobblestoneCrusherTile extends TileEntity {

    private ItemStackHandler handler;
    private int ticks;

    public CobblestoneCrusherTile() {
        super(COBBLECRUSHER_TILE.get());
    }
}
