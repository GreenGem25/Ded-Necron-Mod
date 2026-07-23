package com.ded.necronmod.client.model;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.Item.NecronStaffitem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NecronStaffModel extends GeoModel<NecronStaffitem> {

    private static final ResourceLocation DEFAULT_MODEL = ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID,
            "geo/item/necron_staff.geo.json");
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID,
            "textures/item/necron_staff.png");
    private static final ResourceLocation DEFAULT_ANIMATION = ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID,
            "animations/item/necron_staff.animation.json");

    @Override
    public ResourceLocation getModelResource(NecronStaffitem animatable) {
        return DEFAULT_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(NecronStaffitem animatable) {
        return DEFAULT_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(NecronStaffitem animatable) {
        return DEFAULT_ANIMATION;
    }
}
