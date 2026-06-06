package com.ded.necronmod.Item;

import com.ded.necronmod.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NecronArmorItem extends ArmorItem {
    public NecronArmorItem(Holder<ArmorMaterial> material, ArmorItem.Type type, Item.Properties properties) {
        super(material, type, properties);
    }

    @Override
    public int getMaxDamage(@NotNull ItemStack stack) {
        return Config.NECRO_ARMOR_DURABILITY.get();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {

        tooltipComponents.add(Component.empty());

        tooltipComponents.add(Component.translatable("tooltip.necronmod.necro_armor_set_bonus")
                .withStyle(ChatFormatting.GOLD));

        int percent_reduction = (int) Math.round((1.0F - Config.NECRO_ARMOR_SET_DAMAGE_REDUCE.get()) * 100);

        tooltipComponents.add(Component.translatable("tooltip.necronmod.necro_armor_effect_desc", percent_reduction)
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
