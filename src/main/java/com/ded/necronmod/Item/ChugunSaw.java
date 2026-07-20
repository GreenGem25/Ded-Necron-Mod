package com.ded.necronmod.Item;

import com.ded.necronmod.init.ModTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ChugunSaw extends DiggerItem {

    public ChugunSaw(Tier tier, Properties properties) {
        super(tier, ModTags.Blocks.CHUGUN_SAW_MINEABLE, properties);
    }

    @Override
    public boolean isCorrectToolForDrops(@NotNull ItemStack stack, BlockState state) {
        return state.is(ModTags.Blocks.CHUGUN_SAW_MINEABLE);
    }
}