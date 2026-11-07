package com.ded.necronmod.radio.data;

import javax.annotation.Nullable;
import java.util.List;

public final class RadioData {
    private static volatile List<RadioCountry> countries = List.of();
    private static volatile int slotsPerCountry = 16;

    private RadioData() {}

    /** Вызывается загрузчиком: slots берётся из поля "slotsPerCountry" корня stations.json. */
    public static void set(List<RadioCountry> c, int slots) {
        countries = c;
        slotsPerCountry = Math.max(2, slots);
    }

    public static List<RadioCountry> countries() { return countries; }
    public static int slotsPerCountry() { return slotsPerCountry; }

    @Nullable
    public static RadioCountry byCode(String code) {
        for (RadioCountry c : countries) if (c.code().equals(code)) return c;
        return null;
    }
}
