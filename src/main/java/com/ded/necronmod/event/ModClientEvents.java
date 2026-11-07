package com.ded.necronmod.event;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.client.renderer.entity.CigarFlyRenderer;
import com.ded.necronmod.client.renderer.item.PsykerBookRenderer;
import com.ded.necronmod.client.renderer.item.WaterGunRenderer;
import com.ded.necronmod.client.renderer.armor.TinFoilHatRenderer;
import com.ded.necronmod.client.renderer.item.NecronStaffRenderer;
import com.ded.necronmod.client.renderer.armor.PoopHelmetRenderer;
import com.ded.necronmod.client.renderer.block.TowerTopRenderer;
import com.ded.necronmod.client.renderer.entity.CatCaterpillarRenderer;
import com.ded.necronmod.client.renderer.entity.TarakanRenderer;
import com.ded.necronmod.init.*;
import com.ded.necronmod.radio.RadioStationsLoader;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = DedNecronMod.MODID, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new RadioStationsLoader());
    }

    @SubscribeEvent
    public static void registerItemProperties(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(
                ModItems.TESSERACT_LABYRINTH.get(),
                ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "filled"),
                (stack, level, entity, seed) ->
                        stack.has(ModDataComponents.CAPTURED_MOB.get()) ? 1.0F : 0.0F
        ));
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
            private PsykerBookRenderer renderer;

            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new PsykerBookRenderer();
                }
                return renderer;
            }
        }, ModItems.PSYKER_BOOK.get());

        event.registerItem(new IClientItemExtensions() {
            private WaterGunRenderer renderer;

            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new WaterGunRenderer();
                }
                return renderer;
            }
        }, ModItems.WATER_GUN.get());

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
        event.registerBlockEntityRenderer(ModBlockEntities.TOWER_TOP.get(), TowerTopRenderer::new);

        event.registerEntityRenderer(ModEntities.CAT_CATERPILLAR.get(), CatCaterpillarRenderer::new);
        event.registerEntityRenderer(ModEntities.TARAKAN.get(), TarakanRenderer::new);
        event.registerEntityRenderer(ModEntities.CIGARFLY.get(), CigarFlyRenderer::new);
        event.registerEntityRenderer(ModEntities.WATER_PROJECTILE.get(), NoopRenderer::new);
    }

    @SubscribeEvent
    public static void onPlayerRender(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        HumanoidModel<?> playerModel = event.getRenderer().getModel();

        // Проверяем правую руку
        ItemStack mainHandStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (mainHandStack.is(ModItems.WATER_GUN.get())) {
            // Задаем позу вытянутой руки (как с арбалетом или при прицеливании)
            playerModel.rightArmPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
        }

        // Проверяем левую руку (если пистолет взяли в левую)
        ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (offHandStack.is(ModItems.WATER_GUN.get())) {
            playerModel.leftArmPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
        }
    }
}
