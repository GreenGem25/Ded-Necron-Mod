package com.ded.necronmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class NapoleonCake extends CakeBlock {
    public NapoleonCake(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            ItemStack itemstack = player.getItemInHand(player.getUsedItemHand());
            if (eat(level, pos, state, player).consumesAction()) {
                return InteractionResult.SUCCESS;
            }
            if (itemstack.isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }

        return eat(level, pos, state, player);
    }

    protected static @NotNull InteractionResult eat(@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState state, Player player) {
        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        } else {
            player.awardStat(Stats.EAT_CAKE_SLICE);

            player.getFoodData().eat(4, 0.5F);
            player.hurt(player.damageSources().cactus(), 1);

            int bites = state.getValue(BITES);
            if (bites < 6) {
                level.setBlock(pos, state.setValue(BITES, bites + 1), 3);
            } else {
                level.destroyBlock(pos, false);
            }

            return InteractionResult.SUCCESS;
        }
    }
}
