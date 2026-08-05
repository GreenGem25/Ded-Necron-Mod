package com.ded.necronmod.item;

import com.ded.necronmod.anekdot.JokeManager;
import com.ded.necronmod.anekdot.JokeParser;
import com.ded.necronmod.anekdot.PlayJokePayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PsykerBook extends Item implements GeoItem {
    public PsykerBook(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "book_controller", 0, state -> PlayState.CONTINUE)
                .triggerableAnim("open_trigger",
                        RawAnimation.begin().thenPlayAndHold("open"))

                .triggerableAnim("close_trigger",
                        RawAnimation.begin().thenPlay("close"))
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            if (!JokeManager.tryStartJoke(serverPlayer, itemStack)) {
                serverPlayer.sendSystemMessage(
                        Component.literal("§cКто-то уже рассказывает анекдот! Дождитесь окончания."),
                        true
                );
                return InteractionResultHolder.fail(itemStack);
            }

            player.getCooldowns().addCooldown(this, 200);
            player.sendSystemMessage(Component.literal("Ищем свежий анекдот..."));

            JokeParser.fetchAnekdotAsync().thenAccept(jokeText -> {
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.getServer().execute(() -> {
                        if (!JokeManager.isPlaying()) return;

                        Component message = Component.literal("§e[Книга анекдотов псайкера]:§f\n" + jokeText);
                        serverLevel.getServer().getPlayerList().broadcastSystemMessage(message, false);

                        PacketDistributor.sendToAllPlayers(new PlayJokePayload(jokeText));
                    });
                }
            });

            return InteractionResultHolder.pass(itemStack);
        }

        return InteractionResultHolder.pass(itemStack);
    }
}
