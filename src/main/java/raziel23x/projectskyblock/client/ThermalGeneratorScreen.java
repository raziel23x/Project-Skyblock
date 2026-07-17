package raziel23x.projectskyblock.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import raziel23x.projectskyblock.menu.ThermalGeneratorMenu;
import raziel23x.projectskyblock.client.gui.MachineGuiRenderHelper;

public final class ThermalGeneratorScreen extends AbstractContainerScreen<ThermalGeneratorMenu> {
    public ThermalGeneratorScreen(ThermalGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 202;
        inventoryLabelY = 105;
    }

    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos, y = topPos;
        graphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFFC6C6C6);
        graphics.fill(x + 4, y + 4, x + 172, y + 198, 0xFF8B8B8B);
        graphics.fill(x + 6, y + 6, x + 170, y + 196, 0xFFC6C6C6);
        MachineGuiRenderHelper.drawSlot(graphics, x + 34, y + 48);

        MachineGuiRenderHelper.drawPlayerInventory(graphics, x, y, 8, 115, 175);

        int fluidHeight = menu.fluid() * 59 / menu.fluidCapacity();
        MachineGuiRenderHelper.drawVerticalGauge(
                graphics, x + 77, y + 18, 9, 59, fluidHeight,
                0xFFFF5A00, 0xFFFFA13A
        );

        int energyHeight = menu.energy() * 59 / menu.energyCapacity();
        MachineGuiRenderHelper.drawVerticalGauge(
                graphics, x + 136, y + 18, 9, 59, energyHeight,
                0xFF0867E8, 0xFF39A7FF
        );

        // Compact status LED below the gauges. The larger status panel was removed
        // because it competed with the gauge labels and made the text difficult to read.
        int ledColor = menu.generating() ? 0xFFFFA000 : 0xFF555555;
        graphics.fill(x + 71, y + 91, x + 79, y + 99, 0xFF24272C);
        graphics.fill(x + 72, y + 92, x + 78, y + 98, ledColor);
    }


    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 8, 0x404040, false);
        graphics.drawString(font, "Fuel", 30, 29, 0x404040, false);
        graphics.drawString(font, "Lava", 70, 79, 0xE87516, true);
        graphics.drawString(font, "FE", 133, 79, 0x55D9FF, true);
        graphics.drawString(font, menu.generating() ? "Generating" : "Idle", 83, 90, 0x404040, false);
        graphics.drawString(font, "Output: " + (menu.generating() ? menu.generationRate() : 0) + " FE/t", 83, 100, 0x404040, false);
        graphics.drawString(font, playerInventoryTitle, 8, inventoryLabelY, 0x404040, false);
    }

    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        renderGaugeTooltips(graphics, mouseX, mouseY);
    }

    private void renderGaugeTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        int relativeX = mouseX - leftPos;
        int relativeY = mouseY - topPos;

        if (relativeX >= 74 && relativeX < 101 && relativeY >= 14 && relativeY < 82) {
            graphics.renderTooltip(
                    font,
                    Component.literal("Lava: " + menu.fluid() + " / " + menu.fluidCapacity() + " mB"),
                    mouseX,
                    mouseY
            );
        } else if (relativeX >= 133 && relativeX < 160 && relativeY >= 14 && relativeY < 82) {
            graphics.renderTooltip(
                    font,
                    Component.literal("Energy: " + menu.energy() + " / " + menu.energyCapacity() + " FE"),
                    mouseX,
                    mouseY
            );
        }
    }
}
