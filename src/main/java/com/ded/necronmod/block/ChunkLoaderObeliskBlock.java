package com.ded.necronmod.block;

import com.ded.necronmod.block.entity.ChunkLoaderObeliskBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ChunkLoaderObeliskBlock extends BaseEntityBlock {

    // 1. Создаем кодек для регистрации блока в системе сериализации Mojang
    public static final MapCodec<ChunkLoaderObeliskBlock> CODEC = simpleCodec(ChunkLoaderObeliskBlock::new);

    public ChunkLoaderObeliskBlock(Properties properties) {
        super(properties);
    }

    // 2. Реализуем обязательный абстрактный метод codec()
    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ChunkLoaderObeliskBlockEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ChunkLoaderObeliskBlockEntity obelisk) {
                obelisk.releaseChunk();
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}