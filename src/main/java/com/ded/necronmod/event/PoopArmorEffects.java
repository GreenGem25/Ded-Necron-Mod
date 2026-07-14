package com.ded.necronmod.event;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.init.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

@EventBusSubscriber(modid = DedNecronMod.MODID)
public class PoopArmorEffects {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (!player.level().isClientSide() && (player.tickCount + player.hashCode()) % 200 == 0) {

            if (player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.POOP_HELMET.get())) {

                double radius = 5.0;

                AABB area = player.getBoundingBox().inflate(radius);
                List<Player> nearbyPlayers = player.level().getEntitiesOfClass(Player.class, area);

                for (Player target : nearbyPlayers) {
                    if (target != player) {
                        target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 220, 0, false, true));
                    }
                }

            }
        }
    }
}
