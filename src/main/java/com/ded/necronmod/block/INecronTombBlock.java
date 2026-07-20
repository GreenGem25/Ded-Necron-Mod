package com.ded.necronmod.block;

import com.ded.necronmod.Item.ChugunSaw;
import com.ded.necronmod.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface INecronTombBlock {

    default boolean handleCanHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        ItemStack stack = player.getMainHandItem();
        return stack.getItem() instanceof ChugunSaw;
    }

    default float handleGetDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        ItemStack mainHandItem = player.getMainHandItem();

        if (mainHandItem.is(ModItems.CHUGUN_SAW.get())) {
            float baseProgress = 1.0F / 20.0F;

            player.level();
            var registryAccess = player.level().registryAccess();
            var enchantmentRegistry = registryAccess.registryOrThrow(Registries.ENCHANTMENT);

            int efficiencyLevel = EnchantmentHelper.getTagEnchantmentLevel(
                    enchantmentRegistry.getHolderOrThrow(Enchantments.EFFICIENCY),
                    mainHandItem
            );

            if (efficiencyLevel > 0) {
                float bonus = (float) (efficiencyLevel * efficiencyLevel + 1) / 20.0F;
                return baseProgress + bonus;
            }

            return baseProgress;
        }

        return 0F;
    }

    default boolean handleCanEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        return false;
    }
}