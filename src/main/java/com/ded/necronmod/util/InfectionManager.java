package com.ded.necronmod.util;

import com.ded.necronmod.init.ModBlocks;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class InfectionManager {

    private static final BiMap<Block, Block> INFECTION_MAP = HashBiMap.create();

    public static void init() {
        INFECTION_MAP.put(Blocks.DIRT, ModBlocks.INFECTED_DIRT.get());
        INFECTION_MAP.put(Blocks.GRASS_BLOCK, ModBlocks.INFECTED_GRASS_BLOCK.get());
        INFECTION_MAP.put(Blocks.STONE, ModBlocks.INFECTED_STONE.get());
    }

    public static @Nullable BlockState getInfectedVariant(BlockState original) {
        Block infected = INFECTION_MAP.get(original.getBlock());
        return infected != null ? infected.defaultBlockState() : null;
    }

    public static @Nullable BlockState getCleanVariant(BlockState infected) {
        Block clean = INFECTION_MAP.inverse().get(infected.getBlock());
        return clean != null ? clean.defaultBlockState() : null;
    }

    public static boolean isInfected(BlockState state) {
        return INFECTION_MAP.containsValue(state.getBlock());
    }
}