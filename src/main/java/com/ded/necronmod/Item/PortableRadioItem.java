package com.ded.necronmod.item;

import com.ded.necronmod.init.ModDataComponents;
import com.ded.necronmod.radio.RadioSettings;
import com.ded.necronmod.radio.client.RadioClient;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Переносное радио. Правый клик открывает тот же экран, что и у блока. Пока предмет где-то в инвентаре и включён,
 * он играет (владельцу всегда в ушах; в режиме «динамик» его ещё слышат окружающие игроки).
 * Настройки лежат в компоненте данных предмета.
 */
public class PortableRadioItem extends Item {
    public PortableRadioItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static RadioSettings settings(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.RADIO_SETTINGS.get(), RadioSettings.DEFAULT);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) RadioClient.openItemScreen(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        RadioSettings s = settings(stack);
        tooltip.add(Component.literal(s.playing() ? "Включено" : "Выключено"));
        tooltip.add(Component.literal(s.speaker() ? "Режим: динамик" : "Режим: наушники"));
    }
}
