package com.ded.necronmod.radio;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sound.sampled.AudioFormat;

import com.ded.necronmod.DedNecronMod;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import net.minecraft.client.sounds.AudioStream;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

/**
 * Живой MP3-поток как AudioStream для звукового движка Minecraft.
 *
 * Поток данных:  HTTP (фоновый поток) -> JLayer -> моно или стерео 16 бит -> кольцевой буфер -> read() из звукового потока MC.
 *
 * Главные правила:
 *  - read() НИКОГДА не блокируется: если данных не хватает, добивает тишиной. Иначе зависнет звуковой поток
 *    всего Minecraft, а при опустошении очереди OpenAL источник остановится и движок «забудет» звук.
 *  - Future из start() завершается только когда накоплен запас (PREBUFFER_MS). Движок ждёт его асинхронно,
 *    а пока играет шум. Если все URL мертвы, future завершается ошибкой.
 *  - Переполненный буфер отбрасывает СТАРЫЕ данные (пауза игры не блокирует TCP и не копит задержку).
 */
public final class RadioAudioStream implements AudioStream {
    private static final int PREBUFFER_MS = 2500;       // > 2 с, потому что движок сразу забирает 2 куска по 1 с
    private static final int BUFFER_SECONDS = 10;
    private static final int MAX_RATE = 48_000;
    private static final long READY_TIMEOUT_MS = 20_000;
    private static final int MAX_DECODE_ERRORS = 100;
    private static final AtomicInteger IDS = new AtomicInteger();
    private static final ExecutorService CLEANUP = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "Radio-Cleanup");
        t.setDaemon(true);
        return t;
    });

    private final String name;
    private final List<String> urls;
    private final int channels; // 1 = моно (блок в мире), 2 = стерео (наушники)

    private final CompletableFuture<AudioStream> ready = new CompletableFuture<>();
    private final AtomicBoolean started = new AtomicBoolean();
    private final AtomicBoolean closed = new AtomicBoolean();
    private volatile boolean failed;
    private volatile boolean starving = true;
    private volatile AudioFormat format;
    private volatile Thread thread;
    private volatile HttpURLConnection connection;

    private final Object lock = new Object();
    private short[] ring;        // под lock
    private int head, count;     // под lock

    private int sampleRate;      // только поток декодера
    private final LoudnessNormalizer normalizer = new LoudnessNormalizer(); // только поток декодера
    private long framesDecoded;  // только поток декодера

    /** @param stereo true для наушников (стерео как есть), false для блока в мире (моно, чтобы работало затухание). */
    public RadioAudioStream(String name, List<String> urls, boolean stereo) {
        this.name = name;
        this.urls = List.copyOf(urls);
        this.channels = stereo ? 2 : 1;
    }

    // ---------------------------------------------------------------- публичный API

    /** Запускает подключение (один раз) и возвращает future, готовый к передаче движку. */
    public CompletableFuture<AudioStream> start() {
        if (!urls.isEmpty() && started.compareAndSet(false, true) && !closed.get()) {
            ready.orTimeout(READY_TIMEOUT_MS, TimeUnit.MILLISECONDS).whenComplete((s, err) -> {
                if (err != null) {
                    if (!(err instanceof CancellationException)) {
                        failed = true;
                        DedNecronMod.LOGGER.info("Radio '{}' unavailable: {}", name, err.toString());
                    }
                    close();
                }
            });
            Thread t = new Thread(this::run, "Radio-Decoder-" + IDS.incrementAndGet());
            t.setDaemon(true);
            thread = t;
            t.start();
        } else if (urls.isEmpty()) {
            failed = true;
            ready.completeExceptionally(new IOException("no urls"));
        }
        return ready;
    }

    public boolean isReady() { return ready.isDone() && !ready.isCompletedExceptionally(); }

    /** Не удалось подключиться ни к одному URL (не путать с закрытием вручную). */
    public boolean isFailed() { return failed; }

    /** Звук реально идёт: запас накоплен и последнее чтение не было «голодным». */
    public boolean isAudible() { return isReady() && !starving && !closed.get(); }

    // ---------------------------------------------------------------- AudioStream

    @Override
    public @NotNull AudioFormat getFormat() {
        AudioFormat f = format;
        return f != null ? f : new AudioFormat(44_100f, 16, channels, true, ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN);
    }

    @Override
    public @NotNull ByteBuffer read(int size) {
        int want = (size / 2) / channels * channels;   // отсчётов, кратно числу каналов (только целые кадры)
        ByteBuffer out = BufferUtils.createByteBuffer(want * 2).order(ByteOrder.nativeOrder()); // обнулён = тишина
        ShortBuffer shorts = out.asShortBuffer();
        int got = 0;
        synchronized (lock) {
            if (ring != null) {
                got = Math.min(count, want);
                got -= got % channels;
                for (int i = 0; i < got; i++) shorts.put(ring[(head + i) % ring.length]);
                head = (head + got) % ring.length;
                count -= got;
            }
        }
        starving = got < want / 2;
        return out;
    }

    @Override
    public void close() {
        if (!closed.compareAndSet(false, true)) return;
        ready.cancel(false);
        synchronized (lock) {
            ring = null;
            count = 0;
        }
        // Блокирующее закрытие сети выносим из главного потока (disconnect() по HTTPS может занять сотни мс).
        Thread t = thread;
        HttpURLConnection c = connection;
        CLEANUP.execute(() -> {
            if (t != null) t.interrupt();
            if (c != null) c.disconnect();
        });
    }

    // ---------------------------------------------------------------- поток декодера

    private void run() {
        int attempt = 0;
        int failuresInRow = 0;
        while (!closed.get()) {
            String url = urls.get(attempt++ % urls.size());
            long before = framesDecoded;
            try {
                decodeFrom(url);
            } catch (IOException | RuntimeException e) {
                if (closed.get()) break;
                DedNecronMod.LOGGER.debug("Radio '{}' ({}): {}", name, url, e.toString());
            }
            if (closed.get()) break;

            failuresInRow = framesDecoded > before ? 0 : failuresInRow + 1;
            if (!ready.isDone() && failuresInRow >= Math.max(2, urls.size())) {
                ready.completeExceptionally(new IOException("no working URL"));
                break;
            }
            try {
                Thread.sleep(Math.min(10_000L, 400L << Math.min(failuresInRow, 5)));
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void decodeFrom(String url) throws IOException {
        try (InputStream in = RadioHttp.open(url, c -> {
            connection = c;
            if (closed.get()) c.disconnect();
        })) {
            Bitstream bitstream = new Bitstream(in);
            Decoder decoder = new Decoder();
            int errors = 0;
            while (!closed.get()) {
                Header header;
                try {
                    header = bitstream.readFrame();
                } catch (JavaLayerException e) {
                    if (++errors > MAX_DECODE_ERRORS) throw new IOException("bitstream errors", e);
                    continue;
                }
                if (header == null) return; // конец потока: вернёмся в run() и переподключимся
                try {
                    SampleBuffer out = (SampleBuffer) decoder.decodeFrame(header, bitstream);
                    errors = 0;
                    push(out.getBuffer(), out.getBufferLength(), out.getChannelCount(), header.frequency());
                } catch (JavaLayerException | RuntimeException e) {
                    if (++errors > MAX_DECODE_ERRORS) throw new IOException("decode errors", e);
                } finally {
                    bitstream.closeFrame();
                }
            }
        }
    }

    /** Приводит к нужному числу каналов (моно: среднее L и R; стерео: как есть, моно-источник дублируется). */
    private void push(short[] pcm, int len, int srcChannels, int rate) {
        int n = len / srcChannels;
        short[] frames = new short[n * channels];
        for (int i = 0; i < n; i++) {
            if (channels == 1) {
                frames[i] = srcChannels == 1 ? pcm[i] : (short) ((pcm[2 * i] + pcm[2 * i + 1]) >> 1);
            } else if (srcChannels == 1) {
                frames[2 * i] = frames[2 * i + 1] = pcm[i];
            } else {
                frames[2 * i] = pcm[2 * i];
                frames[2 * i + 1] = pcm[2 * i + 1];
            }
        }

        if (sampleRate == 0) {
            sampleRate = rate;
            format = new AudioFormat(rate, 16, channels, true, ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN);
        } else if (rate != sampleRate) {
            frames = resample(frames, channels, rate, sampleRate); // редко: станция сменила частоту дискретизации
        }
        normalizer.process(frames, channels, sampleRate);          // выравниваем громкость станции ДО буфера

        long bufferedFrames;
        synchronized (lock) {
            if (closed.get()) return;
            if (ring == null) ring = new short[MAX_RATE * BUFFER_SECONDS * channels];
            for (int i = 0; i < frames.length; i += channels) {
                if (count + channels > ring.length) { // переполнение: выбрасываем самый старый КАДР
                    head = (head + channels) % ring.length;
                    count -= channels;
                }
                for (int c = 0; c < channels; c++) {
                    ring[(head + count) % ring.length] = frames[i + c];
                    count++;
                }
            }
            bufferedFrames = count / channels;
        }
        framesDecoded++;

        if (!ready.isDone() && bufferedFrames >= (long) sampleRate * PREBUFFER_MS / 1000) {
            starving = false;
            ready.complete(this);
        }
    }

    private static short[] resample(short[] in, int ch, int from, int to) {
        int inFrames = in.length / ch;
        int outFrames = (int) ((long) inFrames * to / from);
        short[] out = new short[outFrames * ch];
        for (int i = 0; i < outFrames; i++) {
            double pos = (double) i * from / to;
            int i0 = (int) pos;
            int i1 = Math.min(i0 + 1, inFrames - 1);
            double frac = pos - i0;
            for (int c = 0; c < ch; c++) {
                out[i * ch + c] = (short) (in[i0 * ch + c] + (in[i1 * ch + c] - in[i0 * ch + c]) * frac);
            }
        }
        return out;
    }
}
