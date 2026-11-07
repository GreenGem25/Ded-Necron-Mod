package com.ded.necronmod.radio.net;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.radio.RadioSettings;
import com.ded.necronmod.radio.RadioSettingsCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/** Клиент -> сервер: игрок изменил настройки радио-блока. */
public record BlockSettingsPayload(BlockPos pos, RadioSettings settings) implements CustomPacketPayload {
    public static final Type<BlockSettingsPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "radio_block_settings"));

    public static final StreamCodec<ByteBuf, BlockSettingsPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BlockSettingsPayload::pos,
            RadioSettingsCodecs.STREAM_CODEC, BlockSettingsPayload::settings,
            BlockSettingsPayload::new);

    @Override public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}
