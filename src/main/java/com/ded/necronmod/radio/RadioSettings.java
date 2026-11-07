package com.ded.necronmod.radio;

import java.util.regex.Pattern;

/**
 * Настройки радио: общие для блока и переносного предмета, экрана и пакетов.
 * Не зависит от Minecraft (кодеки лежат в RadioSettingsCodecs), поэтому логику проверки можно тестировать отдельно.
 *
 * @param country код страны ("NL"), пустая строка = не выбрана
 * @param slot    позиция второй ручки
 * @param volume  0..1
 * @param playing включено
 * @param speaker только для переносного радио: true = динамик (слышно другим), false = наушники
 */
public record RadioSettings(String country, int slot, float volume, boolean playing, boolean speaker) {
    /** Наибольшая допустимая позиция. Реальное число позиций знает только клиент (stations.json), сервер проверяет границу «с запасом». */
    public static final int MAX_SLOT = 63;

    public static final RadioSettings DEFAULT = new RadioSettings("", 0, 0.7f, false, false);
    /** «Динамик выключен»: рассылается другим игрокам, когда у игрока больше нет играющего динамика. */
    public static final RadioSettings OFF = new RadioSettings("", 0, 0f, false, false);

    private static final Pattern COUNTRY = Pattern.compile("[A-Z]{2}");

    public RadioSettings withTuning(String country, int slot) { return new RadioSettings(country, slot, volume, playing, speaker); }
    public RadioSettings withVolume(float volume) { return new RadioSettings(country, slot, volume, playing, speaker); }
    public RadioSettings withPlaying(boolean playing) { return new RadioSettings(country, slot, volume, playing, speaker); }
    public RadioSettings withSpeaker(boolean speaker) { return new RadioSettings(country, slot, volume, playing, speaker); }

    /**
     * Приводит ЧУЖИЕ данные (из пакета или с диска) к допустимым. Сервер никогда не доверяет клиенту.
     * @param allowSpeaker false для блока: у него нет режима «наушники/динамик»
     */
    public RadioSettings sanitized(boolean allowSpeaker) {
        String c = country == null || !COUNTRY.matcher(country).matches() ? "" : country;
        int s = Math.max(0, Math.min(MAX_SLOT, slot));
        float v = Float.isNaN(volume) ? 0f : Math.max(0f, Math.min(1f, volume));
        return new RadioSettings(c, s, v, playing, allowSpeaker && speaker);
    }
}
