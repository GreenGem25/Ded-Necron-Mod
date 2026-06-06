package com.ded.necronmod.client.renderer;

import com.ded.necronmod.Item.PoopHelmetItem;
import com.ded.necronmod.client.model.PoopHelmetModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class PoopHelmetRenderer extends GeoArmorRenderer<PoopHelmetItem> {
    public PoopHelmetRenderer() {
        super(new PoopHelmetModel());
    }
}
