package com.ded.necronmod.init;

import com.ded.necronmod.DedNecronMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> CHUGUN_SAW_MINEABLE =
                TagKey.create(
                        Registries.BLOCK,
                        ResourceLocation.fromNamespaceAndPath(
                                DedNecronMod.MODID,
                                "chugun_saw_mineable"
                        )
                );
    }
}
