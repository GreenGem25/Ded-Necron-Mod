package com.ded.necronmod.event;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.client.renderer.armor.TinFoilHatRenderer;
import com.ded.necronmod.client.renderer.block.MonolithSproutRenderer;
import com.ded.necronmod.client.renderer.Item.NecronStaffRenderer;
import com.ded.necronmod.client.renderer.armor.PoopHelmetRenderer;
import com.ded.necronmod.client.renderer.block.TowerBaseRenderer;
import com.ded.necronmod.client.renderer.block.TowerTopRenderer;
import com.ded.necronmod.client.renderer.entity.CatCaterpillarRenderer;
import com.ded.necronmod.client.renderer.entity.TarakanRenderer;
import com.ded.necronmod.init.*;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = DedNecronMod.MODID, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void registerItemProperties(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    ModItems.TESSERACT_LABYRINTH.get(),
                    ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "filled"),
                    (stack, level, entity, seed) ->
                            stack.has(ModDataComponentTypes.CAPTURED_MOB.get()) ? 1.0F : 0.0F
            );
        });
    }

    @SubscribeEvent
    public static void registerItemRenderer(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private NecronStaffRenderer renderer;

            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new NecronStaffRenderer();
                }
                return renderer;
            }
        }, ModItems.NECRON_STAFF.get());

        event.registerItem(new IClientItemExtensions() {
            private PoopHelmetRenderer renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(@NotNull LivingEntity entity,
                                                                   @NotNull ItemStack itemStack,
                                                                   @NotNull EquipmentSlot equipmentSlot,
                                                                   @NotNull HumanoidModel<?> original) {
                if (renderer == null) {
                    renderer = new PoopHelmetRenderer();
                }

                renderer.prepForRender(entity, itemStack, equipmentSlot, original,
                        null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);

                return renderer;
            }
        }, ModItems.POOP_HELMET.get());

        event.registerItem(new IClientItemExtensions() {
            private TinFoilHatRenderer renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(@NotNull LivingEntity entity,
                                                                   @NotNull ItemStack itemStack,
                                                                   @NotNull EquipmentSlot equipmentSlot,
                                                                   @NotNull HumanoidModel<?> original) {
                if (renderer == null) {
                    renderer = new TinFoilHatRenderer();
                }

                renderer.prepForRender(entity, itemStack, equipmentSlot, original,
                        null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);

                return renderer;
            }
        }, ModItems.TIN_FOIL_HAT.get());
    }

    @SubscribeEvent
    public static void registerEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.MONOLITH_SPROUT.get(), MonolithSproutRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.TOWER_BASE.get(), TowerBaseRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.TOWER_TOP.get(), TowerTopRenderer::new);

        event.registerEntityRenderer(ModEntities.CAT_CATERPILLAR.get(), CatCaterpillarRenderer::new);
        event.registerEntityRenderer(ModEntities.TARAKAN.get(), TarakanRenderer::new);
    }
}
