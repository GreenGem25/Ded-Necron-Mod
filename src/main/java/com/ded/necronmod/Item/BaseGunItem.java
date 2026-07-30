package com.ded.necronmod.Item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class BaseGunItem extends Item {
    protected final int maxAmmo;
    protected final int cooldownTicks;

    public BaseGunItem(Properties properties, int maxAmmo, int cooldownTicks) {
        super(properties.stacksTo(1));
        this.maxAmmo = maxAmmo;
        this.cooldownTicks = cooldownTicks;
    }

    public int getAmmo(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        return tag.contains("Ammo") ? tag.getInt("Ammo") : maxAmmo;
    }

    public void setAmmo(ItemStack stack, int ammo) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt("Ammo", ammo));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player,
                                                           @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (tryReload(level, player, stack)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        int currentAmmo = getAmmo(stack);

        if (player.isCreative() || currentAmmo > 0) {
            if (!level.isClientSide) {
                shootProjectile(level, player);
                onShoot(level, player, stack);

                if (!player.isCreative()) {
                    setAmmo(stack, currentAmmo - 1);
                }
            }

            playShootSound(level, player);
            player.getCooldowns().addCooldown(this, cooldownTicks);
            return InteractionResultHolder.pass(stack);
        } else {
            playEmptySound(level, player);
            return InteractionResultHolder.fail(stack);
        }
    }

    protected abstract boolean tryReload(Level level, Player player, ItemStack stack);
    protected abstract void shootProjectile(Level level, Player player);
    protected abstract void playShootSound(Level level, Player player);
    protected abstract void playEmptySound(Level level, Player player);
    protected void onShoot(Level level, Player player, ItemStack stack) {}

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return getAmmo(stack) < maxAmmo;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return Math.round(13.0F * getAmmo(stack) / maxAmmo);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Заряд: " + getAmmo(stack) + " / " + maxAmmo));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}