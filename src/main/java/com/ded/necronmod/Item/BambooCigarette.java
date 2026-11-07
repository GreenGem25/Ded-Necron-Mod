package com.ded.necronmod.item;

import com.ded.necronmod.entity.CigarFlyEntity;
import com.ded.necronmod.init.ModEffects;
import com.ded.necronmod.init.ModEntities;
import com.ded.necronmod.init.ModSounds;
import com.ded.necronmod.util.FrameAnimationManager;
import com.ded.necronmod.util.PlayCigaretteAnimationPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BambooCigarette extends Item {

    public BambooCigarette(Item.Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            // Звук зажигалки играет в любом случае
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.LIGHTER, SoundSource.PLAYERS, 1.0F, 1.0F);

            // --- Ветка 1: выпала бабочка (25%) ---
            if (player.getRandom().nextFloat() < 0.25F) {
                CigarFlyEntity cigarFly = new CigarFlyEntity(ModEntities.CIGARFLY.get(), level);
                cigarFly.setPos(player.getX(), player.getY(), player.getZ());
                level.addFreshEntity(cigarFly);

                // Пакет НЕ отправляется → анимация не запускается
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }

            // --- Ветка 2: обычный исход (75%) ---
            player.getCooldowns().addCooldown(stack.getItem(), 600);

            if (player.hasEffect(ModEffects.LUNG_CANCER_EFFECT)) {
                int amplifier = Objects.requireNonNull(player.getEffect(ModEffects.LUNG_CANCER_EFFECT)).getAmplifier();
                player.addEffect(new MobEffectInstance(ModEffects.LUNG_CANCER_EFFECT, 2400,
                        Math.min(amplifier + 1, 255), false, false, true));
            } else if (player.getRandom().nextFloat() < 0.1F) {
                player.addEffect(new MobEffectInstance(ModEffects.LUNG_CANCER_EFFECT, 1200,
                        0, false, false, true));
            }

            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 3,
                    false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 600, 0,
                    false, false, true));

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.BHOP_MUSIC, SoundSource.PLAYERS, 0.3F, 1.0F);

            // Только здесь отправляем клиенту команду «запускай анимацию»
            if (player instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new PlayCigaretteAnimationPayload());
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}