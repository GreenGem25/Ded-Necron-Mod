package com.ded.necronmod.anekdot;

import com.ded.necronmod.DedNecronMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PlayJokePayload(String jokeText) implements CustomPacketPayload {

    public static final Type<PlayJokePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "play_joke"));

    public static final StreamCodec<FriendlyByteBuf, PlayJokePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            PlayJokePayload::jokeText,
            PlayJokePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}