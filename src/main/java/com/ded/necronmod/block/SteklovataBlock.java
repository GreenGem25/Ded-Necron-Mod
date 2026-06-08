package com.ded.necronmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class SteklovataBlock extends Block {
    public SteklovataBlock(Properties properties) {
        super(properties.sound(SoundType.WOOL));
    }

    @Override
    public void stepOn(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        if (!level.isClientSide() && entity.tickCount % 20 == 0 && entity instanceof LivingEntity living) {
            living.hurt(level.damageSources().cactus(), 0.5F);
        }

        super.stepOn(level, pos, state, entity);
    }

    @Override
    protected void entityInside(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (level.isClientSide()) {
            return;
        }

        if (entity instanceof LivingEntity living) {
            if (living.tickCount % 20 == 0) {
                living.hurt(level.damageSources().cactus(), 0.5F);
            }
        }
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return Block.box(
                1.0D, 0.0D, 1.0D,
                15.0D, 16.0D, 15.0D
        );
    }
}
