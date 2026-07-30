package com.ded.necronmod.client.model;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.Item.WaterGunItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WaterGunModel extends GeoModel<WaterGunItem> {

    private static final ResourceLocation DEFAULT_MODEL = ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID,
            "geo/item/water_gun.geo.json");
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID,
            "textures/item/water_gun.png");
    private static final ResourceLocation DEFAULT_ANIMATION = ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID,
            "animations/item/water_gun.animation.json");

    @Override
    public ResourceLocation getModelResource(WaterGunItem animatable) {
        return DEFAULT_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(WaterGunItem animatable) {
        return DEFAULT_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(WaterGunItem animatable) {
        return DEFAULT_ANIMATION;
    }
}
