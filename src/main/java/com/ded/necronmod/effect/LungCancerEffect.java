package com.ded.necronmod.effect;

import com.ded.necronmod.init.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class LungCancerEffect extends MobEffect {

    public LungCancerEffect() {
        // Указываем категорию (вредный/полезный/нейтральный) и цвет частиц в формате HEX
        super(MobEffectCategory.HARMFUL, 0x4A0E4E);
    }

    // Говорим игре, что этот эффект ДОЛЖЕН совершать действия каждый тик
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // Проверяем, что мы на стороне сервера, чтобы звук синхронизировался для всех игроков вокруг
        if (!entity.level().isClientSide()) {
            RandomSource random = entity.getRandom();

            // Задаем шанс. random.nextFloat() выдает число от 0.0 до 1.0.
            // 0.005F означает шанс примерно 0.5% каждый тик.
            // В среднем звук будет играть раз в 200 тиков (10 секунд).
            if (random.nextFloat() < 0.005F) {

                // Воспроизводим звук на позиции существа
                entity.level().playSound(
                        null, // null означает, что звук услышит и сам виновник, и все вокруг
                        entity.getX(), entity.getY(), entity.getZ(),
                        ModSounds.COUGHING_SOUND.get(), // Ванильный звук пещеры (можно заменить на свой)
                        SoundSource.PLAYERS, // Категория звука в настройках микшера
                        1.0F, // Громкость
                        // Случайная тональность (pitch), чтобы звук не надоедал одинаковым звучанием
                        0.8F + random.nextFloat() * 0.4F
                );
            }
        }
        return true;
    }
}