package com.ded.necronmod.block;

import com.ded.necronmod.Item.ChugunSaw;
import com.ded.necronmod.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class NecronTombBlock extends Block {
    public NecronTombBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canHarvestBlock(@NotNull BlockState state,
                                   @NotNull BlockGetter level,
                                   @NotNull BlockPos pos,
                                   Player player) {

        ItemStack stack = player.getMainHandItem();

        return stack.getItem() instanceof ChugunSaw;
    }

    @Override
    public float getDestroyProgress(@NotNull BlockState state,
                                    Player player,
                                    @NotNull BlockGetter level,
                                    @NotNull BlockPos pos) {

        if (player.getMainHandItem().is(ModItems.CHUGUN_SAW)) {
            return 1.0F / 20.0F;
        }

        return 0F;
    }

    @Override
    public boolean canEntityDestroy(@NotNull BlockState state,
                                    @NotNull BlockGetter level,
                                    @NotNull BlockPos pos,
                                    @NotNull Entity entity) {
        return false;
    }
}
