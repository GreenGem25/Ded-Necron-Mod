package com.ded.necronmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class RottenFleshBlock extends Block {
    public RottenFleshBlock(Properties properties) {
        super(properties);
    }

    @Override
    public float getDestroyProgress(@NotNull BlockState state, Player player,
                                    @NotNull BlockGetter level, @NotNull BlockPos pos) {
        ItemStack tool = player.getMainHandItem();

        if (tool.getItem() instanceof SwordItem) {
            return 0.3F;
        }

        return super.getDestroyProgress(state, player, level, pos);
    }
}
