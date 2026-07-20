package com.ded.necronmod.client.model;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.block.entity.TowerBaseBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TowerBaseModel extends GeoModel<TowerBaseBlockEntity> {
    @Override
    public ResourceLocation getModelResource(TowerBaseBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "geo/block/tower_base.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TowerBaseBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "textures/block/tower_base.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TowerBaseBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "animations/block/tower_base.animation.json");
    }
}
