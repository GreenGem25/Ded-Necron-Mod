package com.ded.necronmod.client.renderer;

import com.ded.necronmod.Item.NecronStaffitem;
import com.ded.necronmod.client.model.NecronStaffModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

import javax.annotation.Nullable;

public class NecronStaffRenderer extends GeoItemRenderer<NecronStaffitem> {
    public NecronStaffRenderer() {
        super(new NecronStaffModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public RenderType getRenderType(NecronStaffitem animatable, ResourceLocation texture,
                                    @Nullable net.minecraft.client.renderer.MultiBufferSource bufferSource,
                                    float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }

    @Override
    public void preRender(PoseStack poseStack, NecronStaffitem animatable, BakedGeoModel model,
                          @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer,
                          boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                          int colour) {
        // Pass 15728880 directly into the standard super method call
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender,
                partialTick, 15728880, packedOverlay, colour);
    }
}
