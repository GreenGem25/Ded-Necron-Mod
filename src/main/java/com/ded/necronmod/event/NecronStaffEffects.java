package com.ded.necronmod.event;

import com.ded.necronmod.Config;
import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.init.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = DedNecronMod.MODID)
public class NecronStaffEffects {

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();

        if (!player.level().isClientSide()) {
            ItemStack mainHandItem = player.getMainHandItem();

            if (mainHandItem.is(ModItems.NECRON_STAFF.get())) {
                if (event.getTarget() instanceof LivingEntity target) {
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1, false, true));
                }
            }
        }
    }

}
