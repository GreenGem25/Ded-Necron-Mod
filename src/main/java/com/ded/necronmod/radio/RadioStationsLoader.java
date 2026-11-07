package com.ded.necronmod.radio;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.radio.data.RadioCountry;
import com.ded.necronmod.radio.data.RadioData;
import com.ded.necronmod.radio.data.RadioStation;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.Reader;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

public class RadioStationsLoader extends SimplePreparableReloadListener<List<RadioCountry>> {
    private static final ResourceLocation FILE =
            ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "radio/stations.json");

    private int slots = 16;

    @Override
    protected @NotNull List<RadioCountry> prepare(ResourceManager rm, @NotNull ProfilerFiller profiler) {
        List<RadioCountry> result = new ArrayList<>();
        Optional<Resource> res = rm.getResource(FILE);
        if (res.isEmpty()) { DedNecronMod.LOGGER.warn("stations.json not found"); return result; }

        try (Reader reader = res.get().openAsReader()) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            slots = root.has("slotsPerCountry") ? root.get("slotsPerCountry").getAsInt() : 16;
            for (JsonElement ce : root.getAsJsonArray("countries")) {
                JsonObject c = ce.getAsJsonObject();
                Map<Integer, RadioStation> slots = new HashMap<>();
                for (var e : c.getAsJsonObject("slots").entrySet()) {
                    RadioStation st = parseStation(e.getValue().getAsJsonObject());
                    if (st != null) slots.put(Integer.parseInt(e.getKey()), st);
                }
                result.add(new RadioCountry(
                        c.get("code").getAsString(),
                        c.get("name").getAsString(),
                        parseZone(c.get("timezone").getAsString()),
                        Map.copyOf(slots)));
            }
        } catch (Exception ex) {
            DedNecronMod.LOGGER.error("Failed to load stations.json", ex);
        }
        return result;
    }

    @Nullable
    private static RadioStation parseStation(JsonObject o) {
        List<String> urls = new ArrayList<>();
        for (JsonElement u : o.getAsJsonArray("urls")) {
            String s = u.getAsString();
            if (s.startsWith("http://") || s.startsWith("https://")) urls.add(s);
        }
        if (urls.isEmpty()) return null; // позиция станет шумом
        return new RadioStation(
                o.get("id").getAsString(), o.get("name").getAsString(),
                str(o, "region"), str(o, "genre"), str(o, "tier"),
                List.copyOf(urls),
                o.has("noise") ? o.get("noise").getAsFloat() : 0f);
    }

    private static String str(JsonObject o, String k) {
        return o.has(k) ? o.get(k).getAsString() : "";
    }

    private static ZoneId parseZone(String id) {
        try { return ZoneId.of(id); } catch (DateTimeException e) { return ZoneOffset.UTC; }
    }

    @Override
    protected void apply(@NotNull List<RadioCountry> data, @NotNull ResourceManager rm, @NotNull ProfilerFiller profiler) {
        RadioData.set(data, slots);
    }
}