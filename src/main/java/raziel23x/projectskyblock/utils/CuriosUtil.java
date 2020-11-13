package raziel23x.projectskyblock.utils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;

// Added findItem AND findMod into single class

public class CuriosUtil {
    public static ItemStack findItem(Item item, LivingEntity living) {
        return CuriosApi.getCuriosHelper().findEquippedCurio(item, living)
                .map(ImmutableTriple::getRight)
                .orElse(ItemStack.EMPTY);
    }

    public static boolean findMod () {
        return (ModList.get() != null && ModList.get().getModContainerById("curios").isPresent()); }
}