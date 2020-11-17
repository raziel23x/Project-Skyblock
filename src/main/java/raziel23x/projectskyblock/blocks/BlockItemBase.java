package raziel23x.projectskyblock.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

import raziel23x.projectskyblock.ProjectSkyblock;


public class BlockItemBase extends BlockItem {
    public BlockItemBase(Block block) {
        super(block, new Item.Properties().group(ProjectSkyblock.TAB));
    }


}
