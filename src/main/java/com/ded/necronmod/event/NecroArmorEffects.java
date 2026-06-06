package com.ded.necronmod.event;

import com.ded.necronmod.Config;
import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.init.ModItems;
import com.ded.necronmod.init.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = DedNecronMod.MODID)
public class NecroArmorEffects {
    @SubscribeEvent
    public static void onPlayerDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (hasFullSetOfArmor(player)) {
                float originalDamage = event.getAmount();
                float newDamage = originalDamage * Config.NECRO_ARMOR_SET_DAMAGE_REDUCE.get().floatValue();
                event.setAmount(newDamage);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (!player.level().isClientSide() && player.tickCount % 10 == 0) {

            if (hasFullSetOfArmor(player)) {

                MobEffectInstance activeEffect = player.getEffect(ModEffects.PHYLACTERY_EFFECT);

                if (activeEffect == null || activeEffect.getDuration() < 120) {
                    player.addEffect(new MobEffectInstance(
                            ModEffects.PHYLACTERY_EFFECT,
                            200, // ticks
                            0, // level
                            false, // ambient
                            true, // particles
                            true // icon
                    ));
                }

            }
        }
    }

    private static boolean hasFullSetOfArmor(Player player) {
        boolean hasHelmet = player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.NECRODERMIS_HELMET.get());
        boolean hasChestplate = player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.NECRODERMIS_CHESTPLATE.get());
        boolean hasLeggings = player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.NECRODERMIS_LEGGINGS.get());
        boolean hasBoots = player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.NECRODERMIS_BOOTS.get());

        return hasHelmet && hasChestplate && hasLeggings && hasBoots;
    }
}
