package raziel23x.projectskyblock.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import org.apache.commons.lang3.tuple.ImmutableTriple;


public class CuriosUtil
{

    public static ItemStack findItem(Item item, LivingEntity living)
    {
        return CuriosApi.getCuriosHelper().findEquippedCurio(item, living)
                .map(ImmutableTriple::getRight)
                .orElse(ItemStack.EMPTY);
    }
}