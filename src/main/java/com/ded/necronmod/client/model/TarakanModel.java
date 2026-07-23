package com.ded.necronmod.client.model;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.entity.TarakanEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Locale;
import java.util.Objects;

public class TarakanModel extends GeoModel<TarakanEntity> {


    private static final ResourceLocation DEFAULT_MODEL = ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID,
            "geo/entity/tarakan.geo.json");
    private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID,
            "textures/entity/tarakan.png");
    private static final ResourceLocation FEMBOY_TEXTURE = ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID,
            "textures/entity/tarakan_femboy.png");
    private static final ResourceLocation DEFAULT_ANIMATION = ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID,
            "animations/entity/tarakan.animation.json");

    @Override
    public ResourceLocation getModelResource(TarakanEntity animatable) {
        return DEFAULT_MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(TarakanEntity animatable) {
        if (animatable.hasCustomName()) {
            String name = Objects.requireNonNull(animatable.getCustomName()).getString().toLowerCase(Locale.ROOT);

            if (name.equals("nizir")) {
                return FEMBOY_TEXTURE;
            }
        }

        return DEFAULT_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(TarakanEntity animatable) {
        return DEFAULT_ANIMATION;
    }

    @Override
    public void setCustomAnimations(TarakanEntity animatable, long instanceId, AnimationState<TarakanEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        GeoBone head = this.getAnimationProcessor().getBone("head");

        if (head != null) {
            head.setRotX(entityData.headPitch() * ((float) Math.PI / 180F));
            head.setRotY(entityData.netHeadYaw() * ((float) Math.PI / 180F));
        }
    }
}