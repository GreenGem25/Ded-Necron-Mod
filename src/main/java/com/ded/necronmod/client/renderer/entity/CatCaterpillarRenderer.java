package com.ded.necronmod.client.renderer.entity;

import com.ded.necronmod.client.model.CatCaterpillarModel;
import com.ded.necronmod.entity.CatCaterpillarEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CatCaterpillarRenderer extends GeoEntityRenderer<CatCaterpillarEntity> {

    public CatCaterpillarRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CatCaterpillarModel());
        this.shadowRadius = 0.4F;
    }

    @Override
    public void preRender(PoseStack poseStack, CatCaterpillarEntity animatable,
                          BakedGeoModel model, MultiBufferSource bufferSource,
                          VertexConsumer buffer, boolean isReRender, float partialTick,
                          int packedLight, int packedOverlay, int colour) {

        super.preRender(poseStack, animatable, model, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, colour);

        if (animatable.isBaby()) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
        }
    }
}