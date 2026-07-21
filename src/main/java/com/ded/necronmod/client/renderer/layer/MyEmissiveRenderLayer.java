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

    public MyEmissiveRenderLayer(GeoRenderer<T> renderer, String path) {
        super(renderer);
        this.texturePath = path;
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

        // Формируем RenderType для свечения
        RenderType emissiveType = RenderType.entityTranslucentEmissive(
                ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, texturePath)
        );

        // Чтобы не создавать дубликат деликата (Duplicate delegates) с Iris/Relics,
        // мы получаем буфер непосредственно из переданного bufferSource для нашего emissiveType
        VertexConsumer emissiveBuffer = bufferSource.getBuffer(emissiveType);

        // Используем метод renderActually из GeckoLib вместо reRender,
        // чтобы напрямую передать светящуюся текстуру и максимальную яркость LightTexture.FULL_BRIGHT
        getRenderer().reRender(
                model,
                poseStack,
                bufferSource,
                animatable,
                emissiveType,
                emissiveBuffer,
                partialTick,
                LightTexture.FULL_BRIGHT,
                packedOverlay,
                -1
        );
    }
}