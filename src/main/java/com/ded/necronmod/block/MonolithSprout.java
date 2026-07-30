package com.ded.necronmod.block;

import com.ded.necronmod.init.ModItems;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

public class MonolithSprout extends CropBlock {
    public MonolithSprout() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noOcclusion()
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
                .lightLevel(state -> 5)
        );
    }

    @Override
    protected @NotNull ItemLike getBaseSeedId() {
        return ModItems.MONOLITH_SEEDS.get();
    }

    @Override
    public int getMaxAge() {
        return 7;
    }
}
