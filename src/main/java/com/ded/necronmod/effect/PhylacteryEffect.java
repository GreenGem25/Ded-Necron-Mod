package com.ded.necronmod.effect;

import com.ded.necronmod.Config;
import com.ded.necronmod.init.ModItems;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PhylacteryEffect extends MobEffect {
    public PhylacteryEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x0b0b0b);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity instanceof Player player && !player.level().isClientSide()) {
            if (player.getHealth() < player.getMaxHealth()) {
                player.heal(1.0F);
            }
            repairEquipment(player);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int ticks = Config.PHYLACTERY_EFFECT_COOLDOWN.get() >> amplifier;

        if (ticks > 0) {
            return duration % ticks == 0;
        }

        return true;
    }

    private void repairEquipment(Player player) {
        int repairAmount = Config.NECRO_ITEMS_REPAIR_POWER.get();

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.isArmor()) {
                ItemStack armorItem = player.getItemBySlot(slot);
                if (!armorItem.isEmpty() && armorItem.isDamaged()) {
                    armorItem.setDamageValue(Math.max(0, armorItem.getDamageValue() - repairAmount));
                }
            }
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);

            if (!stack.isEmpty() && stack.is(ModItems.NECRON_STAFF.get())) {

                if (stack.isDamageableItem() && stack.getDamageValue() > 0) {

                    int newDamage = Math.max(stack.getDamageValue() - repairAmount, 0);

                    stack.setDamageValue(newDamage);
                }
            }
        }
    }
}
