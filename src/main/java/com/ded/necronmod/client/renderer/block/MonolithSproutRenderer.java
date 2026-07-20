package com.ded.necronmod.client.renderer.block;

import com.ded.necronmod.block.entity.MonolithSproutBlockEntity;
import com.ded.necronmod.client.model.MonolithSproutModel;
import com.ded.necronmod.client.renderer.layer.MyEmissiveRenderLayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MonolithSproutRenderer extends GeoBlockRenderer<MonolithSproutBlockEntity> {
    public MonolithSproutRenderer(BlockEntityRendererProvider.Context context) {
        super(new MonolithSproutModel());
        addRenderLayer(new MyEmissiveRenderLayer<>(this, "textures/block/monolith_sprout_glowmask.png"));
    }
}
