package com.ded.necronmod.radio.client;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.init.ModDataComponents;
import com.ded.necronmod.radio.RadioInventory;
import com.ded.necronmod.radio.RadioSettings;
import com.ded.necronmod.radio.RadioSoundManager;
import com.ded.necronmod.block.entity.RadioBlockEntity;
import com.ded.necronmod.radio.data.RadioCountry;
import com.ded.necronmod.radio.data.RadioData;
import com.ded.necronmod.radio.data.RadioStation;
import com.ded.necronmod.item.PortableRadioItem;
import com.ded.necronmod.radio.net.BlockSettingsPayload;
import com.ded.necronmod.radio.net.ItemSettingsPayload;
import com.ded.necronmod.radio.ui.RadioScreen;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Клиентский «мост» радио: единственное место, где общий код (блок, предмет, пакеты) касается клиентских классов.
 * Блок/предмет/пакеты вызывают эти методы только на клиенте, поэтому на выделенном сервере класс не загружается.
 * Здесь настройки превращаются в звук (через RadioSoundManager) и открываются экраны.
 */
@EventBusSubscriber(modid = DedNecronMod.MODID, value = Dist.CLIENT)
public final class RadioClient {
    private RadioClient() {}

    // ------------------------------------------------------------------ звук

    /** Своё переносное радио: смотрим в инвентарь каждый тик (это дёшево, менеджер сам замечает смену станции). */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        ItemStack active = RadioInventory.findActive(mc.player);
        if (active.isEmpty()) {
            RadioSoundManager.updateOwnRadio(null, 0f, false);
            return;
        }
        RadioSettings s = PortableRadioItem.settings(active);
        RadioSoundManager.updateOwnRadio(stationFor(s), s.volume(), true);
    }

    /** Радио-блок получил или изменил настройки. */
    public static void blockChanged(BlockPos pos, RadioSettings s) {
        RadioSoundManager.update(pos, stationFor(s), s.volume(), s.playing());
    }

    public static void blockRemoved(BlockPos pos) {
        RadioSoundManager.remove(pos);
    }

    /** Сервер сообщил состояние «динамика» другого игрока. */
    public static void playerState(UUID playerId, RadioSettings s) {
        if (s.playing() && s.speaker()) RadioSoundManager.updatePlayer(playerId, stationFor(s), s.volume(), true);
        else RadioSoundManager.removePlayer(playerId);
    }

    /** Станция по настройкам или null (нет такой страны или пустая позиция = шум). */
    @Nullable
    public static RadioStation stationFor(RadioSettings s) {
        RadioCountry c = RadioData.byCode(s.country());
        return c == null ? null : c.stationAt(s.slot());
    }

    // ------------------------------------------------------------------ экраны

    public static void openBlockScreen(BlockPos pos) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || !(mc.level.getBlockEntity(pos) instanceof RadioBlockEntity be)) return;

        mc.setScreen(new RadioScreen(be.getSettings(), false, s -> {
            // Сразу применяем у себя, чтобы ручка и звук откликались без задержки на пинг; сервер подтвердит то же самое.
            if (mc.level != null && mc.level.getBlockEntity(pos) instanceof RadioBlockEntity b) b.setSettingsClient(s);
            blockChanged(pos, s);
            PacketDistributor.sendToServer(new BlockSettingsPayload(pos, s));
        }));
    }

    public static void openItemScreen(InteractionHand hand) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof PortableRadioItem)) return;

        boolean offhand = hand == InteractionHand.OFF_HAND;
        mc.setScreen(new RadioScreen(PortableRadioItem.settings(stack), true, s -> {
            Player p = Minecraft.getInstance().player;
            if (p == null) return;
            ItemStack current = p.getItemInHand(hand); // стек мог замениться при синхронизации, берём свежий
            if (current.getItem() instanceof PortableRadioItem) current.set(ModDataComponents.RADIO_SETTINGS.get(), s);
            PacketDistributor.sendToServer(new ItemSettingsPayload(offhand, s));
        }));
    }
}
