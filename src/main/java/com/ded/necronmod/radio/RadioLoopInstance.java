package com.ded.necronmod.radio;

import javax.annotation.Nullable;

import com.ded.necronmod.init.ModSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/**
 * Зацикленный локальный звук (из ogg, без сети): шум помех или мелодия-затычка.
 * Плавно появляется и исчезает; когда уходит в ноль и цель = 0, останавливается сам.
 */
public final class RadioLoopInstance extends AbstractTickableSoundInstance {
    public enum Loop {
        /** Шум помех: тише станций. */
        NOISE(0.35f),
        /** Мелодия-затычка: файл уже подогнан под уровень станций после AGC, поэтому без ослабления. */
        MELODY(1.0f);

        final float gain;

        Loop(float gain) { this.gain = gain; }

        /** portable = звук следует за игроком (динамик): меньшая дальность. */
        SoundEvent event(boolean portable) {
            return switch (this) {
                case NOISE -> portable ? ModSounds.RADIO_NOISE_PORTABLE.get() : ModSounds.RADIO_NOISE.get();
                case MELODY -> portable ? ModSounds.RADIO_MELODY_PORTABLE.get() : ModSounds.RADIO_MELODY.get();
            };
        }
    }

    private static final float STEP_UP = 0.12f;   // ~0.4 с
    private static final float STEP_DOWN = 0.04f; // ~1.25 с: перекрывается с появлением станции

    private final Loop loop;
    @Nullable private final Entity entity;
    private float baseVolume;
    private float target = 1f;
    private float level;

    public static RadioLoopInstance positional(Loop loop, BlockPos pos, float baseVolume) {
        return new RadioLoopInstance(loop, RadioMode.POSITIONAL, pos, null, baseVolume);
    }

    public static RadioLoopInstance headphones(Loop loop, float baseVolume) {
        return new RadioLoopInstance(loop, RadioMode.HEADPHONES, null, null, baseVolume);
    }

    /** Звук чужого переносного радио: следует за игроком. */
    public static RadioLoopInstance following(Loop loop, Entity entity, float baseVolume) {
        return new RadioLoopInstance(loop, RadioMode.POSITIONAL, null, entity, baseVolume);
    }

    private RadioLoopInstance(Loop loop, RadioMode mode, @Nullable BlockPos pos, @Nullable Entity entity, float baseVolume) {
        super(loop.event(entity != null), SoundSource.RECORDS, SoundInstance.createUnseededRandom());
        this.loop = loop;
        this.entity = entity;
        this.looping = true;
        this.delay = 0;
        this.baseVolume = baseVolume;
        this.volume = 0f;
        if (mode == RadioMode.HEADPHONES) {
            // как у ванильной фоновой музыки: источник «в голове» слушателя, без затухания и панорамы
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

    /** Без этого движок не запустит звук с громкостью 0 (а мы стартуем с нуля и плавно нарастаем). */
    @Override public boolean canStartSilent() { return true; }

    private void followEntity() {
        assert entity != null;
        this.x = entity.getX();
        this.y = entity.getY() + 1.0;
        this.z = entity.getZ();
    }

    public Loop loop() { return loop; }
    public void setBaseVolume(float v) { this.baseVolume = v; }
    public void setTarget(float t) { this.target = t; }

    @Override
    public void tick() {
        if (entity != null) {
            if (entity.isRemoved()) { stop(); return; }
            followEntity();
        }
        level = target > level
                ? Math.min(target, level + STEP_UP)
                : Math.max(target, level - STEP_DOWN);
        volume = Mth.clamp(baseVolume * loop.gain * level, 0f, 1f);
        if (target <= 0f && level <= 0f) stop();
    }
}
