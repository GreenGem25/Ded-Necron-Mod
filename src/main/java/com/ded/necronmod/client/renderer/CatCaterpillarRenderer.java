package com.ded.necronmod.client.renderer;

import com.ded.necronmod.client.model.CatCaterpillarModel;
import com.ded.necronmod.entity.CatCaterpillarEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CatCaterpillarRenderer extends GeoEntityRenderer<CatCaterpillarEntity> {

    public CatCaterpillarRenderer(EntityRendererProvider.Context renderManager) {
        // Передаем менеджер рендеринга и наш класс модели
        super(renderManager, new CatCaterpillarModel());

        // Размер тени под мобом на земле
        this.shadowRadius = 0.4F;
    }
}