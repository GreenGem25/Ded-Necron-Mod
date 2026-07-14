package com.ded.necronmod.client.model;

import com.ded.necronmod.Item.NecronStaffitem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NecronStaffModel extends GeoModel<NecronStaffitem> {
    @Override
    public ResourceLocation getModelResource(NecronStaffitem animatable) {
        return ResourceLocation.fromNamespaceAndPath("necronmod", "geo/item/necron_staff.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(NecronStaffitem animatable) {
        return ResourceLocation.fromNamespaceAndPath("necronmod", "textures/item/necron_staff.png");
    }

    @Override
    public ResourceLocation getAnimationResource(NecronStaffitem animatable) {
        return ResourceLocation.fromNamespaceAndPath("necronmod", "animations/item/necron_staff.animation.json");
    }
}
