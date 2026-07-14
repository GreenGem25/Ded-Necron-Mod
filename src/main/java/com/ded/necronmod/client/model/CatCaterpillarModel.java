package com.ded.necronmod.client.model;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.entity.CatCaterpillarEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.loading.json.raw.Bone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CatCaterpillarModel extends GeoModel<CatCaterpillarEntity> {

    @Override
    public ResourceLocation getModelResource(CatCaterpillarEntity animatable) {
        // Путь к файлу модели из Blockbench (src/main/resources/assets/necronmod/geo/cat_caterpillar.geo.json)
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "geo/entity/cat_caterpillar.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CatCaterpillarEntity animatable) {
        // Путь к текстуре (src/main/resources/assets/necronmod/textures/entity/cat_caterpillar.png)
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "textures/entity/cat_caterpillar.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CatCaterpillarEntity animatable) {
        // Путь к файлу анимаций из Blockbench (src/main/resources/assets/necronmod/animations/cat_caterpillar.animation.json)
        return ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "animations/entity/cat_caterpillar.animation.json");
    }

    @Override
    public void setCustomAnimations(CatCaterpillarEntity animatable, long instanceId, AnimationState<CatCaterpillarEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        // Получаем данные о вращении головы из движка игры
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        // Получаем кость головы по её ТОЧНОМУ имени из Blockbench
        // Замени "head", если в Blockbench твоя папка с головой называется иначе (например, "cat_head")
        GeoBone head = this.getAnimationProcessor().getBone("head");

        if (head != null) {
            // Применяем углы наклона (вверх-вниз) и поворота (влево-вправо)
            head.setRotX(-entityData.headPitch() * ((float) Math.PI / 180F));
            head.setRotY(entityData.netHeadYaw() * ((float) Math.PI / 180F));
        }
    }
}