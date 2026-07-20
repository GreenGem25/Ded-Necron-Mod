package com.ded.necronmod.client.renderer.block;

import com.ded.necronmod.block.entity.TowerTopBlockEntity;
import com.ded.necronmod.client.model.TowerTopModel;
import com.ded.necronmod.client.renderer.layer.MyEmissiveRenderLayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class TowerTopRenderer extends GeoBlockRenderer<TowerTopBlockEntity> {
    public TowerTopRenderer(BlockEntityRendererProvider.Context context) {
        super(new TowerTopModel());
        addRenderLayer(new MyEmissiveRenderLayer<>(this, "textures/block/tower_top_glowmask.png"));
    }
}
