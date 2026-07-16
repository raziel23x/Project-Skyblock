package raziel23x.projectskyblock.client;

import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import raziel23x.projectskyblock.ProjectSkyblock;
import raziel23x.projectskyblock.machine.crusher.CrusherPowerSource;
import raziel23x.projectskyblock.menu.CobblestoneCrusherMenu;
import raziel23x.projectskyblock.client.gui.MachineGuiRenderHelper;

public final class CobblestoneCrusherScreen
        extends AbstractContainerScreen<CobblestoneCrusherMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            ProjectSkyblock.MOD_ID,
            "textures/gui/container/cobblestone_crusher.png"
    );
    private static final ResourceLocation GEAR = ResourceLocation.fromNamespaceAndPath(
            ProjectSkyblock.MOD_ID,
            "textures/gui/sprites/crusher_gear.png"
    );
    private static final ResourceLocation CRUSHER_HEAD = ResourceLocation.fromNamespaceAndPath(
            ProjectSkyblock.MOD_ID,
            "textures/gui/sprites/crusher_head.png"
    );
    private static final ResourceLocation FLAMES = ResourceLocation.fromNamespaceAndPath(
            ProjectSkyblock.MOD_ID,
            "textures/gui/sprites/crusher_flames.png"
    );

    private static final int PROGRESS_X = 103;
    private static final int PROGRESS_Y = 96;
    private static final int PROGRESS_WIDTH = 72;
    private static final int PROGRESS_HEIGHT = 5;

    private static final int FUEL_X = 9;
    private static final int FUEL_Y = 55;
    private static final int FUEL_WIDTH = 16;
    private static final int FUEL_HEIGHT = 28;

    private static final int ENERGY_X = 198;
    private static final int ENERGY_Y = 28;
    private static final int ENERGY_WIDTH = 9;
    private static final int ENERGY_HEIGHT = 59;

    // The machine chamber in the GUI texture spans X=66..112 and is centered at X=89.
    // Keep every animated machine part relative to this center to prevent drift.
    private static final int MACHINE_CENTER_X = 89;
    private static final int GEAR_CENTER_Y = 39;
    private static final int GEAR_SPACING = 11;
    private static final int GEAR_SIZE = 24;
    private static final int CRUSHER_HEAD_WIDTH = 24;
    private static final int CRUSHER_HEAD_HEIGHT = 12;

    private int animationTicks;
    private int previousProgress;
    private int completionBurstTicks;

    public CobblestoneCrusherScreen(
            CobblestoneCrusherMenu menu,
            Inventory playerInventory,
            Component title) {
        super(menu, playerInventory, title);
        imageWidth = 232;
        imageHeight = 202;
        titleLabelX = 8;
        titleLabelY = 6;
        inventoryLabelX = 35;
        inventoryLabelY = 110;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        animationTicks++;

        int progress = menu.getProgress();
        int processTime = menu.getProcessTime();
        if (previousProgress > progress && previousProgress >= Math.max(1, processTime - 2)) {
            completionBurstTicks = 10;
            if (minecraft != null && minecraft.player != null) {
                minecraft.player.playSound(SoundEvents.STONE_BREAK, 0.55F, 0.72F);
            }
        }
        previousProgress = progress;

        if (completionBurstTicks > 0) {
            completionBurstTicks--;
        }
    }

    @Override
    protected void renderBg(
            GuiGraphics guiGraphics,
            float partialTick,
            int mouseX,
            int mouseY) {
        int left = leftPos;
        int top = topPos;

        guiGraphics.blit(
                TEXTURE,
                left,
                top,
                0,
                0,
                imageWidth,
                imageHeight,
                256,
                256
        );

        // Use the same player inventory color and slot treatment as the Thermal Generator.
        MachineGuiRenderHelper.drawPlayerInventory(guiGraphics, left, top, 35, 120, 178);

        renderProgress(guiGraphics, left, top);
        renderEnergy(guiGraphics, left, top);
        renderMachineAnimation(guiGraphics, left, top, partialTick);
        renderFuelFlame(guiGraphics, left, top);
        renderInputVibration(guiGraphics, left, top, partialTick);
        renderDustAndSparks(guiGraphics, left, top, partialTick);
    }

    private void renderProgress(GuiGraphics guiGraphics, int left, int top) {
        int progressWidth = menu.getScaledProgress(PROGRESS_WIDTH);
        if (progressWidth <= 0) {
            return;
        }

        guiGraphics.fill(
                left + PROGRESS_X,
                top + PROGRESS_Y,
                left + PROGRESS_X + progressWidth,
                top + PROGRESS_Y + PROGRESS_HEIGHT,
                0xFF31B53B
        );
        guiGraphics.fill(
                left + PROGRESS_X,
                top + PROGRESS_Y + PROGRESS_HEIGHT - 1,
                left + PROGRESS_X + progressWidth,
                top + PROGRESS_Y + PROGRESS_HEIGHT,
                0xFF187020
        );
        if (menu.isWorking()) {
            int shine = (animationTicks / 2) % Math.max(1, progressWidth);
            guiGraphics.fill(
                    left + PROGRESS_X + shine,
                    top + PROGRESS_Y,
                    left + PROGRESS_X + Math.min(progressWidth, shine + 2),
                    top + PROGRESS_Y + 1,
                    0xFF8AF08D
            );
        }
    }

    private void renderEnergy(GuiGraphics guiGraphics, int left, int top) {
        int energyHeight = menu.getScaledEnergy(ENERGY_HEIGHT);
        if (energyHeight <= 0) {
            return;
        }

        int energyTop = top + ENERGY_Y + ENERGY_HEIGHT - energyHeight;
        guiGraphics.fill(
                left + ENERGY_X,
                energyTop,
                left + ENERGY_X + ENERGY_WIDTH,
                top + ENERGY_Y + ENERGY_HEIGHT,
                0xFF0867E8
        );
        guiGraphics.fill(
                left + ENERGY_X + 2,
                energyTop,
                left + ENERGY_X + 4,
                top + ENERGY_Y + ENERGY_HEIGHT,
                0xFF39A7FF
        );

        if (getPowerSource() == CrusherPowerSource.FE && menu.isWorking()) {
            int pulseY = top + ENERGY_Y + ENERGY_HEIGHT
                    - ((animationTicks * 2) % Math.max(1, energyHeight));
            guiGraphics.fill(
                    left + ENERGY_X,
                    pulseY,
                    left + ENERGY_X + ENERGY_WIDTH,
                    Math.min(top + ENERGY_Y + ENERGY_HEIGHT, pulseY + 2),
                    0xFF8ED6FF
            );
        }
    }

    private void renderMachineAnimation(
            GuiGraphics guiGraphics,
            int left,
            int top,
            float partialTick) {
        float time = animationTicks + partialTick;
        float leftAngle = menu.isWorking() ? time * 12.0F : 0.0F;
        float rightAngle = menu.isWorking() ? -time * 12.0F : 0.0F;

        renderRotatingSprite(
                guiGraphics,
                GEAR,
                left + MACHINE_CENTER_X - GEAR_SPACING,
                top + GEAR_CENTER_Y,
                GEAR_SIZE,
                GEAR_SIZE,
                leftAngle
        );
        renderRotatingSprite(
                guiGraphics,
                GEAR,
                left + MACHINE_CENTER_X + GEAR_SPACING,
                top + GEAR_CENTER_Y,
                GEAR_SIZE,
                GEAR_SIZE,
                rightAngle
        );

        int headOffset = menu.isWorking()
                ? Math.round((Mth.sin(time * 0.42F) + 1.0F) * 2.0F)
                : 0;
        guiGraphics.blit(
                CRUSHER_HEAD,
                left + MACHINE_CENTER_X - CRUSHER_HEAD_WIDTH / 2,
                top + 50 + headOffset,
                0,
                0,
                CRUSHER_HEAD_WIDTH,
                CRUSHER_HEAD_HEIGHT,
                CRUSHER_HEAD_WIDTH,
                CRUSHER_HEAD_HEIGHT
        );
    }

    private void renderRotatingSprite(
            GuiGraphics guiGraphics,
            ResourceLocation sprite,
            int centerX,
            int centerY,
            int width,
            int height,
            float angle) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX, centerY, 0.0F);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(angle));
        guiGraphics.blit(sprite, -width / 2, -height / 2, 0, 0, width, height, width, height);
        guiGraphics.pose().popPose();
    }

    private void renderFuelFlame(GuiGraphics guiGraphics, int left, int top) {
        if (menu.getBurnTimeRemaining() <= 0) {
            return;
        }

        int frame = (animationTicks / 3) & 3;
        guiGraphics.blit(
                FLAMES,
                left + FUEL_X,
                top + FUEL_Y,
                frame * 16,
                0,
                16,
                28,
                64,
                28
        );

        if (getPowerSource() == CrusherPowerSource.FUEL && menu.isWorking()) {
            guiGraphics.blit(
                    FLAMES,
                    left + MACHINE_CENTER_X - 16,
                    top + 61,
                    frame * 16,
                    0,
                    16,
                    28,
                    64,
                    28
            );
            guiGraphics.blit(
                    FLAMES,
                    left + MACHINE_CENTER_X,
                    top + 61,
                    ((frame + 2) & 3) * 16,
                    0,
                    16,
                    28,
                    64,
                    28
            );
        }
    }

    private void renderInputVibration(
            GuiGraphics guiGraphics,
            int left,
            int top,
            float partialTick) {
        if (!menu.isWorking()) {
            return;
        }

        int phase = (int) ((animationTicks + partialTick) * 2.0F) & 3;
        int offset = phase == 0 ? -1 : phase == 2 ? 1 : 0;
        int color = 0xAA6A6A6A;
        guiGraphics.fill(left + 25 + offset, top + 37, left + 27 + offset, top + 39, color);
        guiGraphics.fill(left + 47 - offset, top + 43, left + 49 - offset, top + 45, color);
        guiGraphics.fill(left + 25 - offset, top + 48, left + 27 - offset, top + 50, color);
    }

    private void renderDustAndSparks(
            GuiGraphics guiGraphics,
            int left,
            int top,
            float partialTick) {
        if (!menu.isWorking() && completionBurstTicks <= 0) {
            return;
        }

        int count = completionBurstTicks > 0 ? 10 : 4;
        float time = animationTicks + partialTick;
        for (int index = 0; index < count; index++) {
            int cycle = (int) (time * (1 + index % 3) + index * 11) & 31;
            int x = left + 89 + ((index * 13 + cycle) % 30) - 15;
            int y = top + 66 + ((index * 7 - cycle) % 18);
            int size = index % 3 == 0 ? 2 : 1;
            int color;
            if (getPowerSource() == CrusherPowerSource.FE && index % 3 == 0) {
                color = 0xFF55B8FF;
            } else if (index % 4 == 0) {
                color = 0xFFFFB13B;
            } else {
                color = 0xFF9A9185;
            }
            guiGraphics.fill(x, y, x + size, y + size, color);
        }
    }

    @Override
    protected void renderLabels(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY) {
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, 0x303030, false);
        guiGraphics.drawString(
                font,
                playerInventoryTitle,
                inventoryLabelX,
                inventoryLabelY,
                0x303030,
                false
        );

        Component powerName = getPowerSourceName();
        guiGraphics.drawString(
                font,
                Component.translatable(
                        "gui.projectskyblock.cobblestone_crusher.power_source",
                        powerName
                ),
                10,
                97,
                getPowerColor(),
                false
        );

        int percent = Math.min(100, menu.getProgress() * 100 / menu.getProcessTime());
        guiGraphics.drawString(
                font,
                Component.literal("Progress: " + percent + "%"),
                72,
                86,
                0x303030,
                false
        );

        Component energyText = Component.literal(
                formatEnergy(menu.getEnergyStored())
                        + " / " + formatEnergy(menu.getEnergyCapacity())
        );
        guiGraphics.drawString(
                font,
                energyText,
                imageWidth - 8 - font.width(energyText),
                7,
                0x303030,
                false
        );
    }

    @Override
    public void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        if (isHovering(ENERGY_X, ENERGY_Y, ENERGY_WIDTH, ENERGY_HEIGHT, mouseX, mouseY)) {
            guiGraphics.renderTooltip(
                    font,
                    Component.translatable(
                            "gui.projectskyblock.cobblestone_crusher.energy",
                            menu.getEnergyStored(),
                            menu.getEnergyCapacity()
                    ),
                    mouseX,
                    mouseY
            );
        } else if (isHovering(FUEL_X, FUEL_Y, FUEL_WIDTH, FUEL_HEIGHT, mouseX, mouseY)) {
            guiGraphics.renderTooltip(
                    font,
                    Component.translatable(
                            "gui.projectskyblock.cobblestone_crusher.fuel",
                            menu.getBurnTimeRemaining(),
                            menu.getBurnTimeTotal()
                    ),
                    mouseX,
                    mouseY
            );
        } else if (isHovering(
                PROGRESS_X,
                PROGRESS_Y,
                PROGRESS_WIDTH,
                PROGRESS_HEIGHT,
                mouseX,
                mouseY
        )) {
            guiGraphics.renderTooltip(
                    font,
                    Component.translatable(
                            "gui.projectskyblock.cobblestone_crusher.progress",
                            menu.getProgress(),
                            menu.getProcessTime()
                    ),
                    mouseX,
                    mouseY
            );
        }
    }

    private int getPowerColor() {
        return switch (getPowerSource()) {
            case FE -> 0x2478D4;
            case FUEL -> 0xD26A19;
            case NONE -> 0x438A35;
        };
    }

    private Component getPowerSourceName() {
        return Component.translatable(
                "gui.projectskyblock.cobblestone_crusher.power."
                        + getPowerSource().name().toLowerCase()
        );
    }

    private CrusherPowerSource getPowerSource() {
        CrusherPowerSource[] values = CrusherPowerSource.values();
        int id = menu.getPowerSourceId();
        return id >= 0 && id < values.length
                ? values[id]
                : CrusherPowerSource.NONE;
    }

    private static String formatEnergy(int energy) {
        if (energy >= 1_000_000) {
            return (energy / 1_000_000) + "M FE";
        }
        if (energy >= 1_000) {
            return (energy / 1_000) + "K FE";
        }
        return energy + " FE";
    }
}
