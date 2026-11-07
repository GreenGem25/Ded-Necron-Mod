package com.ded.necronmod.radio.data;

import javax.annotation.Nullable;
import java.time.ZoneId;
import java.util.Map;

public record RadioCountry(String code, String name, ZoneId zone,
                           Map<Integer, RadioStation> slots) {
    @Nullable
    public RadioStation stationAt(int slot) { return slots.get(slot); } // null = шум
}