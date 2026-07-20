package com.ded.necronmod.client.model;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.entity.CatCaterpillarEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CatCaterpillarModel extends GeoModel<CatCaterpillarEntity> {

    @Override
    public ResourceLocation getModelResource(CatCaterpillarEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "geo/entity/cat_caterpillar.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CatCaterpillarEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "textures/entity/cat_caterpillar.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CatCaterpillarEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "animations/entity/cat_caterpillar.animation.json");
    }

    @Override
    public void setCustomAnimations(CatCaterpillarEntity animatable, long instanceId, AnimationState<CatCaterpillarEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        GeoBone head = this.getAnimationProcessor().getBone("head");

        if (head != null) {
            head.setRotX(-entityData.headPitch() * ((float) Math.PI / 180F));
            head.setRotY(entityData.netHeadYaw() * ((float) Math.PI / 180F));
        }
    }
}