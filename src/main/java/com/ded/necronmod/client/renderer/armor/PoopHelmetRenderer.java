package com.ded.necronmod.client.renderer.armor;

import com.ded.necronmod.Item.PoopHelmetItem;
import com.ded.necronmod.client.model.PoopHelmetModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class PoopHelmetRenderer extends GeoArmorRenderer<PoopHelmetItem> {
    public PoopHelmetRenderer() {
        super(new PoopHelmetModel());
    }
}
