package com.ded.necronmod.Item;

import com.ded.necronmod.Config;
import com.ded.necronmod.client.renderer.Item.NecronStaffRenderer;
import com.ded.necronmod.init.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class NecronStaffitem extends SwordItem implements GeoItem {
    public NecronStaffitem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public int getMaxDamage(@NotNull ItemStack stack) {
        return Config.NECRO_STAFF_DURABILITY.get();
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, @NotNull LivingEntity attacker) {
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1, false, true));
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {

        tooltipComponents.add(Component.translatable("tooltip.necronmod.necron_staff_bonus")
                .withStyle(ChatFormatting.GOLD));

        tooltipComponents.add(Component.translatable("tooltip.necronmod.necron_staff_bonus_desc")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private NecronStaffRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new NecronStaffRenderer();
                }
                return this.renderer;
            }
        });
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        player.getCooldowns().addCooldown(this, 45);
        RandomSource random = player.getRandom();

        level.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                ModSounds.NECRON_STAFF_SHOT.get(),
                SoundSource.PLAYERS,
                1.0F,
                0.8F + random.nextFloat() * 0.4F
        );

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            double maxDistance = 60.0;
            float damage = 20.0F;
            int canHit = 3;

            Vec3 startPos = player.getEyePosition(1.0F);
            Vec3 lookVector = player.getViewVector(1.0F);
            Vec3 endPos = startPos.add(lookVector.scale(maxDistance));

            double step = 0.25;

            for (double d = 0; d < maxDistance; d += step) {
                Vec3 currentCheckPos = startPos.add(lookVector.scale(d));

                BlockPos blockPos = BlockPos.containing(currentCheckPos);
                BlockState blockState = level.getBlockState(blockPos);

                if (!blockState.isAir() && !blockState.canBeReplaced()) {
                    if (blockState.getFluidState().isEmpty()) {
                        endPos = currentCheckPos;
                        break;
                    }
                }

                AABB boundingBox = new AABB(currentCheckPos.add(-0.3, -0.3, -0.3),
                        currentCheckPos.add(0.3, 0.3, 0.3));
                List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                        boundingBox, entity -> entity != player);

                if (!targets.isEmpty()) {
                    for (LivingEntity target : targets) {
                        if (!target.isAlive()) {
                            continue;
                        }
                        canHit--;
                        target.hurt(player.damageSources().playerAttack(player), damage);
                        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                                100, 1, false, true));
                    }
                    endPos = currentCheckPos;
                    if (canHit <= 0) {
                        break;
                    }
                }
            }

            DustParticleOptions greenLaserParticle =
                    new DustParticleOptions(
                            new Vector3f(0.0F, 1.0F, 0.0F), 1.5F
                    );

            Vec3 particleTrack = endPos.subtract(startPos);
            double totalDistance = particleTrack.length();
            Vec3 direction = particleTrack.normalize();

            for (double i = 0; i < totalDistance; i += 0.15) {
                Vec3 spawnPoint = startPos.add(direction.scale(i));

                serverLevel.sendParticles(greenLaserParticle,
                        spawnPoint.x, spawnPoint.y, spawnPoint.z,
                        1, 0, 0, 0, 0);
            }

            serverLevel.sendParticles(greenLaserParticle, endPos.x, endPos.y, endPos.z,
                    10, 0.2, 0.2, 0.2, 0.05);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
}
