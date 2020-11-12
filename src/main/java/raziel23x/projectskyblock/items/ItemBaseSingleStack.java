package raziel23x.projectskyblock.items;

import net.minecraft.item.Item;
import raziel23x.projectskyblock.ProjectSkyblock;

public class ItemBaseSingleStack extends Item {
    public ItemBaseSingleStack() {

        super(new Properties().group(ProjectSkyblock.TAB).maxStackSize(1));
    }
}
