package com.ded.necronmod.radio.net;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.radio.RadioSettings;
import com.ded.necronmod.radio.RadioSettingsCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/** Клиент -> сервер: игрок изменил настройки переносного радио, которое держит в руке. */
public record ItemSettingsPayload(boolean offhand, RadioSettings settings) implements CustomPacketPayload {
    public static final Type<ItemSettingsPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "radio_item_settings"));

    public static final StreamCodec<ByteBuf, ItemSettingsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ItemSettingsPayload::offhand,
            RadioSettingsCodecs.STREAM_CODEC, ItemSettingsPayload::settings,
            ItemSettingsPayload::new);

    @Override public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}
