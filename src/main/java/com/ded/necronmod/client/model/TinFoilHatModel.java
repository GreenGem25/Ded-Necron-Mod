package com.ded.necronmod.client.model;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.Item.TinFoilHat;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TinFoilHatModel extends GeoModel<TinFoilHat> {
    @Override
    public ResourceLocation getModelResource(TinFoilHat animatable) {
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "geo/armor/tin_foil_hat.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TinFoilHat animatable) {
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "textures/armor/tin_foil_hat.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TinFoilHat animatable) {
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "animations/armor/tin_foil_hat.animation.json");
    }
}
