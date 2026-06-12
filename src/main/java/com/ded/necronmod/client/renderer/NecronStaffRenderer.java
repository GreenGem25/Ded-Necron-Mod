package com.ded.necronmod.client.renderer;

import com.ded.necronmod.Item.NecronStaffitem;
import com.ded.necronmod.client.model.NecronStaffModel;
import com.ded.necronmod.client.renderer.layer.NecronStaffEmissiveRenderLayer;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class NecronStaffRenderer extends GeoItemRenderer<NecronStaffitem> {
    public NecronStaffRenderer() {
        super(new NecronStaffModel());
        this.addRenderLayer(new NecronStaffEmissiveRenderLayer<>(this));
    }
}
