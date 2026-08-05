package com.ded.necronmod.client.renderer.armor;

import com.ded.necronmod.item123.PoopHelmetItem;
import com.ded.necronmod.client.model.PoopHelmetModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class PoopHelmetRenderer extends GeoArmorRenderer<PoopHelmetItem> {
    public PoopHelmetRenderer() {
        super(new PoopHelmetModel());
    }
}
