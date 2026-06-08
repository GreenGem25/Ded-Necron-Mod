package com.ded.necronmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class RottenFleshBlock extends Block {
    public RottenFleshBlock(Properties properties) {
        super(properties
                .strength(0.7F, 6.0F)
                .friction(1.0F)
                .speedFactor(1.0F)
                .jumpFactor(1.1F)
                .sound(SoundType.HONEY_BLOCK));
    }
}
