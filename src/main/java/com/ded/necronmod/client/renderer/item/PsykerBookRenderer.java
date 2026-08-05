package com.ded.necronmod.client.renderer.item;

import com.ded.necronmod.item.PsykerBook;
import com.ded.necronmod.client.model.PsykerBookModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PsykerBookRenderer extends GeoItemRenderer<PsykerBook> {
        public PsykerBookRenderer() {
            super(new PsykerBookModel());
        }
}
