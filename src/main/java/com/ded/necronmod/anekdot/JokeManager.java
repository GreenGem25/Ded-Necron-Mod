package com.ded.necronmod.anekdot;

import com.ded.necronmod.Item.PsykerBook;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.UUID;

public class JokeManager {

    private static UUID currentSpeaker = null;
    private static Long activeBookId = null;
    private static ItemStack activeBookStack = null;

    public static synchronized boolean tryStartJoke(ServerPlayer player, ItemStack bookStack) {
        ServerLevel level = player.serverLevel();
        long bookId = GeoItem.getOrAssignId(bookStack, level);

        if (currentSpeaker == null) {
            currentSpeaker = player.getUUID();
            activeBookId = bookId;
            activeBookStack = bookStack;
            triggerBookAnim(player, bookStack, "open_trigger");
            return true;
        }

        if (currentSpeaker.equals(player.getUUID()) && activeBookId != null && activeBookId.equals(bookId)) {
            PacketDistributor.sendToAllPlayers(new StopJokePayload());
            return true;
        }

        return false;
    }

    public static synchronized void stopJoke(ServerPlayer player, ItemStack overrideStack) {
        if (currentSpeaker != null && currentSpeaker.equals(player.getUUID())) {
            currentSpeaker = null;
            activeBookId = null;

            // Приоритет отдаем переданному stack, если он есть, иначе используем запомненный activeBookStack
            ItemStack stackToAnimate = overrideStack != null ? overrideStack : activeBookStack;

            if (stackToAnimate != null) {
                triggerBookAnim(player, stackToAnimate, "close_trigger");
            }

            activeBookStack = null;

            PacketDistributor.sendToAllPlayers(new StopJokePayload());
        }
    }

    public static synchronized boolean isActiveBook(ServerPlayer player, ItemStack stack) {
        if (currentSpeaker == null || activeBookId == null || !currentSpeaker.equals(player.getUUID())) {
            return false;
        }
        long bookId = GeoItem.getOrAssignId(stack, player.serverLevel());
        return activeBookId.equals(bookId);
    }

    private static void triggerBookAnim(ServerPlayer player, ItemStack stack, String trigger) {
        if (stack.getItem() instanceof PsykerBook book) {
            ServerLevel level = player.serverLevel();
            long instanceId = GeoItem.getOrAssignId(stack, level);
            book.triggerAnim(player, instanceId, "book_controller", trigger);
        }
    }

    public static synchronized boolean isSpeaker(ServerPlayer player) {
        return currentSpeaker != null && currentSpeaker.equals(player.getUUID());
    }

    public static synchronized boolean isPlaying() {
        return currentSpeaker != null;
    }
}