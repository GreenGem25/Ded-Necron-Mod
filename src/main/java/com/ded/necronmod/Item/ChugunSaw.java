package com.ded.necronmod.Item;

import com.ded.necronmod.init.ModTags;
import com.ded.necronmod.init.ModToolTiers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ChugunSaw extends PickaxeItem {
    public ChugunSaw(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack,
                                 BlockState state) {

        if (state.is(ModTags.Blocks.CHUGUN_SAW_MINEABLE)) {
            return ModToolTiers.CHUGUN_SAW.getSpeed();
        }

        return 0.0F;
    }

    @Override
    public boolean isCorrectToolForDrops(@NotNull ItemStack stack,
                                         BlockState state) {

        return state.is(ModTags.Blocks.CHUGUN_SAW_MINEABLE);
    }
}
