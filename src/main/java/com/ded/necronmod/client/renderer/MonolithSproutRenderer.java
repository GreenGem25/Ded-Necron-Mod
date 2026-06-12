package com.ded.necronmod.client.renderer;

import com.ded.necronmod.block.entity.MonolithSproutBlockEntity;
import com.ded.necronmod.client.model.MonolithSproutModel;
import com.ded.necronmod.client.renderer.layer.MonolithSproutEmissiveRenderLayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MonolithSproutRenderer extends GeoBlockRenderer<MonolithSproutBlockEntity> {
    public MonolithSproutRenderer(BlockEntityRendererProvider.Context context) {
        super(new MonolithSproutModel());
        addRenderLayer(new MonolithSproutEmissiveRenderLayer<>(this));
    }
}
