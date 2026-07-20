package com.ded.necronmod.client.renderer.entity;

import com.ded.necronmod.client.model.TarakanModel;
import com.ded.necronmod.entity.TarakanEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TarakanRenderer extends GeoEntityRenderer<TarakanEntity> {

    public TarakanRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TarakanModel());
        this.shadowRadius = 0.4F;
    }

    @Override
    public void preRender(PoseStack poseStack, TarakanEntity animatable,
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