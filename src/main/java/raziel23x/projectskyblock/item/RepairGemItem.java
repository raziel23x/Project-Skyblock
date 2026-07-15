package raziel23x.projectskyblock.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public final class RepairGemItem extends Item {
    public RepairGemItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.projectskyblock.repair_gem.line_1")
                .withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.projectskyblock.repair_gem.line_2")
                .withStyle(ChatFormatting.YELLOW));
    }
}
