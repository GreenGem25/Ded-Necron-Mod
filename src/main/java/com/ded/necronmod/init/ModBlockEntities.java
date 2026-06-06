package com.ded.necronmod.init;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.block.MonolithSprout;
import com.ded.necronmod.block.entity.MonolithSproutBlockEntity;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Objects;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, DedNecronMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MonolithSproutBlockEntity>> MONOLITH_SPROUT =
            BLOCK_ENTITIES.register("monolith_sprout", () ->
                    BlockEntityType.Builder.of(
                            MonolithSproutBlockEntity::new,       // Ссылка на конструктор твоего BlockEntity
                            ModBlocks.MONOLITH_SPROUT.get()       // Получаем сам блок
                    ).build(null) // В NeoForge 1.21.1 внутри лямбды .build(null) СНОВА РАБОТАЕТ легально!
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}