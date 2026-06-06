package com.ded.necronmod.client.model;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.block.MonolithSprout;
import com.ded.necronmod.block.entity.MonolithSproutBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class MonolithSproutModel extends GeoModel<MonolithSproutBlockEntity> {
    @Override
    public ResourceLocation getModelResource(MonolithSproutBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "geo/block/monolith_sprout.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MonolithSproutBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "textures/block/monolith_sprout.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MonolithSproutBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "animations/block/monolith_sprout.animation.json");
    }

    @Override
    public void setCustomAnimations(MonolithSproutBlockEntity animatable, long instanceId, AnimationState<MonolithSproutBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        BlockState state = animatable.getBlockState();

        if (state.hasProperty(CropBlock.AGE)) {
            int age = state.getValue(CropBlock.AGE);

            GeoBone stageSmall = getAnimationProcessor().getBone("stage_small");
            GeoBone stageMedium = getAnimationProcessor().getBone("stage_medium");
            GeoBone stageBig = getAnimationProcessor().getBone("stage_big");

            if (stageSmall != null) {
                stageSmall.setHidden(age > 2);
            }

            if (stageMedium != null) {
                stageMedium.setHidden(age < 3 || age > 6);
            }

            if (stageBig != null) {
                stageBig.setHidden(age != 7);
            }

            GeoBone root = getAnimationProcessor().getBone("root");
            if (root != null) {
                BlockPos pos = animatable.getBlockPos();

                long seed = (long) pos.getX() * 3129871L ^ (long) pos.getZ() * 116129781L ^ (long) pos.getY();
                java.util.Random random = new java.util.Random(seed);

                int rotationIndex = random.nextInt(4);

                float randomYaw = rotationIndex * 90.0F;
                float radians = randomYaw * ((float) Math.PI / 180.0F);

                root.setRotY(radians);
            }
        }
    }
}
