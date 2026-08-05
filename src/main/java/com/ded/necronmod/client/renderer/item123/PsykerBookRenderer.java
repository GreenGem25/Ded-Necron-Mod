package com.ded.necronmod.client.renderer.item123;

import com.ded.necronmod.item123.PsykerBook;
import com.ded.necronmod.client.model.PsykerBookModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PsykerBookRenderer extends GeoItemRenderer<PsykerBook> {
        public PsykerBookRenderer() {
            super(new PsykerBookModel());
        }
}
