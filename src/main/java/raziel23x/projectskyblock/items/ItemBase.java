package raziel23x.projectskyblock.items;

import net.minecraft.item.Item;
import raziel23x.projectskyblock.ProjectSkyblock;

public class ItemBase extends Item {
    public ItemBase() {

        super(new Item.Properties().group(ProjectSkyblock.TAB).maxStackSize(1));
    }
}
