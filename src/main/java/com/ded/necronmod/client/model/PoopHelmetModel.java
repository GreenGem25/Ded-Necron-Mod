package com.ded.necronmod.client.model;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.Item.PoopHelmetItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PoopHelmetModel extends GeoModel<PoopHelmetItem> {
    @Override
    public ResourceLocation getModelResource(PoopHelmetItem animatable) {
        // Путь к твоему файлу экспортированному из Blockbench
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "geo/armor/poop_helmet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PoopHelmetItem animatable) {
        // Путь к основной текстуре шлема
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "textures/armor/poop_helmet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PoopHelmetItem animatable) {
        // Если анимаций нет, можно возвращать пустую или ту же самую, GeckoLib ее проигнорирует
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "animations/armor/poop_helmet.animation.json");
    }
}
