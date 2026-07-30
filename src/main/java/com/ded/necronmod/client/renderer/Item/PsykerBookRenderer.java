package com.ded.necronmod.client.renderer.Item;

import com.ded.necronmod.Item.PsykerBook;
import com.ded.necronmod.client.model.PsykerBookModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PsykerBookRenderer extends GeoItemRenderer<PsykerBook> {
        public PsykerBookRenderer() {
            super(new PsykerBookModel());
        }
}
