package com.ded.necronmod.client.renderer.Item;

import com.ded.necronmod.Item.WaterGunItem;
import com.ded.necronmod.client.model.WaterGunModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WaterGunRenderer extends GeoItemRenderer<WaterGunItem> {
        public WaterGunRenderer() {
            super(new WaterGunModel());
        }
}
