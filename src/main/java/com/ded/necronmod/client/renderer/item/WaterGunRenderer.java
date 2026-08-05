package com.ded.necronmod.client.renderer.item;

import com.ded.necronmod.item.WaterGunItem;
import com.ded.necronmod.client.model.WaterGunModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WaterGunRenderer extends GeoItemRenderer<WaterGunItem> {
        public WaterGunRenderer() {
            super(new WaterGunModel());
        }
}
