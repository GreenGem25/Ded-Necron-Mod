package com.ded.necronmod.init;

import com.ded.necronmod.Config;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class ModToolTiers {
    public static final Tier NECRO = new SimpleTier(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            1,
            100.0F,
            19.0F,
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
}