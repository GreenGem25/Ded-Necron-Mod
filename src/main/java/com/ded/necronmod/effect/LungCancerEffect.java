package com.ded.necronmod.effect;

import com.ded.necronmod.init.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class LungCancerEffect extends MobEffect {

    public LungCancerEffect() {
        super(MobEffectCategory.HARMFUL, 0x4A0E4E);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide()) {
            RandomSource random = entity.getRandom();

            if (random.nextFloat() < 0.005F) {

                entity.level().playSound(
                        null,
                        entity.getX(), entity.getY(), entity.getZ(),
                        ModSounds.COUGHING_SOUND.get(),
                        SoundSource.PLAYERS,
                        1.0F,
                        0.8F + random.nextFloat() * 0.4F
                );
            }
        }
        return true;
    }
}