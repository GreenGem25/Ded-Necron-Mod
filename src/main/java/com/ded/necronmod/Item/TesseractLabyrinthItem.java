package com.ded.necronmod.Item;

import com.ded.necronmod.init.ModDataComponentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TesseractLabyrinthItem extends Item {
    public TesseractLabyrinthItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player player,
                                                           @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        Level level = player.level();

        if (target instanceof Player || target.getType().is(Tags.EntityTypes.CAPTURING_NOT_SUPPORTED) ||
                stack.has(ModDataComponentTypes.CAPTURED_MOB.get()) || !target.isAlive()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            CompoundTag mobData = new CompoundTag();

            mobData.putString("id", EntityType.getKey(target.getType()).toString());

            target.saveWithoutId(mobData);

            stack.set(ModDataComponentTypes.CAPTURED_MOB.get(), mobData);

            player.setItemInHand(hand, stack);

            target.discard();

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.2F);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();

        if (!stack.has(ModDataComponentTypes.CAPTURED_MOB.get())) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            BlockPos clickedPos = context.getClickedPos();
            Direction clickedFace = context.getClickedFace();

            BlockPos spawnPos = clickedPos.relative(clickedFace);

            CompoundTag mobData = stack.get(ModDataComponentTypes.CAPTURED_MOB.get());

            if (mobData != null && mobData.contains("id")) {
                Entity entity = EntityType.loadEntityRecursive(mobData, serverLevel, (spawnedEntity) -> {
                    spawnedEntity.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                            context.getRotation(), 0.0F);
                    return spawnedEntity;
                });

                if (entity != null) {
                    serverLevel.addFreshEntityWithPassengers(entity);

                    stack.remove(ModDataComponentTypes.CAPTURED_MOB.get());

                    level.playSound(null, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(),
                            SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        if (stack.has(ModDataComponentTypes.CAPTURED_MOB.get())) {
            CompoundTag mobData = stack.get(ModDataComponentTypes.CAPTURED_MOB.get());
            if (mobData != null && mobData.contains("id")) {
                String mobId = mobData.getString("id").replace("minecraft:", "").toUpperCase();

                tooltipComponents.add(Component.literal("§cСостояние: §7Заполнен"));
                tooltipComponents.add(Component.literal("§6Внутри лабиринта: §e" + mobId));

                if (mobData.contains("CustomName")) {
                    tooltipComponents.add(Component.literal("§dИмя существа: §f" + mobData.getString("CustomName")));
                }
                return;
            }
        }
        tooltipComponents.add(Component.literal("§aСостояние: §7Пуст"));
        tooltipComponents.add(Component.literal("§7Используйте на мобе, чтобы поймать его."));
    }

}
