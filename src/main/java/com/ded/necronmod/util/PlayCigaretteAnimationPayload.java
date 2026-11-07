package com.ded.necronmod.util;

import com.ded.necronmod.DedNecronMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PlayCigaretteAnimationPayload() implements CustomPacketPayload {

    public static final Type<PlayCigaretteAnimationPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "play_cigarette_animation")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayCigaretteAnimationPayload> STREAM_CODEC =
            StreamCodec.unit(new PlayCigaretteAnimationPayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}