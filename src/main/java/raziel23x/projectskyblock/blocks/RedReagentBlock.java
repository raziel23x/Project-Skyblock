package raziel23x.projectskyblock.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class RedReagentBlock extends Block {

    public RedReagentBlock() {
        super(Block.Properties.create(Material.SNOW)
                .sound(SoundType.SNOW)
                .hardnessAndResistance(2.0f, 3.0f)
                .harvestLevel(0)
                .harvestTool(ToolType.SHOVEL)
        );
    }
}
