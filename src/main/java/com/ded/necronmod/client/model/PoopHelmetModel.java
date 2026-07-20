package com.ded.necronmod.client.model;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.Item.PoopHelmetItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PoopHelmetModel extends GeoModel<PoopHelmetItem> {
    @Override
    public ResourceLocation getModelResource(PoopHelmetItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "geo/armor/poop_helmet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PoopHelmetItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "textures/armor/poop_helmet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PoopHelmetItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "animations/armor/poop_helmet.animation.json");
    }
}
