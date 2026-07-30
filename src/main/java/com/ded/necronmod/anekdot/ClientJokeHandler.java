package com.ded.necronmod.anekdot;

import com.mojang.text2speech.Narrator;
import net.minecraft.Util;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientJokeHandler {

    public static void handleJokePayload(PlayJokePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            String text = payload.jokeText().replace("\n", " ");

            if (Util.getPlatform() == Util.OS.WINDOWS) {
                NativeTTS.speakRussian(text);
            } else {
                Narrator.getNarrator().say(text, true);
            }
        });
    }
}