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

    private static final ResourceLocation DEFAULT_MODEL = ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID,
            "geo/entity/cat_caterpillar.geo.json");
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID,
            "textures/entity/cat_caterpillar.png");
    private static final ResourceLocation DEFAULT_ANIMATION = ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID,
            "animations/entity/cat_caterpillar.animation.json");

    @Override
    public ResourceLocation getModelResource(CatCaterpillarEntity animatable) {
        return DEFAULT_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(CatCaterpillarEntity animatable) {
        return DEFAULT_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(CatCaterpillarEntity animatable) {
        return DEFAULT_ANIMATION;
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