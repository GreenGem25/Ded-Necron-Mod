package com.ded.necronmod.sound;

import com.ded.necronmod.block.entity.TowerTopBlockEntity;
import com.ded.necronmod.init.ModItems;
import com.ded.necronmod.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;

public class BrainrotSoundInstance extends AbstractTickableSoundInstance {
    private static final float HEARING_DISTANCE = 30F;
    private static final float UNLOAD_DISTANCE = 50F;
    private static final int FADE_OUT_TICKS = 20;

    private final TowerTopBlockEntity antenna;
    private final int maxTicks;
    private int ticksPlayed = 0;

    public BrainrotSoundInstance(TowerTopBlockEntity antenna, SoundEvent soundEvent, int durationTicks) {
        super(soundEvent, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());

        this.antenna = antenna;
        this.maxTicks = durationTicks;

        this.looping = true;
        this.delay = 0;

        this.volume = 0;
        this.pitch = 1F;

        this.relative = false;

        updatePosition();
    }

    @Override
    public void tick() {
        if (antenna.isRemoved()) {
            stop();
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null) {
            stop();
            return;
        }

        updatePosition();

        double distance = player.position().distanceTo(Vec3.atCenterOf(antenna.getBlockPos()));

        if (distance >= UNLOAD_DISTANCE) {
            stop();
            return;
        }

        if (distance >= HEARING_DISTANCE) {
            volume = 0.0F;
            return;
        }

        if (player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.TIN_FOIL_HAT) || player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.NECRODERMIS_HELMET)) {
            volume = 0.0F;
            return;
        }

        float t = Mth.clamp((float)(distance / HEARING_DISTANCE), 0F, 1F);

        float baseVolume = 1F - (t * t);
        int remaining = maxTicks - ticksPlayed;
        float fade = remaining < FADE_OUT_TICKS ? (float) remaining / FADE_OUT_TICKS : 1.0F;

        volume = baseVolume * fade;

        pitch = 1.0F;

        ticksPlayed++;
        if (ticksPlayed >= maxTicks) {
            stop(); // менеджер увидит isStopped() и запустит следующий
        }
    }

    private void updatePosition() {
        x = antenna.getBlockPos().getX() + 0.5;
        y = antenna.getBlockPos().getY() + 0.5;
        z = antenna.getBlockPos().getZ() + 0.5;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }
}