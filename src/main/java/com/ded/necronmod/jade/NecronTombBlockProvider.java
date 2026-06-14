package com.ded.necronmod.jade;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum NecronTombBlockProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(
            ITooltip tooltip,
            BlockAccessor accessor,
            IPluginConfig config) {

        tooltip.add(
                Component.translatable(
                        "tooltip.necronmod.chugun_saw_required"
                )
        );

    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath(
                DedNecronMod.MODID,
                "chugun_saw"
        );
    }
}
