package com.ded.necronmod.worldgen.structure;

import com.ded.necronmod.block.TowerTopBlock;
import com.ded.necronmod.init.ModBlocks;
import com.ded.necronmod.init.ModStructures;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CommunicationsTowerStructure extends Structure {
    public static final MapCodec<CommunicationsTowerStructure> CODEC = simpleCodec(CommunicationsTowerStructure::new);

    public CommunicationsTowerStructure(Structure.StructureSettings settings) {
        super(settings);
    }

    @Override
    protected @NotNull Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int x = chunkPos.getMiddleBlockX();
        int z = chunkPos.getMiddleBlockZ();
        int y = context.chunkGenerator().getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(), context.randomState());

        BlockPos spawnPos = new BlockPos(x, y, z);
        RandomSource random = context.random();
        int generatedHeight = random.nextIntBetweenInclusive(7, 15);

        return Optional.of(new Structure.GenerationStub(spawnPos, (builder) -> {
            builder.addPiece(new TowerPiece(spawnPos, generatedHeight));
        }));
    }

    @Override
    public @NotNull StructureType<?> type() {
        return ModStructures.COMMUNICATIONS_TOWER.get();
    }

    @Override
    public GenerationStep.@NotNull Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }

    public static class TowerPiece extends StructurePiece {
        private final int height;
        private final BlockPos startPos;

        public TowerPiece(BlockPos pos, int height) {
            super(
                    ModStructures.TOWER_PIECE_TYPE.get(),
                    0,
                    new BoundingBox(pos.getX() - 10, pos.getY() - 5, pos.getZ() - 10,
                            pos.getX() + 10, pos.getY() + height + 10, pos.getZ() + 10)
            );
            this.setOrientation(null);
            this.height = height;
            this.startPos = pos;
        }

        public TowerPiece(CompoundTag tag) {
            super(ModStructures.TOWER_PIECE_TYPE.get(), tag);
            this.height = tag.getInt("Height");
            this.startPos = new BlockPos(tag.getInt("StartX"), tag.getInt("StartY"), tag.getInt("StartZ"));
        }

        @Override
        protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context, CompoundTag tag) {
            tag.putInt("Height", this.height);
            tag.putInt("StartX", this.startPos.getX());
            tag.putInt("StartY", this.startPos.getY());
            tag.putInt("StartZ", this.startPos.getZ());
        }

        @Override
        public void postProcess(@NotNull WorldGenLevel level, @NotNull StructureManager structureManager,
                                @NotNull ChunkGenerator generator, @NotNull RandomSource random,
                                @NotNull BoundingBox box, @NotNull ChunkPos chunkPos, @NotNull BlockPos pos) {
            BlockState bodyBlock = ModBlocks.TOWER_BASE.get().defaultBlockState();

            Direction[] directions = { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST };
            Direction randomDirection = directions[random.nextInt(directions.length)];
            BlockState signalBlock = ModBlocks.TOWER_TOP.get().defaultBlockState().setValue(TowerTopBlock.FACING, randomDirection);

            for (int i = 0; i < this.height; i++) {
                BlockPos currentBlockPos = this.startPos.above(i);

                if (box.isInside(currentBlockPos)) {
                    level.setBlock(currentBlockPos, bodyBlock, 2);
                }
            }

            BlockPos antennaPos = this.startPos.above(this.height);
            if (box.isInside(antennaPos)) {
                level.setBlock(antennaPos, signalBlock, 3);
            }
        }
    }
}