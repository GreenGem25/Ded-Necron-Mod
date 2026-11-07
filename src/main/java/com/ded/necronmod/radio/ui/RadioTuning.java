package com.ded.necronmod.radio.ui;

import java.util.Locale;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Чистая логика настройки (без Minecraft): «виртуальная» FM-частота позиции, коды для обмена между игроками,
 * поиск ближайшей станции. Частоты ненастоящие: позиции 0..N-1 равномерно раскладываются на 87.5..108.0 МГц.
 */
public final class RadioTuning {
    public static final double MIN_MHZ = 87.5;
    public static final double MAX_MHZ = 108.0;

    private static final Pattern CODE = Pattern.compile(
            "^\\s*([A-Za-z]{2})\\s*[-:]?\\s*(\\d{2,3}(?:[.,]\\d)?)\\s*(?:MHz|FM)?\\s*$", Pattern.CASE_INSENSITIVE);

    public record ParsedCode(String country, int slot) {}

    private RadioTuning() {}

    /** Частота позиции, округлённая до 0.1 МГц. */
    public static double frequency(int slot, int slots) {
        if (slots <= 1) return MIN_MHZ;
        double f = MIN_MHZ + slot * (MAX_MHZ - MIN_MHZ) / (slots - 1);
        return Math.round(f * 10.0) / 10.0;
    }

    public static String formatFrequency(double mhz) {
        return String.format(Locale.ROOT, "%.1f", mhz);
    }

    /** Код для обмена: "NL 102.3". */
    public static String shareCode(String country, int slot, int slots) {
        return country + " " + formatFrequency(frequency(slot, slots));
    }

    /** Ближайшая позиция к частоте. */
    public static int slotForFrequency(double mhz, int slots) {
        if (slots <= 1) return 0;
        double step = (MAX_MHZ - MIN_MHZ) / (slots - 1);
        int slot = (int) Math.round((mhz - MIN_MHZ) / step);
        return Math.max(0, Math.min(slots - 1, slot));
    }

    /** Разбирает "NL 102.3" (также "nl-102,3", "NL:102.3 FM"). null, если код не понят или такой страны нет. */
    public static ParsedCode parse(String text, Predicate<String> countryExists, int slots) {
        if (text == null) return null;
        Matcher m = CODE.matcher(text);
        if (!m.matches()) return null;
        String country = m.group(1).toUpperCase(Locale.ROOT);
        if (!countryExists.test(country)) return null;
        double mhz = Double.parseDouble(m.group(2).replace(',', '.'));
        if (mhz < MIN_MHZ - 0.5 || mhz > MAX_MHZ + 0.5) return null;
        return new ParsedCode(country, slotForFrequency(mhz, slots));
    }

    public static int wrap(int i, int n) {
        if (n <= 0) return 0;
        return ((i % n) + n) % n;
    }

    /** Следующая позиция со станцией в направлении dir (+1/-1) с переходом через край. -1, если станций нет вообще. */
    public static int nextWithStation(int from, int dir, int slots, java.util.function.IntPredicate hasStation) {
        for (int k = 1; k <= slots; k++) {
            int s = wrap(from + dir * k, slots);
            if (hasStation.test(s)) return s;
        }
        return -1;
    }
}
