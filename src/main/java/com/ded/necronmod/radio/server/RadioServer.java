package com.ded.necronmod.radio.server;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.init.ModDataComponents;
import com.ded.necronmod.radio.RadioInventory;
import com.ded.necronmod.radio.RadioSettings;
import com.ded.necronmod.block.entity.RadioBlockEntity;
import com.ded.necronmod.item.PortableRadioItem;
import com.ded.necronmod.radio.net.PlayerRadioStatePayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Серверная часть радио:
 *  1. принимает настройки от клиентов (блок и предмет) и ПРОВЕРЯЕТ их: сервер клиенту не доверяет;
 *  2. следит за «динамиками» переносных радио и рассылает их состояние игрокам вокруг.
 * Звука здесь нет совсем: он целиком на клиентах.
 */
@EventBusSubscriber(modid = DedNecronMod.MODID)
public final class RadioServer {
    /** Игрок настраивает радио с экрана, а экран остаётся открытым, даже если отойти: даём запас дальше обычной досягаемости. */
    private static final double MAX_BLOCK_DISTANCE_SQ = 8.0 * 8.0;
    private static final int SCAN_INTERVAL_TICKS = 5;

    private static final RateLimiter RATE = new RateLimiter(10, 8);
    private static final SpeakerTracker SPEAKERS = new SpeakerTracker();

    private RadioServer() {}

    // ------------------------------------------------------------------ приём настроек

    public static void applyBlockSettings(ServerPlayer player, BlockPos pos, RadioSettings requested) {
        Level level = player.level();
        if (!RATE.allow(player.getUUID(), level.getGameTime())) return;
        if (!level.isLoaded(pos)) return; // не загружаем чанки по просьбе клиента
        if (player.distanceToSqr(Vec3.atCenterOf(pos)) > MAX_BLOCK_DISTANCE_SQ) return;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RadioBlockEntity radio) radio.applySettings(requested.sanitized(false));
    }

    public static void applyItemSettings(ServerPlayer player, boolean offhand, RadioSettings requested) {
        if (!RATE.allow(player.getUUID(), player.level().getGameTime())) return;
        ItemStack stack = player.getItemInHand(offhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if (!(stack.getItem() instanceof PortableRadioItem)) return; // радио нет в руке (успели убрать)
        stack.set(ModDataComponents.RADIO_SETTINGS.get(), requested.sanitized(true));
    }

    // ------------------------------------------------------------------ динамики

    /** Раз в несколько тиков смотрим, играет ли у игрока динамик, и рассылаем окружающим изменения. */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.tickCount % SCAN_INTERVAL_TICKS != 0) return;

        ItemStack active = RadioInventory.findActive(player);
        RadioSettings speaker = null;
        if (!active.isEmpty()) {
            RadioSettings s = PortableRadioItem.settings(active).sanitized(true);
            if (s.speaker()) speaker = s;
        }
        RadioSettings toSend = SPEAKERS.update(player.getUUID(), speaker);
        if (toSend != null) {
            PacketDistributor.sendToPlayersTrackingEntity(player, new PlayerRadioStatePayload(player.getUUID(), toSend));
        }
    }

    /** Игрок появился в зоне видимости другого: сообщаем ему, что у того играет динамик. */
    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer target && event.getEntity() instanceof ServerPlayer watcher) {
            RadioSettings state = SPEAKERS.get(target.getUUID());
            if (state != null) PacketDistributor.sendToPlayer(watcher, new PlayerRadioStatePayload(target.getUUID(), state));
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SPEAKERS.forget(player.getUUID());
            RATE.forget(player.getUUID());
        }
    }
}
