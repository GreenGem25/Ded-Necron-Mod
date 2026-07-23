package com.ded.necronmod.client.model;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.Item.PoopHelmetItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PoopHelmetModel extends GeoModel<PoopHelmetItem> {

    private static final ResourceLocation DEFAULT_MODEL = ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID,
            "geo/armor/poop_helmet.geo.json");
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID,
            "textures/armor/poop_helmet.png");
    private static final ResourceLocation DEFAULT_ANIMATION = ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID,
            "animations/armor/poop_helmet.animation.json");

    @Override
    public ResourceLocation getModelResource(PoopHelmetItem animatable) {
        return DEFAULT_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(PoopHelmetItem animatable) {
        return DEFAULT_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(PoopHelmetItem animatable) {
        return DEFAULT_ANIMATION;
    }
}
