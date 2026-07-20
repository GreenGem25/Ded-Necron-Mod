package com.ded.necronmod.client.renderer.Item;

import com.ded.necronmod.Item.NecronStaffitem;
import com.ded.necronmod.client.model.NecronStaffModel;
import com.ded.necronmod.client.renderer.layer.MyEmissiveRenderLayer;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class NecronStaffRenderer extends GeoItemRenderer<NecronStaffitem> {
    public NecronStaffRenderer() {
        super(new NecronStaffModel());
        this.addRenderLayer(new MyEmissiveRenderLayer<>(this, "textures/item/necron_staff_glowmask.png"));
    }
}
