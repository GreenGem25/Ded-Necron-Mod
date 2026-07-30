package com.ded.necronmod.anekdot;

import com.ded.necronmod.DedNecronMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record StopJokePayload() implements CustomPacketPayload {

    public static final Type<StopJokePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "stop_joke"));

    public static final StreamCodec<FriendlyByteBuf, StopJokePayload> STREAM_CODEC =
            StreamCodec.unit(new StopJokePayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}