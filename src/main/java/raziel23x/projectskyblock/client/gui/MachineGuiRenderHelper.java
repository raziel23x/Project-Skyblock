package raziel23x.projectskyblock.client.gui;

import net.minecraft.client.gui.GuiGraphics;

/** Shared visual helpers used by Project Skyblock machine screens. */
public final class MachineGuiRenderHelper {
    private static final int PANEL_COLOR = 0xFFC6C6C6;
    private static final int SLOT_DARK = 0xFF373737;
    private static final int SLOT_LIGHT = 0xFFFFFFFF;
    private static final int SLOT_INNER = 0xFF8B8B8B;

    private MachineGuiRenderHelper() {
    }

    /**
     * Draws the standard Project Skyblock 3x9 player inventory and hotbar.
     * Coordinates are the top-left corner of each 18x18 slot frame.
     */
    public static void drawPlayerInventory(
            GuiGraphics graphics,
            int left,
            int top,
            int inventoryX,
            int inventoryY,
            int hotbarY) {
        // Clean panel behind the slots so every machine uses the same shade.
        graphics.fill(
                left + inventoryX - 2,
                top + inventoryY - 2,
                left + inventoryX + 9 * 18 + 2,
                top + hotbarY + 20,
                PANEL_COLOR
        );

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                drawSlot(
                        graphics,
                        left + inventoryX + column * 18,
                        top + inventoryY + row * 18
                );
            }
        }

        for (int column = 0; column < 9; column++) {
            drawSlot(
                    graphics,
                    left + inventoryX + column * 18,
                    top + hotbarY
            );
        }
    }

    public static void drawSlot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, SLOT_DARK);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, SLOT_LIGHT);
        graphics.fill(x + 2, y + 2, x + 17, y + 17, SLOT_INNER);
    }

    /**
     * Draws a Crusher-style vertical gauge frame and fill.
     * The supplied x/y point to the 9x59 interior fill area.
     */
    public static void drawVerticalGauge(
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            int scaledAmount,
            int fillColor,
            int highlightColor) {
        // Raised metal frame matching the Crusher FE gauge.
        graphics.fill(x - 3, y - 4, x + width + 5, y + height + 5, 0xFFFFFFFF);
        graphics.fill(x - 1, y - 2, x + width + 4, y + height + 4, 0xFF202020);
        graphics.fill(x, y, x + width, y + height, 0xFF101010);

        if (scaledAmount > 0) {
            int fillTop = y + height - scaledAmount;
            graphics.fill(x, fillTop, x + width, y + height, fillColor);
            graphics.fill(x + 2, fillTop, x + 4, y + height, highlightColor);
        }

        // Right-side measuring marks.
        int tickStartX = x + width + 4;
        int longTickEndX = tickStartX + 10;
        int shortTickEndX = tickStartX + 7;
        for (int index = 0; index <= 4; index++) {
            int tickY = y + index * height / 4;
            graphics.fill(
                    tickStartX,
                    tickY,
                    index == 0 || index == 4 ? longTickEndX : shortTickEndX,
                    tickY + 1,
                    0xFF303030
            );
        }
    }
}
