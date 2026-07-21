package com.ded.necronmod.block.entity;

import com.ded.necronmod.init.ModBlockEntities;
import com.ded.necronmod.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class TowerTopBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation ROTATE_ANIM = RawAnimation.begin().thenLoop("main");
    private int serverTickCounter = 0;
    private static final int DAMAGE_INTERVAL = 20;
    private static final double RADIUS = 30.0;

    public TowerTopBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TOWER_TOP.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "antenna_rotation_controller", 0, state -> state.setAndContinue(ROTATE_ANIM)));
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TowerTopBlockEntity blockEntity) {
        blockEntity.serverTickCounter++;

        if (blockEntity.serverTickCounter >= DAMAGE_INTERVAL) {
            blockEntity.serverTickCounter = 0;

            AABB searchArea = new AABB(pos).inflate(RADIUS);

            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, searchArea, entity -> {
                if (!entity.isAlive() || entity.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) > (RADIUS * RADIUS)) {
                    return false;
                }
                if (entity instanceof Player player && player.isCreative()) {
                    return false;
                }

                ItemStack headItem = entity.getItemBySlot(EquipmentSlot.HEAD);

                return headItem.isEmpty() || !headItem.is(ModItems.TIN_FOIL_HAT.get());
            });

            if (!entities.isEmpty()) {
                DamageSource damageSource = level.damageSources().magic();
                for (LivingEntity entity : entities) {
                    entity.hurt(damageSource, 2.0F);
                    entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false));
                }
            }
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
