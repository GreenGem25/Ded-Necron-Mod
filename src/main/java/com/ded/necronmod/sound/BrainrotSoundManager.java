package com.ded.necronmod.sound;

import com.ded.necronmod.block.entity.TowerTopBlockEntity;
import com.ded.necronmod.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrainrotSoundManager {
    private static final Map<BlockPos, BrainrotSoundInstance> SOUNDS
            = new HashMap<>();

    private static final Map<ResourceLocation, Integer> DURATIONS = Map.of(
            ModSounds.BRAINROT_SOUND.getId(),   20 * 12,
            ModSounds.BRAINROT_SOUND_2.getId(), 20 * (3 * 60 + 32),
            ModSounds.BRAINROT_SOUND_3.getId(), 20 * (2 * 60 + 3)
    );

    private static final List<SoundEvent> SOUND_POOL = List.of(
            ModSounds.BRAINROT_SOUND.get(),
            ModSounds.BRAINROT_SOUND_2.get(),
            ModSounds.BRAINROT_SOUND_3.get()
    );

    private static final int DEFAULT_DURATION = 20 * 60;

    public static void tick(TowerTopBlockEntity be) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null) {
            return;
        }

        BlockPos pos = be.getBlockPos();
        BrainrotSoundInstance sound = SOUNDS.get(pos);

        if (sound != null && sound.isStopped()) {
            SOUNDS.remove(pos);
            sound = null;
        }

        double distance = mc.player.position().distanceTo(Vec3.atCenterOf(pos));

        if (distance <= 30 && sound == null) {
            SoundEvent chosen = SOUND_POOL.get(mc.level.random.nextInt(SOUND_POOL.size()));
            int dur = DURATIONS.getOrDefault(chosen.getLocation(), DEFAULT_DURATION);

            sound = new BrainrotSoundInstance(be, chosen, dur);
            SOUNDS.put(pos, sound);
            mc.getSoundManager().play(sound);
        }
    }
}
