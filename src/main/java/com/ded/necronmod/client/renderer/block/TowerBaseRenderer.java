package com.ded.necronmod.client.renderer.block;

import com.ded.necronmod.block.entity.TowerBaseBlockEntity;
import com.ded.necronmod.client.model.TowerBaseModel;
import com.ded.necronmod.client.renderer.layer.MyEmissiveRenderLayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class TowerBaseRenderer extends GeoBlockRenderer<TowerBaseBlockEntity> {
    public TowerBaseRenderer(BlockEntityRendererProvider.Context context) {
        super(new TowerBaseModel());
        addRenderLayer(new MyEmissiveRenderLayer<>(this, "textures/block/tower_base_glowmask.png"));
    }
}
