package com.ded.necronmod.radio.net;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.radio.client.RadioClient;
import com.ded.necronmod.radio.server.RadioServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Регистрация пакетов. Версию канала "1" поставьте такую же, как у остальных ваших пакетов, если они есть
 * (одинаковые версии в нескольких registrar()-вызовах допустимы).
 * Обработчики выполняются в главном потоке сервера/клиента (поведение по умолчанию).
 */
@EventBusSubscriber(modid = DedNecronMod.MODID)
public final class RadioNetwork {
    private RadioNetwork() {}

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(BlockSettingsPayload.TYPE, BlockSettingsPayload.STREAM_CODEC, RadioNetwork::onBlockSettings);
        registrar.playToServer(ItemSettingsPayload.TYPE, ItemSettingsPayload.STREAM_CODEC, RadioNetwork::onItemSettings);
        registrar.playToClient(PlayerRadioStatePayload.TYPE, PlayerRadioStatePayload.STREAM_CODEC, RadioNetwork::onPlayerState);
    }

    private static void onBlockSettings(BlockSettingsPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) RadioServer.applyBlockSettings(player, payload.pos(), payload.settings());
    }

    private static void onItemSettings(ItemSettingsPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) RadioServer.applyItemSettings(player, payload.offhand(), payload.settings());
    }

    /** Выполняется только на клиенте; класс RadioClient на выделенном сервере не загружается. */
    private static void onPlayerState(PlayerRadioStatePayload payload, IPayloadContext context) {
        RadioClient.playerState(payload.player(), payload.settings());
    }
}
