package com.ded.necronmod.radio;

/**
 * Автоматическая регулировка громкости (AGC) для живого потока.
 *
 * Измеряет сглаженный уровень (RMS по всем каналам) и подводит его к TARGET_RMS: тихие станции подтягиваются,
 * громкие приглушаются. Усиление одинаковое для всех каналов, поэтому стерео-баланс не меняется.
 * Затем мягкий лимитер срезает пики, чтобы после усиления не было щелчков клиппинга.
 * Работает по времени АУДИО (по числу кадров), а не по часам.
 *
 * Громкость, которую задаёт игрок (ручка), применяется ПОСЛЕ этого, на уровне источника OpenAL.
 * Состояние хранится в потоке декодера, синхронизация не нужна.
 */
final class LoudnessNormalizer {
    static final double TARGET_RMS = 0.18;      // около -15 дБFS. Выше нельзя: громкость источника в MC не больше 1.0
    static final double MIN_GAIN = 0.2;         // максимум -14 дБ на громких станциях
    static final double MAX_GAIN = 10.0;        // максимум +20 дБ на тихих (дальше растёт только шум)
    static final double SILENCE_RMS = 0.003;    // тише (около -50 дБFS) считаем паузой: уровень не трогаем
    static final double RISE_TAU = 0.25;        // быстро реагируем на «стало громче»
    static final double FALL_TAU = 3.0;         // медленно признаём, что стало тише (нет «накачки» между песнями)
    static final double KNEE = 0.80;            // с этого уровня начинается мягкое ограничение

    private double energy = -1;                 // сглаженная энергия (RMS^2), -1 = ещё не измеряли
    private double gain = 1.0;

    /** Обрабатывает кадр на месте. pcm: чередующиеся отсчёты (L R L R ... для стерео). */
    void process(short[] pcm, int channels, int rate) {
        int frames = pcm.length / channels;
        if (frames == 0) return;

        double sum = 0;
        for (short s : pcm) {
            double x = s / 32768.0;
            sum += x * x;
        }
        double ms = sum / pcm.length;
        double dt = (double) frames / rate;
        double previous = gain;

        if (ms > SILENCE_RMS * SILENCE_RMS) {
            if (energy < 0) {
                energy = ms;
            } else {
                double tau = ms > energy ? RISE_TAU : FALL_TAU;
                energy += (ms - energy) * (1 - Math.exp(-dt / tau));
            }
            double rms = Math.sqrt(energy);
            gain = Math.min(MAX_GAIN, Math.max(MIN_GAIN, TARGET_RMS / rms));
        } // иначе пауза: держим прежнее усиление и не обновляем уровень

        for (int i = 0; i < pcm.length; i++) {
            double g = previous + (gain - previous) * ((double) i / channels + 1) / frames; // плавно, без «ступенек»
            double y = limit(pcm[i] / 32768.0 * g);
            pcm[i] = (short) Math.round(y * 32767.0);
        }
    }

    private static double limit(double x) {
        double a = Math.abs(x);
        if (a <= KNEE) return x;
        double y = KNEE + (1 - KNEE) * Math.tanh((a - KNEE) / (1 - KNEE));
        return x < 0 ? -y : y;
    }
}
