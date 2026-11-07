package com.ded.necronmod.radio.ui;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

/**
 * Вращающаяся ручка без текстур (рисуется кругом и стрелкой). Позиции дискретные, оборот бесконечный:
 * управление колесом мыши (на ручке), перетаскиванием по кругу. Состояние хранит экран, ручка только читает его
 * через suppliers и сообщает о шагах через onStep (+1 по часовой, -1 против).
 */
final class RadioKnob extends AbstractWidget {
    private static final int RIM = 0xFF8C8C8C, RIM_HOT = 0xFFD0D0D0, BODY = 0xFF4A4A4A, CAP = 0xFF222222;
    private static final int TICK = 0xFF9A9A9A, TICK_ON = 0xFFFFD34D, POINTER = 0xFFFFD34D;
    private static final int MAX_TICKS = 48;

    private final int cx, cy, radius;
    private final IntSupplier steps, index;
    private final IntConsumer onStep;
    private final KnobMath.DragAccumulator drag = new KnobMath.DragAccumulator();
    private boolean dragging;
    private double displayAngle;
    private boolean angleReady;

    RadioKnob(int cx, int cy, int radius, Component name, IntSupplier steps, IntSupplier index, IntConsumer onStep) {
        super(cx - radius - 8, cy - radius - 8, 2 * radius + 16, 2 * radius + 16, name);
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        this.steps = steps;
        this.index = index;
        this.onStep = onStep;
    }

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int n = Math.max(1, steps.getAsInt());
        int current = index.getAsInt();
        double target = KnobMath.angleForIndex(current, n);
        if (!angleReady) { displayAngle = target; angleReady = true; }
        displayAngle = KnobMath.approach(displayAngle, target, 0.4);

        // деления по кругу (если позиций слишком много, рисуем каждое k-е)
        int every = (int) Math.ceil(n / (double) MAX_TICKS);
        for (int i = 0; i < n; i += every) {
            double a = Math.toRadians(KnobMath.angleForIndex(i, n));
            int tx = cx + (int) Math.round(Math.sin(a) * (radius + 5));
            int ty = cy - (int) Math.round(Math.cos(a) * (radius + 5));
            boolean on = i == current;
            int s = on ? 3 : 2;
            g.fill(tx - s / 2, ty - s / 2, tx - s / 2 + s, ty - s / 2 + s, on ? TICK_ON : TICK);
        }

        boolean hot = isHoveredOrFocused() || dragging;
        fillCircle(g, cx, cy, radius, hot ? RIM_HOT : RIM);
        fillCircle(g, cx, cy, radius - 2, BODY);

        // стрелка: от центра к краю
        double a = Math.toRadians(displayAngle);
        double sin = Math.sin(a), cos = Math.cos(a);
        for (int t = 3; t <= radius - 4; t++) {
            int px = cx + (int) Math.round(sin * t);
            int py = cy - (int) Math.round(cos * t);
            g.fill(px - 1, py - 1, px + 1, py + 1, POINTER);
        }
        fillCircle(g, cx, cy, 3, CAP);
    }

    /** Заливка круга горизонтальными линиями (хватает для прототипа без текстур). */
    static void fillCircle(GuiGraphics g, int cx, int cy, int r, int color) {
        for (int dy = -r; dy <= r; dy++) {
            int dx = (int) Math.floor(Math.sqrt((double) r * r - (double) dy * dy));
            g.fill(cx - dx, cy + dy, cx + dx + 1, cy + dy + 1, color);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!active || !visible || button != 0 || !isMouseOver(mouseX, mouseY)) return false;
        dragging = true;
        drag.reset();
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!dragging || button != 0) return false;
        double dx = mouseX - cx, dy = mouseY - cy;
        if (dx * dx + dy * dy < 25) { // у самого центра угол «шумит»
            drag.reset();
            return true;
        }
        int s = drag.update(KnobMath.mouseAngle(dx, dy), steps.getAsInt());
        if (s != 0) onStep.accept(s);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging && button == 0) {
            dragging = false;
            drag.reset();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!active || !visible || scrollY == 0 || !isMouseOver(mouseX, mouseY)) return false;
        onStep.accept(scrollY > 0 ? 1 : -1);
        return true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
