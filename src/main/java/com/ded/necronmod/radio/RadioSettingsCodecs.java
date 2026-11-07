package com.ded.necronmod.radio;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/** Кодеки RadioSettings: для сохранения (NBT, компонент предмета) и для сети. */
public final class RadioSettingsCodecs {
    public static final Codec<RadioSettings> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("country").forGetter(RadioSettings::country),
            Codec.INT.fieldOf("slot").forGetter(RadioSettings::slot),
            Codec.FLOAT.fieldOf("volume").forGetter(RadioSettings::volume),
            Codec.BOOL.fieldOf("playing").forGetter(RadioSettings::playing),
            Codec.BOOL.fieldOf("speaker").forGetter(RadioSettings::speaker)
    ).apply(i, RadioSettings::new));

    /** Строка страны ограничена 8 символами: больший пакет отклоняется ещё при чтении. */
    public static final StreamCodec<ByteBuf, RadioSettings> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(8), RadioSettings::country,
            ByteBufCodecs.VAR_INT, RadioSettings::slot,
            ByteBufCodecs.FLOAT, RadioSettings::volume,
            ByteBufCodecs.BOOL, RadioSettings::playing,
            ByteBufCodecs.BOOL, RadioSettings::speaker,
            RadioSettings::new);

    private RadioSettingsCodecs() {}
}
