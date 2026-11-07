package com.ded.necronmod.radio;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;

import com.ded.necronmod.init.ModSounds;
import com.ded.necronmod.radio.data.RadioStation;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * Звук одной станции. Данные берёт не из ogg, а из RadioAudioStream.
 * POSITIONAL: моно в точке блока или за игроком (динамик переносного радио), с затуханием.
 * HEADPHONES: стерео «в ушах» владельца, без затухания и панорамы.
 */
public final class RadioSoundInstance extends AbstractTickableSoundInstance {
    private static final float STEP_IN = 0.06f;  // ~0.8 с
    private static final float STEP_OUT = 0.10f; // ~0.5 с

    private final String stationId;
    private final RadioAudioStream stream;
    private float baseVolume;
    private float fade;
    private boolean retiring;
    private int ticksSeen;
    private boolean engineStarted; // передан ли звук движку (канал выделяется только в этот момент)
    @Nullable private final Entity entity; // за кем следует звук (динамик переносного радио), иначе null

    /** Блок в мире. */
    public static RadioSoundInstance positional(BlockPos pos, RadioStation station, float baseVolume) {
        return new RadioSoundInstance(RadioMode.POSITIONAL, pos, null, station, baseVolume);
    }

    /** Собственное радио игрока: стерео в ушах (и для «наушников», и для «динамика»: свой динамик слышен как наушники). */
    public static RadioSoundInstance headphones(RadioStation station, float baseVolume) {
        return new RadioSoundInstance(RadioMode.HEADPHONES, null, null, station, baseVolume);
    }

    /** Чужое переносное радио в режиме «динамик»: звук следует за игроком, меньшая дальность. */
    public static RadioSoundInstance following(Entity entity, RadioStation station, float baseVolume) {
        return new RadioSoundInstance(RadioMode.POSITIONAL, null, entity, station, baseVolume);
    }

    private RadioSoundInstance(RadioMode mode, @Nullable BlockPos pos, @Nullable Entity entity,
                               RadioStation station, float baseVolume) {
        super(entity != null ? ModSounds.RADIO_STREAM_PORTABLE.get() : ModSounds.RADIO_STREAM.get(),
                SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.entity = entity;
        this.stationId = station.id();
        this.stream = new RadioAudioStream(station.name(), station.urls(), mode == RadioMode.HEADPHONES);
        this.baseVolume = baseVolume;
        this.looping = false;
        this.delay = 0;
        this.volume = 0f;
        if (mode == RadioMode.HEADPHONES) {
            this.relative = true;
            this.attenuation = Attenuation.NONE;
            this.x = 0;
            this.y = 0;
            this.z = 0;
        } else if (entity != null) {
            this.relative = false;
            this.attenuation = Attenuation.LINEAR;
            followEntity();
        } else {
            this.relative = false;
            this.attenuation = Attenuation.LINEAR;
            assert pos != null;
            this.x = pos.getX() + 0.5;
            this.y = pos.getY() + 0.5;
            this.z = pos.getZ() + 0.5;
        }
    }

    /** Движок читает getX/Y/Z каждый тик, поэтому достаточно обновлять поля в tick() (как в ванильных звуках сущностей). */
    private void followEntity() {
        assert entity != null;
        this.x = entity.getX();
        this.y = entity.getY() + 1.0; // примерно уровень груди
        this.z = entity.getZ();
    }

    @Override public boolean canStartSilent() { return true; }

    /** Начинает подключение к станции в фоне. Движку звук НЕ отдаётся: см. playIn(). */
    public void connect() { stream.start(); }

    /**
     * Передаёт звук движку. Вызывать только когда isReady(), то есть поток уже подключён и буфер накоплен.
     * Почему не раньше: play() сразу занимает один из 8 стриминговых каналов движка, а если звук остановить,
     * пока он не начал играть (ещё подключается, станция мертва, игрок переключил ручку), канал НЕ освобождается и
     * утекает навсегда. Восемь таких утечек, и стриминг в игре не работает вообще, в том числе ваниль.
     */
    public void playIn(SoundManager sm) {
        engineStarted = true;
        sm.play(this);
    }

    /** Движок вызывает это вместо чтения ogg. К этому моменту поток уже готов, future завершён. */
    @Override
    public @NotNull CompletableFuture<AudioStream> getStream(@NotNull SoundBufferLibrary buffers, @NotNull Sound sound, boolean looping) {
        return stream.start();
    }

    @Override
    public void tick() {
        ticksSeen++;
        if (entity != null) {
            if (entity.isRemoved()) { shutdown(); return; } // игрок вышел из зоны видимости или из игры
            followEntity();
        }
        if (stream.isFailed()) { shutdown(); return; }
        float target = (retiring || !stream.isReady()) ? 0f : 1f;
        fade = target > fade ? Math.min(target, fade + STEP_IN) : Math.max(target, fade - STEP_OUT);
        volume = Mth.clamp(baseVolume * fade, 0f, 1f);
        if (retiring && fade <= 0f) shutdown();
    }

    /** Остановить и ОБЯЗАТЕЛЬНО закрыть поток: движок мог так и не успеть его подключить. */
    public void shutdown() {
        stop();
        stream.close();
    }

    /** Станция сменилась: плавно затихаем (если ещё не звучали, гасим сразу). */
    public void retire() {
        retiring = true;
        if (!stream.isReady()) shutdown();
    }

    public String stationId() { return stationId; }
    public boolean isFailed() { return stream.isFailed(); }
    public boolean isReady() { return stream.isReady(); }
    public boolean isEngineStarted() { return engineStarted; }
    /** Реально звучит: передан движку, запас накоплен и последнее чтение не «голодное». */
    public boolean isAudible() { return engineStarted && stream.isAudible(); }
    public void setBaseVolume(float v) { this.baseVolume = v; }
    /** 0 = движок ни разу не вызвал tick(): звук не приняли (например, нет свободных каналов). */
    public int ticksSeen() { return ticksSeen; }
}
