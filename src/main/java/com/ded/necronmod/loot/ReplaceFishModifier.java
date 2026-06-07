package com.ded.necronmod.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class ReplaceFishModifier extends LootModifier {
    public static final MapCodec<ReplaceFishModifier> CODEC = RecordCodecBuilder.mapCodec(
            inst -> codecStart(inst).and(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("custom_fish").forGetter(m -> m.customFish)
            ).apply(inst, ReplaceFishModifier::new)
    );

    private final Item customFish;

    public ReplaceFishModifier(LootItemCondition[] conditions, Item customFish) {
        super(conditions);
        this.customFish = customFish;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (this.conditions.length == 0) return generatedLoot;

        RandomSource random = context.getRandom();

        // Loop through the generated loot to find vanilla fish
        for (int i = 0; i < generatedLoot.size(); i++) {
            ItemStack stack = generatedLoot.get(i);
            Item item = stack.getItem();

            // Check if the caught item is a basic vanilla fish
            if (item == Items.COD || item == Items.SALMON || item == Items.PUFFERFISH || item == Items.TROPICAL_FISH) {
                // Swap the item stack for your custom fish, preserving the original count (usually 1)
                generatedLoot.set(i, new ItemStack(this.customFish, stack.getCount()));
            }
        }

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}