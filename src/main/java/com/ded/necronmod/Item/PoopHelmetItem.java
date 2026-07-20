package com.ded.necronmod.Item;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class PoopHelmetItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public PoopHelmetItem(Holder<ArmorMaterial> material, ArmorItem.Type type, Item.Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide() && entity instanceof Player wearer) {

            if (wearer.getItemBySlot(EquipmentSlot.HEAD) == stack) {

                if ((wearer.tickCount + wearer.hashCode()) % 20 == 0) {
                    double radius = 5.0;
                    AABB area = wearer.getBoundingBox().inflate(radius);
                    List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class, area);

                    for (Player target : nearbyPlayers) {
                        if (target != wearer && !target.isCreative() && !target.isSpectator()) {
                            target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 0,
                                    false, true));
                        }
                    }
                }
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
