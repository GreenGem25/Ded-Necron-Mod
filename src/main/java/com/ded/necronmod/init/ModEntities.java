package com.ded.necronmod.init;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.entity.CatCaterpillarEntity;
import com.ded.necronmod.entity.TarakanEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, DedNecronMod.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<CatCaterpillarEntity>> CAT_CATERPILLAR =
            ENTITY_TYPES.register("cat_caterpillar",
                    () -> EntityType.Builder.of(CatCaterpillarEntity::new, MobCategory.CREATURE)
                            .sized(0.7F, 0.4F)
                            .build("cat_caterpillar")
            );

    public static final DeferredHolder<EntityType<?>, EntityType<TarakanEntity>> TARAKAN =
            ENTITY_TYPES.register("tarakan",
                    () -> EntityType.Builder.of(TarakanEntity::new, MobCategory.MONSTER)
                            .sized(0.5F, 0.2F)
                            .build("tarakan")
            );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}