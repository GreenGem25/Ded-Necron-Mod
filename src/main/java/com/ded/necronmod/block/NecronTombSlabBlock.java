package com.ded.necronmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class NecronTombSlabBlock extends SlabBlock implements INecronTombBlock {
    public NecronTombSlabBlock(Properties properties) { super(properties); }

    @Override
    public boolean canHarvestBlock(@NotNull BlockState s, @NotNull BlockGetter l, @NotNull BlockPos p, @NotNull Player pl) { return handleCanHarvestBlock(s, l, p, pl); }
    @Override
    public float getDestroyProgress(@NotNull BlockState s, @NotNull Player pl, @NotNull BlockGetter l, @NotNull BlockPos p) { return handleGetDestroyProgress(s, pl, l, p); }
    @Override
    public boolean canEntityDestroy(@NotNull BlockState s, @NotNull BlockGetter l, @NotNull BlockPos p, @NotNull Entity e) { return handleCanEntityDestroy(s, l, p, e); }
}