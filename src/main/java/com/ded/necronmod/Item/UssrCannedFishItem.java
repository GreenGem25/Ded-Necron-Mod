package com.ded.necronmod.Item;

import com.ded.necronmod.init.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UssrCannedFishItem extends Item {

    public UssrCannedFishItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {

        tooltipComponents.add(Component.empty());

        tooltipComponents.add(Component.translatable("tooltip.necronmod.ussr_canned_fish_desc")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        ItemStack resultStack = super.finishUsingItem(stack, level, entity);

        if (!level.isClientSide()) {
            level.playSound(
                    null,
                    entity.getX(), entity.getY(), entity.getZ(),
                    ModSounds.USSR_CANNED_FISH_SOUND.get(),
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
        }

        return resultStack;
    }
}
