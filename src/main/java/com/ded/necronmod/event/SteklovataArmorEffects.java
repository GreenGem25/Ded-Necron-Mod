package com.ded.necronmod.event;

import com.ded.necronmod.Config;
import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.init.ModEffects;
import com.ded.necronmod.init.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = DedNecronMod.MODID)
public class SteklovataArmorEffects {
    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity victim = event.getEntity();

        if (victim.level().isClientSide()) return;

        if (hasFullSetOfArmor(victim)) {

            if (event.getSource().getEntity() instanceof LivingEntity attacker) {

                if (attacker != victim) {

                    float returnDamage = event.getAmount();

                    attacker.hurt(victim.damageSources().thorns(victim), returnDamage);
                }
            }
        }
    }

    private static final Map<UUID, Vec2> LAST_POS = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (player.level().isClientSide() || player.tickCount % 20 != 0 || !hasFullSetOfArmor(player)) {
            return;
        }

        Vec2 current = new Vec2((float) player.position().x, (float) player.position().z);
        Vec2 previous = LAST_POS.put(player.getUUID(), current);

        if (previous == null) {
            return;
        }

        if (current.distanceToSqr(previous) > 1.0E-6) {
            player.hurt(player.damageSources().magic(), 0.5F);
        }
    }

    private static boolean hasFullSetOfArmor(LivingEntity player) {
        boolean hasHelmet = player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.STEKLOVATA_HELMET.get());
        boolean hasChestplate = player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.STEKLOVATA_CHESTPLATE.get());
        boolean hasLeggings = player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.STEKLOVATA_LEGGINGS.get());
        boolean hasBoots = player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.STEKLOVATA_BOOTS.get());

        return hasHelmet && hasChestplate && hasLeggings && hasBoots;
    }
}
