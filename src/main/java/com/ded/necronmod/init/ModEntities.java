package com.ded.necronmod.init;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.entity.CatCaterpillarEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    // Создаем регистратор для типов сущностей по аналогии с блоками и предметами
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, DedNecronMod.MODID);

    // Регистрируем котогусеницу:
    // MobCategory.CREATURE указывает игре, что это мирное приручаемое животное
    public static final DeferredHolder<EntityType<?>, EntityType<CatCaterpillarEntity>> CAT_CATERPILLAR =
            ENTITY_TYPES.register("cat_caterpillar",
                    () -> EntityType.Builder.of(CatCaterpillarEntity::new, MobCategory.CREATURE)
                            .sized(0.7F, 0.4F) // Размеры хитбокса: ширина и высота
                            .build("cat_caterpillar")
            );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}