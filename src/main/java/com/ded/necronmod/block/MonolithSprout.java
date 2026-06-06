package com.ded.necronmod.block;

import com.ded.necronmod.block.entity.MonolithSproutBlockEntity;
import com.ded.necronmod.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class MonolithSprout extends CropBlock implements EntityBlock {
    public MonolithSprout() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noOcclusion()
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
        );
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return state.is(Blocks.FARMLAND);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, net.minecraft.world.level.@NotNull LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        return this.mayPlaceOn(level.getBlockState(blockpos), level, blockpos);
    }

    @Override
    protected @NotNull ItemLike getBaseSeedId() {
        return ModItems.MONOLITH_SEEDS.get();
    }

    @Override
    public int getMaxAge() {
        return 7;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MonolithSproutBlockEntity(pos, state);
    }

    @Override
    public net.minecraft.world.level.block.@NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return net.minecraft.world.level.block.RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
