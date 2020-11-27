package raziel23x.projectskyblock.gameObjs.blocks.reagents;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class BlueReagentBlock extends Block {

    public BlueReagentBlock() {
        super(Block.Properties.create(Material.SAND)
                .sound(SoundType.SNOW)
                .hardnessAndResistance(2.0f, 3.0f)
                .harvestLevel(0)
                .harvestTool(ToolType.SHOVEL)
        );
    }
}
