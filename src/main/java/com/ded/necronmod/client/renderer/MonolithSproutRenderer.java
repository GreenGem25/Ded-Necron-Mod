package com.ded.necronmod.client.renderer;

import com.ded.necronmod.block.entity.MonolithSproutBlockEntity;
import com.ded.necronmod.client.model.MonolithSproutModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class MonolithSproutRenderer extends GeoBlockRenderer<MonolithSproutBlockEntity> {
    public MonolithSproutRenderer(BlockEntityRendererProvider.Context context) {
        super(new MonolithSproutModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
