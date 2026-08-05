package com.ded.necronmod.client.renderer.armor;

import com.ded.necronmod.item.TinFoilHat;
import com.ded.necronmod.client.model.TinFoilHatModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class TinFoilHatRenderer extends GeoArmorRenderer<TinFoilHat> {
    public TinFoilHatRenderer() {
        super(new TinFoilHatModel());
    }
}
