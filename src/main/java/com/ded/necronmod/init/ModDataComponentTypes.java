package com.ded.necronmod.init;

import com.ded.necronmod.DedNecronMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponentTypes {
    public static final DeferredRegister<DataComponentType<?>> COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, DedNecronMod.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> CAPTURED_MOB =
            COMPONENT_TYPES.register("captured_mob",
                    () -> DataComponentType.<CompoundTag>builder()
                            .persistent(CompoundTag.CODEC)
                            .build());

    public static void register(IEventBus eventBus) {
        COMPONENT_TYPES.register(eventBus);
    }
}
