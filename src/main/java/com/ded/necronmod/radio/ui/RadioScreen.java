package com.ded.necronmod.radio.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;

import com.ded.necronmod.radio.data.RadioCountry;
import com.ded.necronmod.radio.data.RadioData;
import com.ded.necronmod.radio.RadioSettings;
import com.ded.necronmod.radio.data.RadioStation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;

/**
 * Экран радио: общий для блока и переносного предмета.
 * Две ручки (страна и частота), громкость, питание, режим «наушники/динамик» (только портативное),
 * дисплей с кодом для обмена, скан и случайная станция.
 *
 * Экран ничего не знает о сети: он меняет RadioSettings и вызывает onChange (не чаще раза в SEND_DELAY_TICKS
 * и при закрытии). Блок и предмет сами решают, что с настройками делать (пакет на сервер и т. п.).
 */
public final class RadioScreen extends Screen {
    private static final int W = 300, H = 206;
    private static final int SEND_DELAY_TICKS = 4; // 0,2 с без изменений = отправляем
    private static final int STATUS_TICKS = 70;

    private static final int PANEL = 0xFF2E2E2E, PANEL_BORDER = 0xFF121212, PANEL_LIGHT = 0xFF444444;
    private static final int LCD = 0xFF0B1A0B, LCD_BORDER = 0xFF050B05;
    private static final int GREEN = 0xFF55FF55, GREEN_DIM = 0xFF2E6B2E, RED = 0xFFFF5555, GRAY = 0xFFAAAAAA, WHITE = 0xFFFFFFFF;

    private final boolean portable;
    private final Consumer<RadioSettings> onChange;
    private final List<RadioCountry> countries;
    private final int slots;

    private RadioSettings settings;
    private int countryIndex;
    private boolean dirty;
    private int dirtyTicks;
    private String status = "";
    private boolean statusError;
    private int statusTicks;
    private String codeText = "";

    private final java.util.Random random = new java.util.Random();

    @Nullable private EditBox codeBox;
    @Nullable private Button powerButton;
    @Nullable private Button modeButton;

    /**
     * @param portable true для переносного радио (показывает переключатель «наушники/динамик»)
     * @param onChange получает актуальные настройки после изменений (с задержкой) и при закрытии
     */
    public RadioScreen(RadioSettings initial, boolean portable, Consumer<RadioSettings> onChange) {
        super(Component.literal(portable ? "Переносное радио" : "Радио"));
        this.portable = portable;
        this.onChange = onChange;
        this.countries = RadioData.countries();
        this.slots = Math.max(2, RadioData.slotsPerCountry());

        int idx = 0;
        for (int i = 0; i < countries.size(); i++) if (countries.get(i).code().equals(initial.country())) idx = i;
        this.countryIndex = idx;
        String code = countries.isEmpty() ? initial.country() : countries.get(idx).code();
        this.settings = initial.withTuning(code, Math.max(0, Math.min(slots - 1, initial.slot())));
    }

    // ------------------------------------------------------------------ виджеты

