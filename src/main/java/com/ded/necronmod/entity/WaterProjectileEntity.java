package com.ded.necronmod.entity;

import com.ded.necronmod.init.ModBlocks;
import com.ded.necronmod.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class WaterProjectileEntity extends ThrowableItemProjectile {

    public WaterProjectileEntity(EntityType<? extends WaterProjectileEntity> type, Level level) {
        super(type, level);
    }

    public WaterProjectileEntity(Level level, LivingEntity shooter) {
        super(ModEntities.WATER_PROJECTILE.get(), shooter, level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.SPLASH_POTION;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            for (int i = 0; i < 2; i++) {
                this.level().addParticle(
                        ParticleTypes.DRIPPING_WATER,
                        this.getX() + (this.random.nextDouble() - 0.5D) * 0.2D,
                        this.getY() + (this.random.nextDouble() - 0.5D) * 0.2D,
                        this.getZ() + (this.random.nextDouble() - 0.5D) * 0.2D,
                        0.0D, 0.0D, 0.0D
                );
                this.level().addParticle(
                        ParticleTypes.SPLASH,
                        this.getX(), this.getY(), this.getZ(),
                        0.0D, 0.0D, 0.0D
                );
            }
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);

        this.level().playSound(
                null,
                this.getX(), this.getY(), this.getZ(),
                SoundEvents.GENERIC_SPLASH,
                SoundSource.NEUTRAL,
                1.0F,
                1.0F
        );
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);

        if (!this.level().isClientSide) {
            BlockPos hitPos = result.getBlockPos();
            BlockState hitState = this.level().getBlockState(hitPos);

            if (hitState.is(Blocks.FARMLAND)) {
                int moisture = hitState.getValue(FarmBlock.MOISTURE);
                if (moisture < 7) {
                    this.level().setBlockAndUpdate(hitPos, hitState.setValue(FarmBlock.MOISTURE, 7));
                }
            }

            if (hitState.is(Blocks.CAULDRON)) {
                this.level().setBlockAndUpdate(
                        hitPos,
                        Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 1)
                );
            } else if (hitState.is(Blocks.WATER_CAULDRON)) {
                int level = hitState.getValue(LayeredCauldronBlock.LEVEL);
                if (level < 3) {
                    this.level().setBlockAndUpdate(
                            hitPos,
                            hitState.setValue(LayeredCauldronBlock.LEVEL, level + 1)
                    );
                }
            }

            if (hitState.getBlock() instanceof ConcretePowderBlock powderBlock) {
                this.level().setBlockAndUpdate(hitPos, powderBlock.concrete.defaultBlockState());
            }

            BlockPos firePos = hitPos.relative(result.getDirection());
            BlockState fireState = this.level().getBlockState(firePos);

            if (hitState.is(Blocks.FIRE) || hitState.is(Blocks.SOUL_FIRE)) {
                this.level().removeBlock(hitPos, false);
                this.level().playSound(null, hitPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F);
            } else if (fireState.is(Blocks.FIRE) || fireState.is(Blocks.SOUL_FIRE)) {
                this.level().removeBlock(firePos, false);
                this.level().playSound(null, firePos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F);
            }

            if (hitState.is(ModBlocks.TOWER_TOP)) {
                this.level().removeBlock(hitPos, false);
                this.level().explode(
                        this,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        3.0F,
                        Level.ExplosionInteraction.TNT
                );

                this.discard();
            }
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }
}