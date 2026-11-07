package com.ded.necronmod.radio;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.radio.data.RadioCountry;
import com.ded.necronmod.radio.data.RadioData;
import com.ded.necronmod.radio.data.RadioStation;
import com.ded.necronmod.radio.ui.RadioScreen;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

/**
 * Отладка звука без блока:
 *   /radiotest NL 7            блок в точке, где вы стоите (моно, с затуханием)
 *   /radiotest hp NL 7 [0.5]   собственное радио (стерео, прямо в ушах, глушит остальные)
 *   /radiotest screen          экран радио как у блока (звук играет в точке, где вы стоите)
 *   /radiotest screen portable экран переносного радио (звук в ушах, есть переключатель наушники/динамик)
 *   /radiotest stop            остановить всё
 * Позже команду можно удалить.
 */
@EventBusSubscriber(modid = DedNecronMod.MODID, value = Dist.CLIENT)
public final class RadioDebugCommand {
    private RadioDebugCommand() {}

    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("radiotest")
                .then(Commands.literal("stop").executes(ctx -> { RadioSoundManager.stopAll(); return 1; }))
                .then(Commands.literal("screen")
                        .executes(ctx -> openScreen(false))
                        .then(Commands.literal("portable").executes(ctx -> openScreen(true))))
                .then(Commands.literal("hp").then(args(true)))
                .then(args(false)));
    }

    private static com.mojang.brigadier.builder.ArgumentBuilder<CommandSourceStack, ?> args(boolean headphones) {
        return Commands.argument("country", StringArgumentType.word())
                .then(Commands.argument("slot", IntegerArgumentType.integer(0, 63))
                        .executes(ctx -> play(ctx, 0.8f, headphones))
                        .then(Commands.argument("volume", FloatArgumentType.floatArg(0f, 1f))
                                .executes(ctx -> play(ctx, FloatArgumentType.getFloat(ctx, "volume"), headphones))));
    }

    /** Последние настройки экрана, чтобы при повторном открытии не начинать заново (отдельно для блока и портативного). */
    private static RadioSettings lastBlock, lastPortable;

    private static int openScreen(boolean portable) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return 0;
        if (RadioData.countries().isEmpty()) {
            mc.player.displayClientMessage(Component.literal("stations.json не загружен"), false);
            return 0;
        }
        BlockPos pos = mc.player.blockPosition();
        RadioSettings initial = (portable ? lastPortable : lastBlock);
        if (initial == null) initial = new RadioSettings(RadioData.countries().getFirst().code(), 0, 0.7f, false, false);

        // Те же настройки, что в игре пришли бы с блока или предмета, здесь сразу применяем к звуку.
        java.util.function.Consumer<RadioSettings> apply = s -> {
            if (portable) lastPortable = s; else lastBlock = s;
            RadioCountry c = RadioData.byCode(s.country());
            RadioStation st = c == null ? null : c.stationAt(s.slot());
            if (portable) RadioSoundManager.updateOwnRadio(st, s.volume(), s.playing());
            else RadioSoundManager.update(pos, st, s.volume(), s.playing());
        };
        final RadioSettings start = initial;
        // Команда выполняется из окна чата, которое закроется ПОСЛЕ неё и сбросит наш экран: открываем на следующем тике.
        mc.tell(() -> mc.setScreen(new RadioScreen(start, portable, apply)));
        return 1;
    }

    private static int play(CommandContext<CommandSourceStack> ctx, float volume, boolean headphones) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return 0;

        String code = StringArgumentType.getString(ctx, "country").toUpperCase(Locale.ROOT);
        int slot = IntegerArgumentType.getInteger(ctx, "slot");
        RadioCountry country = RadioData.byCode(code);
        if (country == null) {
            mc.player.displayClientMessage(Component.literal("Нет страны " + code), false);
            return 0;
        }

        RadioStation station = country.stationAt(slot);
        RadioSoundManager.stopAll();
        if (headphones) RadioSoundManager.updateOwnRadio(station, volume, true);
        else RadioSoundManager.update(mc.player.blockPosition(), station, volume, true);
        mc.player.displayClientMessage(Component.literal(station == null
                ? code + " #" + slot + ": нет сигнала (шум)"
                : (headphones ? "[своё радио] " : "[блок] ") + code + " #" + slot + ": " + station.name() + " " + station.urls()), false);
        return 1;
    }
}