    @Override
    protected void init() {
        int left = left(), top = top();
        int cy = top + 96, r = 24, cxCountry = left + 62, cxFreq = left + W - 62;

        addRenderableWidget(new RadioKnob(cxCountry, cy, r, Component.literal("Страна"),
                () -> Math.max(1, countries.size()), () -> countryIndex, this::tuneCountry));
        addRenderableWidget(new RadioKnob(cxFreq, cy, r, Component.literal("Частота"),
                () -> slots, () -> settings.slot(), this::tuneSlot));

        // стрелки под ручками (для тех, кому неудобно крутить мышью)
        addRenderableWidget(Button.builder(Component.literal("<"), b -> tuneCountry(-1)).bounds(cxCountry - 24, top + 148, 22, 14).build());
        addRenderableWidget(Button.builder(Component.literal(">"), b -> tuneCountry(1)).bounds(cxCountry + 2, top + 148, 22, 14).build());
        addRenderableWidget(Button.builder(Component.literal("<"), b -> tuneSlot(-1)).bounds(cxFreq - 24, top + 148, 22, 14).build());
        addRenderableWidget(Button.builder(Component.literal(">"), b -> tuneSlot(1)).bounds(cxFreq + 2, top + 148, 22, 14).build());

        // середина: громкость, питание, режим
        int mx = left + W / 2 - 48;
        addRenderableWidget(new VolumeSlider(mx, top + 74, 96, 18, settings.volume()));
        powerButton = addRenderableWidget(Button.builder(Component.empty(), b -> togglePower()).bounds(mx, top + 96, 96, 18).build());
        if (portable) {
            modeButton = addRenderableWidget(Button.builder(Component.empty(), b -> toggleMode()).bounds(mx, top + 118, 96, 18).build());
        }

        // ряд кнопок поиска
        int y = top + 166, x = left + 10, bw = 66;
        addRenderableWidget(Button.builder(Component.literal("< Скан"), b -> scan(-1)).bounds(x, y, bw, 16).build());
        addRenderableWidget(Button.builder(Component.literal("Скан >"), b -> scan(1)).bounds(x + (bw + 5), y, bw, 16).build());
        addRenderableWidget(Button.builder(Component.literal("Случайная"), b -> randomStation()).bounds(x + 2 * (bw + 5), y, bw, 16).build());
        addRenderableWidget(Button.builder(Component.literal("Готово"), b -> onClose()).bounds(x + 3 * (bw + 5), y, bw, 16).build());

        // обмен кодами
        int y2 = top + 186;
        codeBox = addRenderableWidget(new EditBox(font, left + 10, y2, 140, 16, Component.literal("Код")));
        codeBox.setMaxLength(16);
        codeBox.setHint(Component.literal("Код, например NL 102.3"));
        codeBox.setValue(codeText);
        codeBox.setResponder(s -> codeText = s);
        addRenderableWidget(Button.builder(Component.literal("Копировать"), b -> copyCode()).bounds(left + 156, y2, 62, 16).build());
        addRenderableWidget(Button.builder(Component.literal("Найти"), b -> applyCode()).bounds(left + 222, y2, 62, 16).build());

        refreshButtons();
    }

    private int left() { return (width - W) / 2; }
    private int top() { return (height - H) / 2; }

    // ------------------------------------------------------------------ состояние (package-private для тестов)

    @Nullable
    RadioCountry country() { return countries.isEmpty() ? null : countries.get(countryIndex); }

    @Nullable
    RadioStation station() {
        RadioCountry c = country();
        return c == null ? null : c.stationAt(settings.slot());
    }

    RadioSettings settings() { return settings; }
    String statusText() { return status; }

    private void commit(RadioSettings next) {
        settings = next;
        dirty = true;
        dirtyTicks = 0;
        refreshButtons();
    }

    private void flush() {
        if (!dirty) return;
        dirty = false;
        onChange.accept(settings);
    }

    void tuneCountry(int delta) {
        if (countries.isEmpty() || delta == 0) return;
        countryIndex = RadioTuning.wrap(countryIndex + delta, countries.size());
        commit(settings.withTuning(countries.get(countryIndex).code(), settings.slot()));
        click(1.2f);
    }

    void tuneSlot(int delta) {
        if (delta == 0) return;
        commit(settings.withTuning(settings.country(), RadioTuning.wrap(settings.slot() + delta, slots)));
        click(station() != null ? 1.6f : 1.0f); // на станции щелчок «звонче»
    }

    void scan(int dir) {
        RadioCountry c = country();
        int next = c == null ? -1 : RadioTuning.nextWithStation(settings.slot(), dir, slots, s -> c.stationAt(s) != null);
        if (next < 0) { setStatus("В этой стране нет станций", true); return; }
        commit(settings.withTuning(settings.country(), next));
        click(1.6f);
    }

