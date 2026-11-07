package com.ded.necronmod.event;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.anekdot.*;
import com.ded.necronmod.entity.CatCaterpillarEntity;
import com.ded.necronmod.entity.CigarFlyEntity;
import com.ded.necronmod.entity.TarakanEntity;
import com.ded.necronmod.init.ModDamageTypes;
import com.ded.necronmod.init.ModEffects;
import com.ded.necronmod.init.ModEntities;
import com.ded.necronmod.init.ModItems;
import com.ded.necronmod.util.FrameAnimationManager;
import com.ded.necronmod.util.PlayCigaretteAnimationPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Objects;

@EventBusSubscriber(modid = DedNecronMod.MODID)
public class ModServerEvents {

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(
                ModEntities.CAT_CATERPILLAR.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );

        event.register(
                ModEntities.TARAKAN.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TarakanEntity::canSpawn,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );

        event.register(
                ModEntities.CIGARFLY.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
    }

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
            ItemEntity itemEntity = event.getEntity();
            ItemStack tossedStack = itemEntity.getItem();

            if (tossedStack.is(ModItems.PSYKER_BOOK.get())) {
                JokeManager.stopJoke(serverPlayer, tossedStack);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            JokeManager.stopJoke(serverPlayer, null);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ItemStack mainHand = serverPlayer.getMainHandItem();
            ItemStack offHand = serverPlayer.getOffhandItem();

            if (JokeManager.isActiveBook(serverPlayer, mainHand)) {
                JokeManager.stopJoke(serverPlayer, mainHand);
            } else if (JokeManager.isActiveBook(serverPlayer, offHand)) {
                JokeManager.stopJoke(serverPlayer, offHand);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (JokeManager.isSpeaker(serverPlayer)) {
                ItemStack mainHand = serverPlayer.getMainHandItem();
                ItemStack offHand = serverPlayer.getOffhandItem();

                boolean holdsActiveBook = JokeManager.isActiveBook(serverPlayer, mainHand)
                        || JokeManager.isActiveBook(serverPlayer, offHand);

                if (!holdsActiveBook) {
                    JokeManager.stopJoke(serverPlayer, null);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (JokeManager.isSpeaker(serverPlayer)) {
                JokeManager.stopJoke(serverPlayer, null);
            }
        }
    }

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.CAT_CATERPILLAR.get(), CatCaterpillarEntity.createAttributes().build());
        event.put(ModEntities.CIGARFLY.get(), CigarFlyEntity.createAttributes().build());
        event.put(ModEntities.TARAKAN.get(), TarakanEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                PlayJokePayload.TYPE,
                PlayJokePayload.STREAM_CODEC,
                ClientJokeHandler::handleJokePayload
        );

        registrar.playToClient(
                StopJokePayload.TYPE,
                StopJokePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(NativeTTS::stopSpeech)
        );

        registrar.playToClient(
                PlayCigaretteAnimationPayload.TYPE,
                PlayCigaretteAnimationPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(FrameAnimationManager::play)
        );
    }
}
