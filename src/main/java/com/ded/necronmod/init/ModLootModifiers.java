package com.ded.necronmod.init;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.loot.AddItemModifier;
import com.ded.necronmod.loot.ReplaceFishModifier;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModLootModifiers {
    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, DedNecronMod.MODID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<AddItemModifier>> ADD_ITEM =
            LOOT_MODIFIERS.register("add_item", () -> AddItemModifier.CODEC);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<ReplaceFishModifier>> REPLACE_FISH =
            LOOT_MODIFIERS.register("replace_fish", () -> ReplaceFishModifier.CODEC);

    public static void register(IEventBus bus) {
        LOOT_MODIFIERS.register(bus);
    }
}