    void randomStation() {
        List<int[]> found = new ArrayList<>(); // {индекс страны, слот}
        for (int i = 0; i < countries.size(); i++)
            for (int s = 0; s < slots; s++)
                if (countries.get(i).stationAt(s) != null) found.add(new int[] {i, s});
        if (found.isEmpty()) { setStatus("Станций нет", true); return; }
        int[] pick = found.get(random.nextInt(found.size()));
        countryIndex = pick[0];
        commit(settings.withTuning(countries.get(pick[0]).code(), pick[1]));
        click(1.6f);
    }

    void togglePower() { commit(settings.withPlaying(!settings.playing())); click(settings.playing() ? 1.4f : 0.8f); }
    void toggleMode() { commit(settings.withSpeaker(!settings.speaker())); click(1.0f); }
    void setVolume(float v) { commit(settings.withVolume(Math.max(0f, Math.min(1f, v)))); }

    void copyCode() {
        String code = RadioTuning.shareCode(settings.country(), settings.slot(), slots);
        if (minecraft != null) minecraft.keyboardHandler.setClipboard(code);
        setStatus("Скопировано: " + code, false);
    }

    void applyCode() {
        RadioTuning.ParsedCode p = RadioTuning.parse(codeText,
                code -> RadioData.byCode(code) != null, slots);
        if (p == null) { setStatus("Неверный код. Пример: NL 102.3", true); return; }
        for (int i = 0; i < countries.size(); i++) if (countries.get(i).code().equals(p.country())) countryIndex = i;
        commit(settings.withTuning(p.country(), p.slot()));
        setStatus("Настроено: " + RadioTuning.shareCode(p.country(), p.slot(), slots), false);
        click(1.6f);
    }

    private void setStatus(String text, boolean error) {
        status = text;
        statusError = error;
        statusTicks = STATUS_TICKS;
    }

    private void refreshButtons() {
        if (powerButton != null) powerButton.setMessage(Component.literal(settings.playing() ? "Питание: ВКЛ" : "Питание: ВЫКЛ"));
        if (modeButton != null) modeButton.setMessage(Component.literal(settings.speaker() ? "Режим: ДИНАМИК" : "Режим: НАУШНИКИ"));
    }

