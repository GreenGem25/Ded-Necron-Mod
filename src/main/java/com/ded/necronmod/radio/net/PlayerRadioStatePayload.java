package com.ded.necronmod.radio.net;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.radio.RadioSettings;
import com.ded.necronmod.radio.RadioSettingsCodecs;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/** Сервер -> клиент: у игрока включился, изменился или выключился «динамик» переносного радио (RadioSettings.OFF = выключился). */
public record PlayerRadioStatePayload(UUID player, RadioSettings settings) implements CustomPacketPayload {
    public static final Type<PlayerRadioStatePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "radio_player_state"));

    public static final StreamCodec<ByteBuf, PlayerRadioStatePayload> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, PlayerRadioStatePayload::player,
            RadioSettingsCodecs.STREAM_CODEC, PlayerRadioStatePayload::settings,
            PlayerRadioStatePayload::new);

    @Override public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}
