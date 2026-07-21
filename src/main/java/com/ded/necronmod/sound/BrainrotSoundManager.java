package com.ded.necronmod.sound;

import com.ded.necronmod.block.entity.TowerTopBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class BrainrotSoundManager {
    private static final Map<BlockPos, BrainrotSoundInstance> SOUNDS
            = new HashMap<>();

    public static void tick(TowerTopBlockEntity be) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null)
            return;

        BrainrotSoundInstance sound = SOUNDS.get(be.getBlockPos());

        if (sound != null && sound.isStopped()) {
            SOUNDS.remove(be.getBlockPos());
            sound = null;
        }

        double distance = mc.player.position()
                .distanceTo(Vec3.atCenterOf(be.getBlockPos()));

        if (distance <= 30 && sound == null) {

            sound = new BrainrotSoundInstance(be);

            SOUNDS.put(be.getBlockPos(), sound);

            mc.getSoundManager().play(sound);
        }
    }
}
