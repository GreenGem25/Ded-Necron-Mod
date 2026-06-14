package com.ded.necronmod.init;

import com.ded.necronmod.Config;
import com.ded.necronmod.DedNecronMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.SimpleTier;

public class ModToolTiers {
    public static final Tier NECRO = new SimpleTier(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            1,
            100.0F,
            29.0F,
            22,
            () -> Ingredient.of(ModItems.LIVING_METAL_PLATE.get())
    );

    public static final Tier SHAMPUR = new SimpleTier(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            256,
            100.0F,
            2.0F,
            1,
            () -> Ingredient.of(Items.IRON_INGOT)
    );

    public static final Tier CHUGUN_SAW = new SimpleTier(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            5000,
            20.0F,
            2.0F,
            22,
            () -> Ingredient.of(Items.IRON_INGOT)
    );
}