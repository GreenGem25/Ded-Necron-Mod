package com.ded.necronmod.client.renderer.layer;

import com.ded.necronmod.DedNecronMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class MyEmissiveRenderLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {

    private final String texturePath;

    public MyEmissiveRenderLayer(GeoRenderer<T> renderer, String Path) {
        super(renderer);
        texturePath = Path;
    }

    @Override
    public void render(
            PoseStack poseStack,
            T animatable,
            BakedGeoModel model,
            RenderType renderType,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            float partialTick,
            int packedLight,
            int packedOverlay) {

        RenderType emissiveType =
                RenderType.entityTranslucentEmissive(ResourceLocation.fromNamespaceAndPath(
                        DedNecronMod.MODID,
                        texturePath
                ));

        getRenderer().reRender(
                model,
                poseStack,
                bufferSource,
                animatable,
                emissiveType,
                bufferSource.getBuffer(emissiveType),
                partialTick,
                LightTexture.FULL_BRIGHT,
                packedOverlay,
                -1
        );
    }
}