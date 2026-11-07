package com.ded.necronmod.util;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.init.ModSounds;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = DedNecronMod.MODID, value = Dist.CLIENT)
public class FrameAnimationManager {

    private static final String FRAME_PATH = "textures/gui/cigarette_video/frame_%03d.png"; // %04d для frame_0000.png
    public static final int TOTAL_FRAMES = 262; // Общее количество кадров
    public static final int TICKS_PER_FRAME = 1; // 1 тик на кадр = 20 FPS

    private static boolean isPlaying = false;
    private static int currentFrame = 0;
    private static int tickCounter = 0;

    private static boolean hideHand = false;
    private static int handHideTicks = 0;

    private static boolean deathHandled = false;

    public static void play() {
        isPlaying = true;
        currentFrame = 0;
        tickCounter = 0;
        hideHand = true;
        handHideTicks = TOTAL_FRAMES * TICKS_PER_FRAME;
    }

    public static void stop() {
        isPlaying = false;
        hideHand = false;
        handHideTicks = 0;
    }

    // Вызывайте этот метод из ClientTickEvent
    public static void tick() {
        if (!isPlaying) return;

        tickCounter++;
        if (tickCounter >= TICKS_PER_FRAME) {
            tickCounter = 0;
            currentFrame++;
            if (currentFrame >= TOTAL_FRAMES) {
                // Достигли конца — останавливаемся или зацикливаем
                stop();
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        if (player.isDeadOrDying()) {
            if (!deathHandled) {
                stop();
                Minecraft.getInstance().getSoundManager().stop(
                        ModSounds.BHOP_MUSIC.getId(),
                        SoundSource.PLAYERS
                );
                deathHandled = true;
            }
            return;
        }

        deathHandled = false;

        tick();

        if (handHideTicks > 0) {
            handHideTicks--;
            if (handHideTicks == 0) {
                hideHand = false;
            }
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (hideHand) {
            event.setCanceled(true);
        }
    }

    // --- Регистрация слоя поверх всех остальных ---
    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
                ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "cigarette_video"),
                new CigaretteVideoLayer()
        );
    }

    // --- Сам слой, который и будет рисовать видео ---
    private static class CigaretteVideoLayer implements LayeredDraw.Layer {
        @Override
        public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
            if (!isPlaying) return;

            ResourceLocation frameTexture = ResourceLocation.fromNamespaceAndPath(
                    DedNecronMod.MODID,
                    String.format(FRAME_PATH, currentFrame)
            );

            int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO
            );
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            guiGraphics.blit(frameTexture, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}