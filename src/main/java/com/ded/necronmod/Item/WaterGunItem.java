package com.ded.necronmod.item;

import com.ded.necronmod.entity.WaterProjectileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WaterGunItem extends BaseGunItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation SHOOT_ANIM = RawAnimation.begin().thenPlay("shoot");

    public WaterGunItem(Properties properties) {
        super(properties, 16, 5);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        controllers.add(
                new AnimationController<>(this, "shoot_controller", 0, state -> PlayState.STOP)
                        .triggerableAnim("shoot", SHOOT_ANIM)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected boolean tryReload(Level level, Player player, ItemStack stack) {
        BlockHitResult hitResult = getPlayerPOVHitResult(
                level, player, ClipContext.Fluid.SOURCE_ONLY
        );

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = hitResult.getBlockPos();
            if (level.getFluidState(pos).is(Fluids.WATER)) {
                if (getAmmo(stack) < maxAmmo) {
                    if (!level.isClientSide) {
                        setAmmo(stack, maxAmmo);
                        level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
                        player.displayClientMessage(Component.literal("Водяной пистолет заправлен!"), true);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void shootProjectile(Level level, Player player) {
        WaterProjectileEntity projectile = new WaterProjectileEntity(level, player);
        projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
        level.addFreshEntity(projectile);
    }

    @Override
    protected void onShoot(Level level, Player player, ItemStack stack) {
        triggerAnim(
                player,
                GeoItem.getOrAssignId(stack, (ServerLevel) level),
                "shoot_controller",
                "shoot"
        );
    }

    @Override
    protected void playShootSound(Level level, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    protected void playEmptySound(Level level, Player player) {
        if (level.isClientSide) {
            player.displayClientMessage(Component.literal("Закончилась вода! Наберите из источника."), true);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.5F, 1.2F);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return 0x3498DB;
    }
}