    private void click(float pitch) {
        if (minecraft != null) minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, pitch));
    }

    // ------------------------------------------------------------------ жизненный цикл и ввод

    @Override
    public void tick() {
        if (dirty && ++dirtyTicks >= SEND_DELAY_TICKS) flush();
        if (statusTicks > 0 && --statusTicks == 0) status = "";
    }

    @Override
    public void removed() {
        flush(); // закрыли экран: настройки уходят сразу, без ожидания паузы
    }

    /** Не ставим одиночную игру на паузу: радио должно играть, пока экран открыт. */
    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (codeBox != null && codeBox.isFocused()) {
            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) { applyCode(); return true; }
            return super.keyPressed(key, scanCode, modifiers);
        }
        switch (key) {
            case GLFW.GLFW_KEY_RIGHT -> { tuneSlot(1); return true; }
            case GLFW.GLFW_KEY_LEFT -> { tuneSlot(-1); return true; }
            case GLFW.GLFW_KEY_UP -> { tuneCountry(1); return true; }
            case GLFW.GLFW_KEY_DOWN -> { tuneCountry(-1); return true; }
            case GLFW.GLFW_KEY_SPACE -> { togglePower(); return true; }
            default -> { return super.keyPressed(key, scanCode, modifiers); }
        }
    }

    /** Колесо вне ручек подстраивает частоту, с Shift страну. */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;
        if (scrollY == 0) return false;
        int dir = scrollY > 0 ? 1 : -1;
        if (hasShiftDown()) tuneCountry(dir); else tuneSlot(dir);
        return true;
    }

    // ------------------------------------------------------------------ отрисовка

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(g, mouseX, mouseY, partialTick);
        int left = left(), top = top();
        g.fill(left - 2, top - 2, left + W + 2, top + H + 2, PANEL_BORDER);
        g.fill(left, top, left + W, top + H, PANEL);
        g.fill(left, top, left + W, top + 1, PANEL_LIGHT);
        // дисплей
        g.fill(left + 8, top + 16, left + W - 8, top + 64, LCD_BORDER);
        g.fill(left + 9, top + 17, left + W - 9, top + 63, LCD);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        int left = left(), top = top();
        boolean on = settings.playing();
        int fg = on ? GREEN : GREEN_DIM;

        g.drawCenteredString(font, title, left + W / 2, top + 5, WHITE);

        RadioCountry c = country();
        RadioStation st = station();
        int textMax = 200;
        String countryLine = c == null ? "Нет данных" : "[" + c.code() + "] " + c.name();
        g.drawString(font, font.plainSubstrByWidth(countryLine, textMax), left + 14, top + 21, fg, false);

        String stationLine = st != null ? st.name() : "— нет сигнала —";
        g.drawString(font, font.plainSubstrByWidth(stationLine, textMax), left + 14, top + 33, st != null ? fg : (on ? RED : GREEN_DIM), false);

        if (st != null) {
            String extra = joinNonEmpty(st.region(), st.genre());
            if (!extra.isEmpty()) g.drawString(font, font.plainSubstrByWidth(extra, textMax), left + 14, top + 45, GREEN_DIM, false);
        }

        // частота и код для обмена (справа, крупно)
        String mhz = RadioTuning.formatFrequency(RadioTuning.frequency(settings.slot(), slots));
        var pose = g.pose();
        pose.pushPose();
        pose.translate(left + W - 14, top + 21, 0);
        pose.scale(2f, 2f, 1f);
        g.drawString(font, mhz, -font.width(mhz), 0, fg, false);
        pose.popPose();
        g.drawString(font, "FM", left + W - 14 - font.width("FM"), top + 41, fg, false);
        if (c != null) {
            String code = RadioTuning.shareCode(c.code(), settings.slot(), slots);
            g.drawString(font, code, left + W - 14 - font.width(code), top + 51, GREEN_DIM, false);
        }
        // индикатор: зелёный = станция играет, красный = шум/нет сигнала при включённом питании
        int led = !on ? 0xFF303030 : (st != null ? 0xFF33FF33 : 0xFFAA2222);
        g.fill(left + W - 22, top + 4, left + W - 14, top + 12, led);

        // подписи ручек
        int cy = top + 96, r = 24, cxCountry = left + 62, cxFreq = left + W - 62;
        g.drawCenteredString(font, "СТРАНА", cxCountry, cy + r + 8, GRAY);
        g.drawCenteredString(font, c == null ? "-" : c.code(), cxCountry, cy + r + 19, WHITE);
        g.drawCenteredString(font, "ЧАСТОТА", cxFreq, cy + r + 8, GRAY);
        g.drawCenteredString(font, mhz, cxFreq, cy + r + 19, WHITE);

        if (!status.isEmpty()) g.drawCenteredString(font, status, left + W / 2, top + 150, statusError ? RED : GREEN);
    }

    private static String joinNonEmpty(String a, String b) {
        if (a == null || a.isEmpty()) return b == null ? "" : b;
        if (b == null || b.isEmpty()) return a;
        return a + " | " + b;
    }

    // ------------------------------------------------------------------ ползунок громкости

    private final class VolumeSlider extends AbstractSliderButton {
        VolumeSlider(int x, int y, int w, int h, double value) {
            super(x, y, w, h, Component.empty(), value);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal("Громкость: " + (int) Math.round(value * 100) + "%"));
        }

        @Override
        protected void applyValue() {
            setVolume((float) value);
        }
    }
}
