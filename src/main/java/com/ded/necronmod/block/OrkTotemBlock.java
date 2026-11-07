package com.ded.necronmod.block;

import com.ded.necronmod.block.entity.OrkTotemBlockEntity;
import com.ded.necronmod.init.ModBlockEntities;
import com.ded.necronmod.util.InfectionManager;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OrkTotemBlock extends BaseEntityBlock {

    public static final MapCodec<OrkTotemBlock> CODEC = simpleCodec(OrkTotemBlock::new);

    public OrkTotemBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new OrkTotemBlockEntity(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    // При разрушении Тотема - моментальная очистка некоторой области вокруг!
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && level instanceof ServerLevel serverLevel) {
            cleanInfectionArea(serverLevel, pos, 26);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private void cleanInfectionArea(ServerLevel level, BlockPos center, int radius) {
        int radiusSqr = radius * radius;
        BlockPos.betweenClosedStream(center.offset(-radius, -16, -radius), center.offset(radius, 16, radius))
                .filter(p -> p.distSqr(center) <= radiusSqr)
                .forEach(p -> {
                    BlockState current = level.getBlockState(p);
                    BlockState clean = InfectionManager.getCleanVariant(current);
                    if (clean != null) {
                        level.setBlock(p, clean, 3);
                    }
                });
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.ORK_TOTEM.get(), OrkTotemBlockEntity::serverTick);
    }
}