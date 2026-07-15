package raziel23x.projectskyblock.compat.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.items.IItemHandler;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.registry.ModItems;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Optional Curios integration implemented through a guarded reflection bridge.
 * Project Skyblock therefore has no hard compile-time or runtime dependency on Curios.
 */
public final class CuriosRepairCompat {
    private static final String CURIOS_MOD_ID = "curios";
    private static boolean reflectionFailureLogged;

    private CuriosRepairCompat() {
    }

    public static boolean hasEquippedRepairGem(LivingEntity entity) {
        if (!ModList.get().isLoaded(CURIOS_MOD_ID)) {
            return false;
        }

        try {
            Class<?> curiosApi = Class.forName("top.theillusivec4.curios.api.CuriosApi");
            Method getCuriosInventory = curiosApi.getMethod("getCuriosInventory", LivingEntity.class);
            Object optionalHandler = getCuriosInventory.invoke(null, entity);

            if (!(optionalHandler instanceof Optional<?> optional) || optional.isEmpty()) {
                return false;
            }

            Object handler = optional.get();
            Class<?> handlerType = Class.forName("top.theillusivec4.curios.api.type.capability.ICuriosItemHandler");
            Object equipped = handlerType.getMethod("getEquippedCurios").invoke(handler);

            if (!(equipped instanceof IItemHandler equippedCurios)) {
                return false;
            }

            for (int slot = 0; slot < equippedCurios.getSlots(); slot++) {
                ItemStack stack = equippedCurios.getStackInSlot(slot);
                if (stack.is(ModItems.REPAIR_GEM.get())) {
                    return true;
                }
            }
        } catch (ReflectiveOperationException | LinkageError exception) {
            logReflectionFailureOnce(exception);
        }

        return false;
    }

    private static void logReflectionFailureOnce(Throwable exception) {
        if (reflectionFailureLogged) {
            return;
        }

        reflectionFailureLogged = true;
        ProjectSkyblock.LOGGER.warn(
                "Curios is installed, but Project Skyblock could not initialize Repair Gem Curios compatibility.",
                exception
        );
    }
}
