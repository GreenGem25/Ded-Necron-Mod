package com.ded.necronmod.client.model;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.block.entity.TowerTopBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

import java.util.Objects;

public class TowerTopModel extends GeoModel<TowerTopBlockEntity> {
    @Override
    public ResourceLocation getModelResource(TowerTopBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "geo/block/tower_top.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TowerTopBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "textures/block/tower_top.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TowerTopBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "animations/block/tower_top.animation.json");
    }

    @Override
    public void setCustomAnimations(TowerTopBlockEntity animatable, long instanceId, AnimationState<TowerTopBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone antennaLight = getAnimationProcessor().getBone("antenna_light");
        GeoBone antennaLightOff = getAnimationProcessor().getBone("antenna_light_off");
        GeoBone smallBoxLight = getAnimationProcessor().getBone("small_box_light");
        GeoBone smallBoxLightOff = getAnimationProcessor().getBone("small_box_light_off");

        if (animatable.getLevel() != null && antennaLight != null && antennaLightOff != null && smallBoxLight != null && smallBoxLightOff != null) {
            long currentTick = Objects.requireNonNull(animatable.getLevel()).getGameTime();

            boolean isLightOn1 = (currentTick / 9) % 2 == 0;
            boolean isLightOn2 = (currentTick / 11) % 2 == 0;

            antennaLight.setHidden(!isLightOn1);
            smallBoxLight.setHidden(!isLightOn2);
            antennaLightOff.setHidden(isLightOn1);
            smallBoxLightOff.setHidden(isLightOn2);
        }
    }
}
